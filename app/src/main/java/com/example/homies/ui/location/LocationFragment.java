package com.example.homies.ui.location;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.homies.MyApplication;
import com.example.homies.R;
import com.example.homies.model.Location;
import com.example.homies.model.viewmodel.HouseholdViewModel;
import com.example.homies.model.viewmodel.LocationViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.ViewAnnotationAnchor;
import com.mapbox.maps.ViewAnnotationOptions;
import com.mapbox.maps.extension.observable.eventdata.StyleDataLoadedEventData;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.delegates.listeners.OnStyleDataLoadedListener;
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import timber.log.Timber;

/*
 * Adapted from Dr.Champion's TicTacToe app.
 */
public class LocationFragment extends Fragment implements PermissionsListener, OnIndicatorBearingChangedListener, OnIndicatorPositionChangedListener,
        OnMoveListener, OnStyleDataLoadedListener {

    private final PermissionsManager mPermissionsManager = new PermissionsManager(this);
    private MapView mMapView;
    private LocationPuck2D mLocationPuck;
    private LocationComponentPlugin mLocationPlugin;
    private GesturesPlugin mGesturesPlugin;
    private static final String TAG = LocationFragment.class.getSimpleName();
    ArrayList<Location> locationsArrayList = new ArrayList<>();
    private String selectedHouseholdId;
    private HouseholdViewModel householdViewModel;
    private LocationViewModel locationViewModel;
    private String currentLatitude;
    private String currentLongitude;
    private LocationManager locationManager;
    private boolean locationEnabled;
    private static final String PREFERENCES = "MyPreferences";
    private static final String PREF_THEME_KEY = "theme";
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final long UPDATE_INTERVAL = 1; // 1 minute


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Timber.tag(TAG).d("onCreateView()");
        setHasOptionsMenu(true);

        View v = inflater.inflate(R.layout.fragment_location, container, false);

        mMapView = v.findViewById(R.id.mapView);
        if (mMapView != null) {
            MapboxMap mMapboxMap = mMapView.getMapboxMap();
            setupMap(mMapboxMap);
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.tag(TAG).d("onViewCreated()");

        // Check if network connection exists
        if (MyApplication.hasNetworkConnection(requireContext())) {

            //Initialize ViewModel instances
            householdViewModel = new ViewModelProvider(requireActivity()).get(HouseholdViewModel.class);
            locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

            // Check if device location is enabled
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    
            // Request location permissions when the fragment is created
            if (!PermissionsManager.areLocationPermissionsGranted(requireContext())) {
                Timber.tag(TAG).d("Requesting permissions...");
                mPermissionsManager.requestLocationPermissions(requireActivity());
            } else {
                // Permissions are already granted, start location updates
                Timber.tag(TAG).d("Permissions already granted.");
                if (locationEnabled) {
                    new Handler().postDelayed(this::startLocationUpdates, 1000); // 1000 milliseconds delay
                }
            }

            //Observe the selected household LiveData
            householdViewModel.getSelectedHousehold(requireContext()).observe(getViewLifecycleOwner(), household -> {
                if (household != null) {
                    selectedHouseholdId = household.getHouseholdId();
                    Timber.tag(TAG).d("Selected household observed: %s", selectedHouseholdId);

                    // Fetch locations for the selected household's location manager
                    locationViewModel.getLocationsFromLocationManager(household.getHouseholdId());
                } else {
                    Timber.tag(TAG).d("No household selected.");
                }
            });

            //Observe the locations LiveData
            locationViewModel.getSelectedLocations().observe(getViewLifecycleOwner(), locations -> {
                if (locations != null && !locations.isEmpty()) {
                    Timber.tag(TAG).d("Locations: %s", locations.size());
                    locationsArrayList.clear();
                    locationsArrayList.addAll(locations);
                    Timber.tag(TAG).d("LocationArrayList: %s", locationsArrayList.get(0).getUserId());
                    addLocationMarkers();
                } else {
                    Timber.tag(TAG).d("No locations");
                }
            });

            //Observe the location exists LiveData
            locationViewModel.getLocationExists().observe(getViewLifecycleOwner(), locationExists -> {
                if (locationExists) {
                    Timber.tag(TAG).d("Location already exists. Updating location...");
                    locationViewModel.updateLocation(currentLongitude, currentLatitude, FirebaseAuth.getInstance().getUid());
                } else {
                    Timber.tag(TAG).d("Location doesn't exist. Adding new location...");
                    locationViewModel.addLocation(currentLongitude, currentLatitude, FirebaseAuth.getInstance().getUid());
                }
            });
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void showGPSDisabledAlertToUser() {
        Timber.tag(TAG).d("showGPSDisabledAlertToUser");
        MaterialAlertDialogBuilder locationDialog = new MaterialAlertDialogBuilder(getContext());
        locationDialog.setTitle("Attention");
        locationDialog.setMessage("Location setting is not enabled.");
        locationDialog.setCancelable(false);
        locationDialog.setPositiveButton("Open settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        locationDialog.create().show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_maps, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();
        final Activity activity = requireActivity();
        if (itemId == R.id.menu_showcurrentlocation) {
            if (!PermissionsManager.areLocationPermissionsGranted(requireContext())) {
                mPermissionsManager.requestLocationPermissions(activity);
            } else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){   // Check if device location is enabled
                Timber.tag(TAG).d("GPS not enabled.");
                showGPSDisabledAlertToUser();
            } else {
                MapboxMap map = mMapView.getMapboxMap();
                setupMap(map);
            }
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (MyApplication.hasNetworkConnection(requireContext())) {
            if (mGesturesPlugin != null){
                mGesturesPlugin.removeOnMoveListener(this);
                mGesturesPlugin = null;
            }
            if (mLocationPlugin != null){
                mLocationPlugin.removeOnIndicatorBearingChangedListener(this);
                mLocationPlugin.removeOnIndicatorPositionChangedListener(this);
                mLocationPlugin = null;
            }
            MapboxMap map = mMapView.getMapboxMap();
            map.removeOnStyleDataLoadedListener(this);
            mMapView = null;
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        final Context ctx = requireContext();
        Toast.makeText(ctx, "You must enable location permissions", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            Timber.tag(TAG).d("Location permission granted");
            Toast.makeText(requireContext(), "User granted location permission", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(this::startLocationUpdates, 1000); // 1000 milliseconds delay
        } else {
            Timber.d(TAG, "User denied location permission");
            Toast.makeText(requireContext(), "User denied location permission", Toast.LENGTH_SHORT).show();
            onCameraTrackingDismissed();
        }
    }

    private void setupMap(MapboxMap map) {
        if (MyApplication.hasNetworkConnection(requireContext())) {
            Timber.tag(TAG).d("setupMap");
            CameraOptions cameraOptions = new CameraOptions.Builder()
                    .zoom(14.0)
                    .build();
            map.setCamera(cameraOptions);
            map.loadStyleUri(Style.MAPBOX_STREETS);
            map.addOnStyleDataLoadedListener(this);
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }


    private void initLocation() {
        Timber.tag(TAG).d("initLocation");
        final Activity activity = requireActivity();
        if (mLocationPuck == null) {
            mLocationPuck = new LocationPuck2D();
        }

        mLocationPlugin = mMapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
        if (mLocationPlugin != null) {
            mLocationPlugin.updateSettings(new Function1<LocationComponentSettings, Unit>() {
                @Override
                public Unit invoke(LocationComponentSettings locationComponentSettings) {
                    locationComponentSettings.setEnabled(true);
                    return null;
                }
            });
            mLocationPlugin.addOnIndicatorBearingChangedListener(this);
            mLocationPlugin.addOnIndicatorPositionChangedListener(this);
        }
    }

    private void setupGesturesListener() {
        mGesturesPlugin = mMapView.getPlugin(Plugin.MAPBOX_GESTURES_PLUGIN_ID);
        if (mGesturesPlugin != null) {
            mGesturesPlugin.addOnMoveListener(this);
        }
    }

    @Override
    public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
        return false;
    }

    @Override
    public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
        onCameraTrackingDismissed();
    }

    @Override
    public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {
        // Nothing here
    }

    @Override
    public void onIndicatorBearingChanged(double v) {

    }

    @Override
    public void onIndicatorPositionChanged(@NonNull Point point) {
        MapboxMap map = mMapView.getMapboxMap();
        CameraOptions.Builder builder = new CameraOptions.Builder();
        map.setCamera(builder.center(point).build());
        mGesturesPlugin.setFocalPoint(map.pixelForCoordinate(point));

        currentLatitude = String.valueOf(point.latitude());
        currentLongitude = String.valueOf(point.longitude());
    }

    @Override
    public void onStyleDataLoaded(@NonNull StyleDataLoadedEventData styleDataLoadedEventData) {
        initLocation();
        setupGesturesListener();
    }

    private void onCameraTrackingDismissed() {
        Timber.tag(TAG).d("onCameraTrackingDismissed()");
//        Toast.makeText(requireActivity(), "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show();
        if (mLocationPlugin != null) {
            mLocationPlugin.removeOnIndicatorPositionChangedListener(this);
            mLocationPlugin.removeOnIndicatorBearingChangedListener(this);
        }
        if (mGesturesPlugin != null) {
            mGesturesPlugin.removeOnMoveListener(this);
        }
    }

    private void startLocationUpdates() {
        executorService.scheduleAtFixedRate(() -> {
            if (MyApplication.hasNetworkConnection(requireContext())) {
                String userId = FirebaseAuth.getInstance().getUid();
                if (userId != null) {
                    locationViewModel.checkIfLocationExists(userId, selectedHouseholdId);
                } else {
                    Timber.tag(TAG).d("User ID is null. Unable to save location.");
                }
            } else {
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }, 0, UPDATE_INTERVAL, TimeUnit.MINUTES);
    }

    private void addLocationMarkers() {
        Timber.tag(TAG).d("addLocationMarkers");
        Timber.tag(TAG).d("LocationArrayList!: %s", locationsArrayList.get(0).getUserId().toString());

        ViewAnnotationManager viewAnnotationManager = mMapView.getViewAnnotationManager();

        // Remove all existing annotations
        viewAnnotationManager.removeAllViewAnnotations();

        if (MyApplication.hasNetworkConnection(requireContext())) {
            for (Location location : locationsArrayList) {
                // If user's location is not enabled, their location in db will be null
                if (location.getLatitude() == null || location.getLongitude() == null) {
                    continue;
                }
                Timber.tag(TAG).d("got %s's location", location.getUserId());

                // Create a point geometry from the location coordinates
                Geometry g = Point.fromLngLat(
                        Double.parseDouble(location.getLongitude()),
                        Double.parseDouble(location.getLatitude())
                );

                // Fetch the display name asynchronously
                location.getDisplayName(displayName -> {
                    if (displayName != null) {
                        // Create a ViewAnnotationOptions
                        ViewAnnotationOptions viewAnnotationOptions = new ViewAnnotationOptions.Builder()
                                .geometry(g)
                                .width(200)
                                .height(75)
                                .visible(true)
                                .anchor(ViewAnnotationAnchor.CENTER)
                                .selected(false)
                                .build();

                        // Inflate the layout to access its views
                        View annotationView = LayoutInflater.from(getContext()).inflate(R.layout.location_annotation, mMapView, false);
                        TextView displayNameTextView = annotationView.findViewById(R.id.annotation);

                        // Set the text to the fetched display name
                        displayNameTextView.setText(displayName);

                        // Retrieve the current theme preference
                        boolean isDarkModeEnabled = isDarkModeEnabled(requireContext());

                        // Choose the annotation color based on the theme
                        int annotationColor = isDarkModeEnabled
                                ? ContextCompat.getColor(requireContext(), R.color.purple_200)
                                : ContextCompat.getColor(requireContext(), R.color.purple_500);

                        // Choose the text color based on the theme
                        int textColor = isDarkModeEnabled
                                ? ContextCompat.getColor(requireContext(), android.R.color.black)
                                : ContextCompat.getColor(requireContext(), android.R.color.white);

                        displayNameTextView.setTextColor(textColor);
                        annotationView.setBackgroundColor(annotationColor);

                        viewAnnotationManager.addViewAnnotation(annotationView, viewAnnotationOptions);
                    } else {
                        Timber.tag(TAG).d("Display Name is null for %s", location.getUserId());
                    }
                });
            }
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isDarkModeEnabled(Context context) {
        // Retrieve the current theme preference
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        int themeId = preferences.getInt(PREF_THEME_KEY, R.style.Theme_Homies_Light);
        return themeId == R.style.Theme_Homies_Dark;
    }
}
