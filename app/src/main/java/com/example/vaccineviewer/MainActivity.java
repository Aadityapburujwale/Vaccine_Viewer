package com.example.vaccineviewer;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    TextInputEditText pincode_editText;
    Button pick_date_btn;
    ArrayList<vaccinationData> dataList = new ArrayList<vaccinationData>();
    Button search_btn;
    RecyclerView recyclerView;
    RequestQueue queue;
    Button auto_refresh_btn;
    CheckBox include_paid_vaccine;

   public volatile boolean stopThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pincode_editText = findViewById(R.id.pincode_edittext);
        search_btn = findViewById(R.id.search_Btn);
        pick_date_btn = findViewById(R.id.select_date_btn);
        auto_refresh_btn = findViewById(R.id.auto_refresh_btn);
        include_paid_vaccine = findViewById(R.id.include_paid_checkbox);

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

                stopThread = true;
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
                    boolean check = checkVaccine(pinCode,date,"MANUALLY");
                    Toast.makeText(MainActivity.this,"Try Our AUTO NOTIFY Feature!",Toast.LENGTH_SHORT).show();


                }
            }
        });


        auto_refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopThread = false;


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
                    notifyToTheUser(11111,"Id","Vaccine Is Finding...",
                            "You Will Get Notification When Vaccine Is Available",
                            true,false);
                    Toast.makeText(MainActivity.this,"Auto Featured Is Turned On.\nKeep App In Backround",Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this,"Keep App Running In Backround.",Toast.LENGTH_SHORT).show();
                    ExampleRunnable runnable = new ExampleRunnable(pinCode,date);
                    new Thread(runnable).start();
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

   public boolean checkVaccine(String pinCode,String data,String pressed){

        boolean includePaidVaccine = include_paid_vaccine.isChecked();

       dataList.clear();

        String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode="+pinCode+"&date="+data;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray dataArray = response.getJSONArray("centers");

                            if(dataArray.length()==0 && pressed.equals("MANUALLY")){
                                Toast.makeText(MainActivity.this,"No Center Available",Toast.LENGTH_SHORT).show();
                            }
                            for(int i=0;i<dataArray.length();i++){
                                JSONObject centers = dataArray.getJSONObject(i);

                                String centerName = centers.getString("name");
                                String centerAddress = centers.getString("address");
                                String vaccinationStartTime = centers.getString("from");
                                String vaccinationEndTime = centers.getString("to");
                                String vaccinePrice = centers.getString("fee_type");

                                if(vaccinePrice.equalsIgnoreCase("Paid") && !includePaidVaccine) continue;

                                JSONObject sessions = centers.getJSONArray("sessions").getJSONObject(0);
                                int minimumAge = sessions.getInt("min_age_limit");
                                String vaccineName = sessions.getString("vaccine");
                                int totalVaccineAvailable = sessions.getInt("available_capacity");

                                if(totalVaccineAvailable>0){
                                    notifyToTheUser(i,"Id","Vaccine Is Available At "+centerName,
                                            "Click Here To Book Now...",false,true);
                                    notifyToTheUser(11111,"Id","Vaccine Is Available.",
                                            "Click Here To Book Now...",
                                            false,true);
                                }

                                vaccinationData addData = new vaccinationData(centerName,
                                        centerAddress,vaccinationStartTime,vaccinationEndTime,
                                        vaccinePrice,vaccineName,totalVaccineAvailable,minimumAge);

                                dataList.add(addData);

                            }

                            if(pressed.equals("AUTO") && isVaccineAvailable()){
                                vaccinationDataAdapter adapter = new vaccinationDataAdapter(MainActivity.this,dataList);
                                recyclerView.setAdapter(adapter);
                            }else if(pressed.equals("MANUALLY")){
                                vaccinationDataAdapter adapter = new vaccinationDataAdapter(MainActivity.this,dataList);
                                recyclerView.setAdapter(adapter);
                            }

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

        return isVaccineAvailable();
    }

    boolean isVaccineAvailable(){
        for(int i=0;i<dataList.size();i++){
            if(dataList.get(i).totalAvailableVaccin>0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = String.format("%02d-%02d-%d",dayOfMonth,month+1,year);
         pick_date_btn.setText(date);
    }

    @TargetApi(Build.VERSION_CODES.M)
    void notifyToTheUser(int nId,String Id,String title,String des,boolean sticky,boolean openRegPage){
        // Notification
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,Id+nId)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(des)
               // .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
               // .setContentIntent(pendingIntent);


        if(sticky){
            notificationBuilder.setOngoing(true);
        }
        if(openRegPage){
            Intent openRegisterPage = new Intent(Intent.ACTION_VIEW, Uri.parse("https://selfregistration.cowin.gov.in"));
            PendingIntent pendingIntent = PendingIntent.getActivity(this,1,openRegisterPage,PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentIntent(pendingIntent);
        }

        NotificationManager notifyToTheUser = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Checking Android Version Is > Oreo (For Notification)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(Id+nId,title,NotificationManager.IMPORTANCE_HIGH);
            notifyToTheUser.createNotificationChannel(notificationChannel);
        }

        if(title.contains("Vaccine Is Available")){
            MediaPlayer mp = MediaPlayer.create(this,R.raw.vaccine_tone);
            mp.start();
            notifyToTheUser.notify(nId,notificationBuilder.build());
        }else {
            notificationBuilder.setSound(defaultSound);
            notifyToTheUser.notify(nId,notificationBuilder.build());
        }

    }


    // Close Keyboard
    void closeKeyboard(){

        View view = this.getCurrentFocus();

        if(view != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    class ExampleRunnable implements Runnable {
        String pinCode;
        String date;

        ExampleRunnable(String pinCode,String date) {
            this.pinCode = pinCode;
            this.date = date;
        }

        @Override
        public void run() {
            while(!stopThread) {
                if(stopThread) break;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopThread = checkVaccine(pinCode,date,"AUTO");
                            if(stopThread) return;
                        }
                    });

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(stopThread) return;
        }
    }

}