package com.example.myfyp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myfyp.dbhelper.DBHelperForAccessUploadDistanceServer;

import com.example.myfyp.vo.Databank;
import com.example.myfyp.vo.InputForm;
import com.example.myfyp.vo.License;
import com.example.myfyp.vo.LoginformToAccessUploadDistanceServer;
import com.example.myfyp.vo.UploadedData;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
public class OverTakeActivity extends AppCompatActivity {

    private DBHelperForAccessUploadDistanceServer dbHelperForAccessUploadDistanceServer;
    private String value;
    private String android_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaddistancetoserver);
        android_id =Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        Button upload=findViewById(R.id.uploaddistance);
        Button logout=findViewById(R.id.logout);
        dbHelperForAccessUploadDistanceServer = new DBHelperForAccessUploadDistanceServer(this);
        /**
          this four lines of code represent the default car plate and password pre-install
          into the system which will be used to license comparison and login to back end server
         */
        if(dbHelperForAccessUploadDistanceServer.getsize()==0){
            dbHelperForAccessUploadDistanceServer.insertUserInfo(android_id,"","232323");
        } else if(dbHelperForAccessUploadDistanceServer.getdatabydevice(android_id)!=null){
            dbHelperForAccessUploadDistanceServer.update(android_id,"","232323");
        } else {
            dbHelperForAccessUploadDistanceServer.insertUserInfo(android_id,"","232323");
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("key");
        }

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overtake();
                //uploaddata(10,106.52,220.3,102.3,24.1);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), IndexActivity.class);
                startActivity(intent);
            }
        });

    }

    Boolean isfirsttime;
    @SuppressLint("MissingPermission")
    public void overtake() {
        askpermission();
        isfirsttime=true;
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if(isfirsttime){
                    decisionmaking(location.getSpeed(), 0, 0, 0, 0);
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
    private void decisionmaking(double speed,double currentlatitude,double currentlongitude,double targelatitude,double targelongitude){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UploadedData uploadedData = new UploadedData(currentlatitude,currentlongitude,targelatitude,targelongitude,0);
                    if(speed>0){
                        toast("movement detected");
                        if(licenseComparison()){
                            toast("license "+android_id+" found and match");
                            Map<String,String> token=gettoken();
                            if(token!=null){
                                Double distance=ifuploadsucess(token,uploadedData);
                                toast("the example distance is "+distance);
                            }
                        } else {
                            //authentication failed
                            toast("unable to authenticate user");
                        }
                    } else {
                        //login then upload
                        Intent intent = new Intent(getApplicationContext(), LoginToUploadDistanceServerActivity.class);
                        if(value!=null && value.equals("loginpassed")){
                            Map<String,String> token=gettoken();
                            assert token != null;
                            Double distance=ifuploadsucess(token,uploadedData);
                            toast("example distance is "+distance);
                        } else{
                            toast("you need to login to be authed to upload data");
                            intent.putExtra("key", "needlogintoauthedtouploaddata");
                            startActivity(intent);
                        }
                    }
                } catch (Exception e){
                    toast(e.toString());
                }
            }
        }).start();
    }

    private void toast(String input){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(OverTakeActivity.this,input,Toast.LENGTH_LONG).show();
            }
        });
    }

    private Boolean licenseComparison(){
        License license= dbHelperForAccessUploadDistanceServer.getdatabydevice(android_id);
        return license.getDeviceId().equals(android_id);
    }

    /*
    because we want qualified users to use our services only,
    so we will use the car plate with a random password which are pre install into our system as user credentials to login to our servers
    */
    private Map<String,String> gettoken(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        License license= dbHelperForAccessUploadDistanceServer.getdatabydevice(android_id);
        if(license!=null){
            return restTemplate.postForObject("http://10.0.2.2:8081/authentication/pass",new LoginformToAccessUploadDistanceServer(license.getDeviceId(),license.getPassword()),Map.class);
        } else {
            return null;
        }
    }

    private Double ifuploadsucess(Map<String,String> token,UploadedData uploadedData){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        HttpHeaders header = new HttpHeaders();
        header.set("token", token.get("token"));
        HttpEntity<UploadedData> entity_2=new HttpEntity<>(uploadedData,header);
        return restTemplate.postForObject("http://10.0.2.2:8081/uploaddata",entity_2,Double.class);
    }

    /**
     * asking authentication remotely is not the best way in this situation since we are trying to minimize the time to perform authentication
    */
    private Boolean ifuselicensecomparison(double speed){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate.postForObject("http://10.0.2.2:8081/authentication",new InputForm(speed),Boolean.class);
    }

    /**
     * similar compare car plate with driver id remotely is also not the best way in this situation since we are trying to minimize the time to perform authentication,
     * also, we decided to only check if the local database holds the same number as the car plate (we use device id to represent car plate in this simulation)
     * this authentication should only be aimed at the ambulance and not the driver because the distance info is not sensitive
    */
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
                License license= dbHelperForAccessUploadDistanceServer.getdatabydevice(android_id);
                Databank databank=new Databank(license.getDriverlicense(),license.getDeviceId());
                return restTemplate.postForObject("http://10.0.2.2:8082/finddriver", databank, Boolean.class);
            } catch (Exception e){
                System.out.println("you need to at least login once to access database"+e);
                Intent intent = new Intent(getApplicationContext(), LoginToUploadDistanceServerActivity.class);
                startActivity(intent);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean match) {
            super.onPostExecute(match);
        }
    }

    private void askpermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // ask permissions here using below code
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10001);
        }
    }
}
