package com.example.myfyp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myfyp.dbhelper.DBHelper;
import com.example.myfyp.vo.Databank;
import com.example.myfyp.vo.InputForm;
import com.example.myfyp.vo.License;
import com.example.myfyp.vo.Loginform;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class Index extends AppCompatActivity {

    private Button id;
    private DBHelper dbHelper;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        mAuth=FirebaseAuth.getInstance();
        mAuth.signOut();
        dbHelper = new DBHelper(this);
        id = (Button) findViewById(R.id.idofdevice);
        id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //overtake();
                uploaddata(10,106.52,220.3,102.3,24.1);

            }
        });
    }

    Boolean isfirsttime;
    @SuppressLint("MissingPermission")
    public void overtake() {
        isfirsttime=true;
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if(isfirsttime){
                    System.out.println(location.getSpeed());
                    if(mAuth.getCurrentUser()==null){
                        uploaddata(location.getSpeed(), 0, 0, 0, 0);
                    }
                }
                isfirsttime=false;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private void uploaddata(double speed,double currentlatitude,double currentlongitude,double targelatitude,double targelongitude){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UploadedData uploadedData = new UploadedData(currentlatitude,currentlongitude,targelatitude,targelongitude,0);
                    Boolean ifuselicensecomparison=ifuselicensecomparison(speed);
                    if(ifuselicensecomparison){
                        if(licenseComparison()){
                            Map<String,String> token=gettoken();
                            Boolean data=ifuploadsucess(token,uploadedData);
                            System.out.println(data);
                        } else {
                            //authentication failed
                            System.out.println("unable to authenticate user");
                        }
                    } else {
                        System.out.println(mAuth.getCurrentUser());
                        if(mAuth.getCurrentUser()==null){
                            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Map<String,String> token=gettoken();
                            ifuploadsucess(token,uploadedData);
                        }
                    }
                } catch (Exception e){
                    System.out.println(e);
                }
            }
        }).start();
    }

    private Boolean licenseComparison(){
        try {
            return new LicenseCompare().execute().get();
        } catch (Exception e){
            System.out.println(e);
            return false;
        }
    }

    private Map<String,String> gettoken(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        License license=dbHelper.getdatabydevice(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
        return restTemplate.postForObject("http://10.0.2.2:8081/authentication/pass",new Loginform(license.getDeviceId(),license.getPassword()),Map.class);
    }

    private Boolean ifuploadsucess(Map<String,String> token,UploadedData uploadedData){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        HttpHeaders header = new HttpHeaders();
        header.set("token", token.get("token"));
        HttpEntity<UploadedData> entity_2=new HttpEntity<>(uploadedData,header);
        return restTemplate.postForObject("http://10.0.2.2:8081/uploaddata",entity_2,Boolean.class);
    }

    private Boolean ifuselicensecomparison(double speed){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate.postForObject("http://10.0.2.2:8081/authentication",new InputForm(speed),Boolean.class);
    }

    private class LicenseCompare extends AsyncTask<String,String,Boolean>{
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                //get driver licnese from database
                //visit government vehicle management website and extract the driver info which under the car plate
                //ie: xxx-xxx -> driver's licence
                //compare driver licence
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Databank databank=new Databank(dbHelper.getdatabydevice(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID)).getDriverlicense(),dbHelper.getdatabydevice(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID)).getDeviceId());
                return restTemplate.postForObject("http://10.0.2.2:8082/finddriver", databank, Boolean.class);
            } catch (Exception e){
                System.out.println(e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean match) {
            super.onPostExecute(match);
        }
    }
}
