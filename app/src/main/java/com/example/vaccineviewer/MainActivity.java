package com.example.vaccineviewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.datepicker.MaterialTextInputPicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    TextInputEditText pincode_editText;
    Button pick_date_btn;
    ArrayList<vaccinationData> dataList = new ArrayList<vaccinationData>();
    Button search_btn;
    RecyclerView recyclerView;
    RequestQueue queue;
    Button auto_refresh_btn;

    boolean isVaccineAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pincode_editText = findViewById(R.id.pincode_edittext);
        search_btn = findViewById(R.id.search_Btn);
        pick_date_btn = findViewById(R.id.select_date_btn);
        auto_refresh_btn = findViewById(R.id.auto_refresh_btn);

        recyclerView = findViewById(R.id.availablity_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        queue = Volley.newRequestQueue(MainActivity.this);

        pick_date_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                showDatePicker();
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                String pinCode = pincode_editText.getText().toString();
                String date = pick_date_btn.getText().toString();

                if(pinCode.length()!=6){
                    pincode_editText.setError("Enter Valid PinCode");
                }else if(date.length()!=10){
                    Toast.makeText(MainActivity.this,"Please Select A Date",Toast.LENGTH_SHORT).show();
                    search_btn.setError("Please Select A Date");
                }
                else{
                    search_btn.setError(null);
                    dataList.clear();
                    if(checkVaccine(pinCode,date)) {
                        Toast.makeText(MainActivity.this, "Vaccine Is Availble.", Toast.LENGTH_SHORT).show();
                    notifyToTheUser();
                    }
                    else
                    Toast.makeText(MainActivity.this,"Vaccine Is Not Available.",Toast.LENGTH_SHORT).show();
                }
            }
        });


        auto_refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                String pinCode = pincode_editText.getText().toString();
                String date = pick_date_btn.getText().toString();

                if(pinCode.length()!=6){
                    pincode_editText.setError("Enter Valid PinCode");
                }else if(date.length()!=10){
                    Toast.makeText(MainActivity.this,"Please Select A Date",Toast.LENGTH_SHORT).show();
                    auto_refresh_btn.setError("Please Select A Date");
                }
                else{

                    while(!checkVaccine(pinCode,date)){
                        Handler waitForFiveSeconds = new Handler();
                        waitForFiveSeconds.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            }
                        },5000);
                        dataList.clear();
                    }

                    Toast.makeText(MainActivity.this,"Vaccine Is AVailable.",Toast.LENGTH_SHORT).show();

                    notifyToTheUser();

                }

            }
        });


    }

    void showDatePicker(){
        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }

    boolean checkVaccine(String pinCode,String data){
        isVaccineAvailable = false;
        String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode="+pinCode+"&date="+data;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONArray dataArray = response.getJSONArray("centers");

                            if(dataArray.length()==0){
                                Toast.makeText(MainActivity.this,"No Center Available",Toast.LENGTH_SHORT).show();
                            }
                            for(int i=0;i<dataArray.length();i++){
                                JSONObject centers = dataArray.getJSONObject(i);

                                String centerName = centers.getString("name");
                                String centerAddress = centers.getString("address");
                                String vaccinationStartTime = centers.getString("from");
                                String vaccinationEndTime = centers.getString("to");
                                String vaccinePrice = centers.getString("fee_type");

                                JSONObject sessions = centers.getJSONArray("sessions").getJSONObject(0);
                                int minimumAge = sessions.getInt("min_age_limit");
                                String vaccineName = sessions.getString("vaccine");
                                int totalVaccineAvailable = sessions.getInt("available_capacity");

                                if(totalVaccineAvailable>0){
                                    isVaccineAvailable = true;
                                }

                                vaccinationData addData = new vaccinationData(centerName,
                                        centerAddress,vaccinationStartTime,vaccinationEndTime,
                                        vaccinePrice,vaccineName,totalVaccineAvailable,minimumAge);

                                dataList.add(addData);

                            }

                            vaccinationDataAdapter adapter = new vaccinationDataAdapter(MainActivity.this,dataList);
                            recyclerView.setAdapter(adapter);

                        }catch (JSONException e){
                            Toast.makeText(MainActivity.this,"Unable To Get Data From Server.",Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this,"Unable To Get Data From Server.\nMake Sure Your Internet Connection ON.",Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
        return isVaccineAvailable;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = String.format("%02d-%02d-%d",dayOfMonth,month+1,year);
         pick_date_btn.setText(date);
    }

    @TargetApi(Build.VERSION_CODES.M)
    void notifyToTheUser(){
        // Notification

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"ID")
                .setSmallIcon(R.drawable.vaccin_logo)
                .setContentTitle("Vaccine Is Available.")
                .setContentText("Go Now To Book.")
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notifyToTheUser = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Checking Android Version Is > Oreo (For Notification)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("ID","Vaccine Is Available",NotificationManager.IMPORTANCE_HIGH);
            notifyToTheUser.createNotificationChannel(notificationChannel);
        }

        notifyToTheUser.notify(1,notificationBuilder.build());

    }

    void closeKeyboard(){

        View view = this.getCurrentFocus();

        if(view != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

}