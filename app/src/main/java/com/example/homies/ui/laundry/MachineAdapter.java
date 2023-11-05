package com.example.homies.ui.laundry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homies.R;

import java.util.ArrayList;

//https://guides.codepath.com/android/using-the-recyclerview
public class MachineAdapter extends RecyclerView.Adapter<MachineAdapter.ViewHolder>{

    private ArrayList<Machine> machineList;
    private FragmentManager fragmentManager;

    public MachineAdapter(ArrayList<Machine> machineList, FragmentManager fragmentManager){
        this.machineList = machineList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public MachineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.adapter_laundry_machine, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MachineAdapter.ViewHolder holder, int position) {
        Machine machine = machineList.get(position);

        TextView name = holder.machineNameTV;
        name.setText(machine.getName());
    }

    @Override
    public int getItemCount() {
        return machineList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView machineNameTV;
        public Button stopMachineButton, editMachineButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            machineNameTV = itemView.findViewById(R.id.textViewMachineNameInList);
            stopMachineButton = itemView.findViewById(R.id.laundryStopButton);
            editMachineButton = itemView.findViewById(R.id.machineEditButton);

            stopMachineButton.setOnClickListener(this);
            editMachineButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.laundryStopButton){
                //stop laundry
                //refresh data in list
            } else if (v.getId() == R.id.machineEditButton){
                //link to machine edit page
                fragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, new EditLaundryMachineFragment())
                        .addToBackStack(null)
                        .commit();
            }
        }
    }
}
