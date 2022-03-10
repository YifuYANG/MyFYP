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
import androidx.appcompat.app.AppCompatActivity;
import com.example.myfyp.dbhelper.DBHelper;

import com.example.myfyp.vo.Databank;
import com.example.myfyp.vo.InputForm;
import com.example.myfyp.vo.License;
import com.example.myfyp.vo.LoginformToAccessUploadDistanceServer;
import com.example.myfyp.vo.UploadedData;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
public class LicenseComparisonActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private String value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaddistancetoserver);
        dbHelper = new DBHelper(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("key");
        }
        overtake();
        //uploaddata(10,106.52,220.3,102.3,24.1);
        Intent intent = new Intent(getApplicationContext(), IndexActivity.class);
        startActivity(intent);
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
                    uploaddata(location.getSpeed(), 0, 0, 0, 0);
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
                    if(ifuselicensecomparison(speed)){
                        if(licenseComparison()){
                            Map<String,String> token=gettoken();
                            if(token!=null){
                                Boolean isdataupload=ifuploadsucess(token,uploadedData);
                                System.out.println(isdataupload);
                            } else {
                                System.out.println("you need to at least login once to access database");
                                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            //authentication failed
                            System.out.println("unable to authenticate user");
                        }
                    } else {
                        //login then upload
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        if(value!=null && value.equals("loginpassed")){
                            Map<String,String> token=gettoken();
                            ifuploadsucess(token,uploadedData);
                        } else{
                            intent.putExtra("key", "needlogintoauthedtouploaddata");
                            startActivity(intent);
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
        if(license!=null){
            return restTemplate.postForObject("http://10.0.2.2:8081/authentication/pass",new LoginformToAccessUploadDistanceServer(license.getDeviceId(),license.getPassword()),Map.class);
        } else {
            return null;
        }
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

    private class LicenseCompare extends AsyncTask<String,String,Boolean> {
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
                System.out.println("you need to at least login once to access database"+e);
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean match) {
            super.onPostExecute(match);
        }
    }
}
