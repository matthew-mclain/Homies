package com.example.homies.ui.location;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.homies.R;
import com.example.homies.model.GroceryItem;
import com.example.homies.model.Location;
import com.example.homies.model.viewmodel.GroceryListViewModel;
import com.example.homies.model.viewmodel.HouseholdViewModel;
import com.example.homies.model.viewmodel.LocationViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;
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
    private HouseholdViewModel householdViewModel;
    private LocationViewModel locationViewModel;
    private long lastTime;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final long UPDATE_INTERVAL = 5; // 5 minutes


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        View v = inflater.inflate(R.layout.fragment_location, container, false);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        MapboxMap mMapboxMap = mMapView.getMapboxMap();
        setupMap(mMapboxMap);

        // Request location permissions when the fragment is created
        if (!PermissionsManager.areLocationPermissionsGranted(requireContext())) {
            mPermissionsManager.requestLocationPermissions(requireActivity());
        } else {
            // Permissions are already granted, start location updates
            startLocationUpdates();
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.tag(TAG).d("onViewCreated()");

        //Initialize ViewModel instances
        householdViewModel = new ViewModelProvider(requireActivity()).get(HouseholdViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        //Observe the selected household LiveData
        householdViewModel.getSelectedHousehold(requireContext()).observe(getViewLifecycleOwner(), household -> {
            if (household != null) {
                Timber.tag(TAG).d("Selected household observed: %s", household.getHouseholdId());
                // Fetch messages for the selected household's group chat
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
            } else {
                Timber.tag(TAG).d("No locations");
            }
        });
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
        mGesturesPlugin.removeOnMoveListener(this);
        mGesturesPlugin = null;
        mLocationPlugin.removeOnIndicatorBearingChangedListener(this);
        mLocationPlugin.removeOnIndicatorPositionChangedListener(this);
        mLocationPlugin = null;
        MapboxMap map = mMapView.getMapboxMap();
        map.removeOnStyleDataLoadedListener(this);
        mMapView = null;
        executorService.shutdown();
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
            startLocationUpdates();
        } else {
            Timber.d(TAG, "User denied location permission");
            Toast.makeText(requireContext(), "User denied location permission", Toast.LENGTH_SHORT).show();
            onCameraTrackingDismissed();
        }
    }

    private void setupMap(MapboxMap map) {
        CameraOptions cameraOptions = new CameraOptions.Builder()
                .zoom(14.0)
                .build();
        map.setCamera(cameraOptions);
        map.loadStyleUri(Style.MAPBOX_STREETS);
        map.addOnStyleDataLoadedListener(this);
    }


    private void initLocation() {
        final Activity activity = requireActivity();
        if (mLocationPuck == null) {
            mLocationPuck = new LocationPuck2D();
//            mLocationPuck.setBearingImage(AppCompatResources.getDrawable(activity, R.drawable.location_24));
//            mLocationPuck.setShadowImage(AppCompatResources.getDrawable(activity, R.drawable.location_24));
        }

        mLocationPlugin = mMapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
        if (mLocationPlugin != null) {
            mLocationPlugin.updateSettings(new Function1<LocationComponentSettings, Unit>() {
                @Override
                public Unit invoke(LocationComponentSettings locationComponentSettings) {
                    locationComponentSettings.setEnabled(true);
//                    locationComponentSettings.setLocationPuck(mLocationPuck);
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

        //TODO: save location to database
        Timber.tag(TAG).d("longitude: " + point.longitude() + " latitude: " + point.latitude());
    }

    @Override
    public void onStyleDataLoaded(@NonNull StyleDataLoadedEventData styleDataLoadedEventData) {
        initLocation();
        setupGesturesListener();
    }

    private void onCameraTrackingDismissed() {
        Timber.tag(TAG).d("onCameraTrackingDismissed()");
        Toast.makeText(requireActivity(), "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show();
        if (mLocationPlugin != null) {
            mLocationPlugin.removeOnIndicatorPositionChangedListener(this);
            mLocationPlugin.removeOnIndicatorBearingChangedListener(this);
        }
        if (mGesturesPlugin != null) {
            mGesturesPlugin.removeOnMoveListener(this);
        }
    }

    private void saveLocationToDatabase() {
        if (locationsArrayList != null && !locationsArrayList.isEmpty()) {
            // Get the last known location from the list of locations
            Location lastKnownLocation = locationsArrayList.get(locationsArrayList.size() - 1);

            String latitude = lastKnownLocation.getLatitude();
            String longitude = lastKnownLocation.getLongitude();
            String userId = lastKnownLocation.getUserId();

            locationViewModel.checkIfLocationExists(userId).observe(getViewLifecycleOwner(), locationExists -> {
                if (locationExists) {
                    Timber.tag(TAG).d("Location already exists. Updating location...");
                    // Handle the case where the location already exists (e.g., update the existing location)
                    locationViewModel.updateLocation(longitude, latitude, userId);
                } else {
                    Timber.tag(TAG).d("Location doesn't exist. Adding new location...");
                    // Handle the case where the location doesn't exist (e.g., add the new location to the database)
                    locationViewModel.addLocation(longitude, latitude, userId);
                }
            });

        } else {
            Timber.tag(TAG).d("No location data available to save.");
        }
    }

    private void startLocationUpdates() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.scheduleAtFixedRate(this::saveLocationToDatabase, 0, UPDATE_INTERVAL, TimeUnit.MINUTES);
            Timber.tag(TAG).d("Location updates started.");
        }
    }
}
