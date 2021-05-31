package com.example.vaccineviewer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class vaccinationDataAdapter extends RecyclerView.Adapter<vaccinationDataAdapter.ViewHolder> {

    Context context;
    ArrayList<vaccinationData> dataList;
    vaccinationDataAdapter(Context context, ArrayList<vaccinationData> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vaccination_data_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.centerNameTextView.setText("Center Name : "+dataList.get(position).centerName);
        holder.centerAddressTextView.setText("Address :"+dataList.get(position).centerAddress);
        holder.vaccinationTimeTextView.setText("Timing :"+dataList.get(position).vaccinationStartTime.substring(0,5)+" - " + dataList.get(position).VaccinationEndTime.substring(0,5));
        holder.vaccineNameTextView.setText("Vaccine Name :"+dataList.get(position).vaccineName);
        holder.vaccinePriceTextView.setText(dataList.get(position).vaccinePrice);
        holder.minimumAgeLimitTextView.setText("Age :"+dataList.get(position).minimumAgeLimit +"+");
        holder.totalAvailableVaccineTextView.setText("Available Vaccine :"+dataList.get(position).totalAvailableVaccin+"+");
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView centerNameTextView,centerAddressTextView,vaccinationTimeTextView,vaccineNameTextView,vaccinePriceTextView,minimumAgeLimitTextView,totalAvailableVaccineTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            centerNameTextView = itemView.findViewById(R.id.center_name_textview);
            centerAddressTextView = itemView.findViewById(R.id.center_address_textview);
            vaccinationTimeTextView = itemView.findViewById(R.id.vaccination_time_textbox);
            vaccineNameTextView = itemView.findViewById(R.id.vaccine_name_textview);
            vaccinePriceTextView = itemView.findViewById(R.id.vaccine_price_textview);
            minimumAgeLimitTextView = itemView.findViewById(R.id.minimum_age_limit_textview);
            totalAvailableVaccineTextView = itemView.findViewById(R.id.total_available_vaccine_textview);
            
        }
    }
}
