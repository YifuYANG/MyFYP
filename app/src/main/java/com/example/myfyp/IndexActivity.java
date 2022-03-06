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

import com.example.myfyp.entity.UploadedData;
import com.example.myfyp.dbhelper.DBHelper;
import com.example.myfyp.vo.Databank;
import com.example.myfyp.vo.InputForm;
import com.example.myfyp.vo.License;
import com.example.myfyp.vo.LoginformToAccessUploadDistanceServer;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        Button id = (Button) findViewById(R.id.idofdevice);
        id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UploadDistanceToServer.class);
                startActivity(intent);
            }
        });

        Button acquireinfo = (Button) findViewById(R.id.acquireinfo);
        acquireinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginToTrafficInfoApiActivity.class);
                startActivity(intent);
            }
        });
    }
}
