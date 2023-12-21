package com.example.homies.ui.calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homies.MyApplication;
import com.example.homies.R;
import com.example.homies.model.CalendarEvent;
import com.example.homies.model.viewmodel.CalendarViewModel;
import com.example.homies.model.viewmodel.HouseholdViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class CalendarFragment extends Fragment implements View.OnClickListener, AddEventDialogFragment.OnEventAddedListener,
        AddEventDialogFragment.OnEventUpdatedListener {
    View view;
    private FloatingActionButton actionButton;
    private RecyclerView recyclerViewEvents;
    private ArrayList<CalendarEvent> calendarEvents = new ArrayList<>();
    private CalendarAdapter adapter;
    private HouseholdViewModel householdViewModel;
    private CalendarViewModel calendarViewModel;
    private final String TAG = CalendarFragment.class.getSimpleName();
    private static final String PREFERENCES = "MyPreferences";
    private static final String PREF_THEME_KEY = "theme";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        Timber.tag(TAG).d("onCreateView()");

        actionButton = view.findViewById(R.id.fabAddEvent);
        recyclerViewEvents = view.findViewById(R.id.recyclerViewEvents);

        // Retrieve the current theme preference
        boolean isDarkModeEnabled = isDarkModeEnabled(requireContext());

        // Choose the background tint color based on the theme
        int backgroundColor = isDarkModeEnabled
                ? ContextCompat.getColor(requireContext(), R.color.purple_200)
                : ContextCompat.getColor(requireContext(), R.color.purple_500);

        // Choose the icon color based on the theme
        int iconColor = isDarkModeEnabled
                ? ContextCompat.getColor(requireContext(), android.R.color.black)
                : ContextCompat.getColor(requireContext(), android.R.color.white);

        // Set the background tint dynamically
        actionButton.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));

        // Set the icon color dynamically
        actionButton.setImageTintList(ColorStateList.valueOf(iconColor));

        // Create an instance of the CalendarAdapter
        adapter = new CalendarFragment.CalendarAdapter(calendarEvents, this);

        // Set the layout manager and adapter for the RecyclerView
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewEvents.setAdapter(adapter);

        // Set OnClickListener for the FAB
        actionButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.tag(TAG).d("onViewCreated()");

        // Initialize the ViewModel instances
        householdViewModel = new ViewModelProvider(requireActivity()).get(HouseholdViewModel.class);
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        // Check if network connection exists
        if (MyApplication.hasNetworkConnection(requireContext())) {
            // Observe the selected household LiveData
            householdViewModel.getSelectedHousehold(requireContext()).observe(getViewLifecycleOwner(), household -> {
                if (household != null) {
                    Timber.tag(TAG).d("Selected household observed: %s", household.getHouseholdId());
                    // Fetch events for the selected household's calendar
                    calendarViewModel.getEventsForCalendar(household.getHouseholdId());
                } else {
                    Timber.tag(TAG).d("No household selected.");
                }
            });

            // Observe the events LiveData from CalendarViewModel
            calendarViewModel.getSelectedEvents().observe(getViewLifecycleOwner(), events -> {
                if (events != null && !events.isEmpty()) {
                    Timber.tag(TAG).d("Received events: %s", events.size());
                    // Clear existing events and add the new events to the adapter
                    calendarEvents.clear();
                    calendarEvents.addAll(events);

                    // Sort the list based on event dates
                    calendarEvents.sort(Comparator.comparing(CalendarEvent::getEventDateTime));
                    adapter.notifyDataSetChanged();
                } else {
                    // Handle the case where no events are available
                    Timber.tag(TAG).d("No events available.");
                }
            });
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        Timber.tag(TAG).d("onClick()");
        if (view.getId() == R.id.fabAddEvent) {
            openAddEventDialog();
        }
    }

    private boolean isDarkModeEnabled(Context context) {
        // Retrieve the current theme preference
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        int themeId = preferences.getInt(PREF_THEME_KEY, R.style.Theme_Homies_Light);
        return themeId == R.style.Theme_Homies_Dark;
    }

    @Override
    public void onEventAdded(String eventName, Timestamp eventDateTime) {
        if (!eventName.isEmpty() && !eventNameExists(eventName)) {
            // Add the new event to the list
            calendarViewModel.addCalendarEvent(eventName, eventDateTime);
        } else {
            // Show a toast message based on the validation result
            if (eventName.isEmpty()) {
                Toast.makeText(requireContext(), "Event name cannot be empty!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Event name already exists!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onEventUpdated(String oldEventName, String newEventName, Timestamp newDateTime, int position) {
        if (newEventName.equals(oldEventName) || !eventNameExists(newEventName)) {
            // Find the position of the old event in the sorted list
            int oldPosition = calendarEvents.indexOf(calendarEvents.get(position));

            // Update the existing event in the list with new data
            CalendarEvent updatedEvent = calendarEvents.get(oldPosition);
            updatedEvent.setEventName(newEventName);
            updatedEvent.setEventDateTime(newDateTime);

            // Sort the list based on event dates
            calendarEvents.sort(Comparator.comparing(CalendarEvent::getEventDateTime));

            // Find the updated position after sorting
            int updatedPosition = calendarEvents.indexOf(updatedEvent);

            // Notify the adapter of the data change
            adapter.notifyItemMoved(oldPosition, updatedPosition);
            adapter.notifyItemChanged(updatedPosition);

            // Update the event in Firebase
            calendarViewModel.updateCalendarEvent(oldEventName, newEventName, newDateTime);
        } else {
            // Show a toast message based on the validation result
            if (newEventName.isEmpty()) {
                Toast.makeText(requireContext(), "Event name cannot be empty!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Failed to update. Event name already exists!", Toast.LENGTH_SHORT).show();
            }
        }
    }



    // Helper method to check if the event name already exists in the list
    private boolean eventNameExists(String eventName) {
        for (CalendarEvent event : calendarEvents) {
            if (event.getEventName().equalsIgnoreCase(eventName)) {
                return true; // Event name already exists
            }
        }
        return false; // Event name does not exist
    }



    // Method to open the dialog
    private void openAddEventDialog() {
        AddEventDialogFragment dialogFragment = new AddEventDialogFragment();
        dialogFragment.setOnEventAddedListener(this);
        dialogFragment.show(getChildFragmentManager(), "AddEventDialogFragment");
    }

    // Method to open the edit dialog
    private void openEditEventDialog(CalendarEvent event, int position) {
        AddEventDialogFragment dialogFragment = AddEventDialogFragment.newInstanceForEdit(event, position);
        dialogFragment.setOnEventUpdatedListener(this);
        dialogFragment.show(requireActivity().getSupportFragmentManager(), "EditEventDialogFragment");
    }

    // Inner class for RecyclerView Adapter
    private class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
        private List<CalendarEvent> calendarEvents;
        private CalendarFragment calendarFragment;

        public CalendarAdapter(List<CalendarEvent> calendarEvents, CalendarFragment calendarFragment) {
            this.calendarEvents = calendarEvents;
            this.calendarFragment = calendarFragment;
        }

        @NonNull
        @Override
        public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_event, parent, false);
            return new CalendarViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
            CalendarEvent event = calendarEvents.get(position);

            TextView eventNameTextView = holder.itemView.findViewById(R.id.textViewEventName);
            TextView eventDateTimeTextView = holder.itemView.findViewById(R.id.textViewEventDateTime);
            Button editButton = holder.itemView.findViewById(R.id.buttonEdit);
            Button deleteButton = holder.itemView.findViewById(R.id.buttonDelete);

            eventNameTextView.setText(event.getEventName());

            // Format timestamp and set it in the TextView
            String formattedDateTime = formatDateTime(event.getEventDateTime());
            eventDateTimeTextView.setText(formattedDateTime);

            // Set OnClickListener for the edit and delete button
            editButton.setOnClickListener(view -> {
                openEditEventDialog(event, position);
            });
            deleteButton.setOnClickListener(view -> {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    calendarEvents.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);

                    // Delete the event from Firebase
                    calendarViewModel.deleteCalendarEvent(event.getEventName());
                }
            });
        }

        private void openEditEventDialog(CalendarEvent event, int position) {
            if (calendarFragment != null) {
                calendarFragment.openEditEventDialog(event, position);
            }
        }

        private String formatDateTime(Timestamp timestamp) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
            return timeFormat.format(timestamp.toDate());
        }

        @Override
        public int getItemCount() {
            return calendarEvents.size();
        }

        public class CalendarViewHolder extends RecyclerView.ViewHolder {

            public CalendarViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
