package com.example.myfyp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.myfyp.dbhelper.DBHelperForAccessUploadDistanceServer;
import com.example.myfyp.vo.License;
import com.example.myfyp.vo.LoginformToAccessGetPatientInfoServer;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class AcquirePatientInfo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        choiceauth();
    }
    Boolean isfirsttime;
    @SuppressLint("MissingPermission")
    public void choiceauth() {
        isfirsttime=true;
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if(isfirsttime){
                    if(location.getSpeed()>0){
                        Intent intent = new Intent(getApplicationContext(), BiometricAuthenticationActivity.class);
                        startActivity(intent);
                    } else {
                        //login first then access info from server
                        Intent intent = new Intent(getApplicationContext(), LoginToAccessPatientInfoServer.class);
                        startActivity(intent);
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
    private void toast(String input){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(AcquirePatientInfo.this,input,Toast.LENGTH_LONG).show();
            }
        });
    }
}
