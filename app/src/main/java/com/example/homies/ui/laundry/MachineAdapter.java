package com.example.homies.ui.laundry;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

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
import com.example.homies.model.Machine;
import com.example.homies.model.User;
import com.example.homies.model.viewmodel.LaundryViewModel;
import com.example.homies.model.viewmodel.UserViewModel;

import java.util.ArrayList;

import timber.log.Timber;

public class MachineAdapter extends RecyclerView.Adapter<MachineAdapter.ViewHolder>{

    private ArrayList<Machine> machineList;
    private FragmentManager fragmentManager;
    LaundryViewModel laundryViewModel;
    private ArrayList<ViewHolder> viewHolderList;

    private final String TAG = getClass().getSimpleName();

    public MachineAdapter(ArrayList<Machine> machineList, FragmentManager fragmentManager, LaundryViewModel laundryViewModel){
        this.machineList = machineList;
        this.fragmentManager = fragmentManager;
        this.laundryViewModel = laundryViewModel;
        viewHolderList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MachineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.adapter_laundry_machine, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolderList.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MachineAdapter.ViewHolder holder, int position) {
        Machine machine = machineList.get(position);

        holder.setMachine(machine);
        TextView name = holder.machineNameTV;
        name.setText(machine.getName());

        if(machine.getUsedBy() == null){
            holder.unableStopButton();
        } else {
            holder.ableStopButton();
        }
    }

    @Override
    public int getItemCount() {
        return machineList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView machineNameTV, machineDetailTV;
        public Button stopMachineButton, editMachineButton;
        private Machine machine;
        private User user;


        public ViewHolder(View itemView) {
            super(itemView);
            machineNameTV = itemView.findViewById(R.id.textViewMachineNameInList);
            machineDetailTV = itemView.findViewById(R.id.textViewMachineDetailInList);
            stopMachineButton = itemView.findViewById(R.id.laundryStopButton);
            editMachineButton = itemView.findViewById(R.id.machineEditButton);

            stopMachineButton.setOnClickListener(this);
            editMachineButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.laundryStopButton){
                laundryViewModel.updateMachineStatus(machine.getName(), -1);
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new LaundryFragment())
                        .commit();
            } else if (v.getId() == R.id.machineEditButton){
                //link to machine edit page
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new EditLaundryMachineFragment(laundryViewModel, machine))
                        .addToBackStack(null)
                        .commit();
            }
        }

        public void unableStopButton() {
            //hide buttons
            stopMachineButton.setVisibility(INVISIBLE);
            machineDetailTV.setVisibility(INVISIBLE);
            Timber.tag(TAG).d("hide machine details for %s", machine.getName());
        }

        public void setMachine(Machine machine) {
            this.machine = machine;
            UserViewModel userViewModel = new UserViewModel();
            if (machine.getUsedBy() != null){
                userViewModel.getUserInformation(machine.getUsedBy());
                user = userViewModel.getUser();
            }
        }

        public void ableStopButton() {
            //show buttons
            stopMachineButton.setVisibility(VISIBLE);
            machineDetailTV.setVisibility(VISIBLE);
            machineDetailTV.setText("End Time: "+machine.getEndAt());
            Timber.tag(TAG).d("show machine details for %s", machine.getName());

//            if (user == null){
//                Timber.tag(TAG).e("User Not Found: %s", machine.getUsedBy());
//            } else{
//                if (user.getDisplayName() != ""){
//                    machineDetailTV.setText(machine.getEndAt());
//                } else {
//                    machineDetailTV.setText(machine.getEndAt());
//                }
//                Timber.tag(TAG).d("show machine details for %s", machine.getName());
//            }
        }


    }
}
