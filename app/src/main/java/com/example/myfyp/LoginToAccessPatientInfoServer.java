package com.example.myfyp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myfyp.dbhelper.DBHelperForAccessPatientInfo;
import com.example.myfyp.dbhelper.DBHelperForAccessUploadDistanceServer;
import com.example.myfyp.vo.LoginformToAccessUploadDistanceServer;
import com.example.myfyp.vo.UploadedData;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Map;

public class LoginToAccessPatientInfoServer extends AppCompatActivity {
    private DBHelperForAccessPatientInfo dbHelperForAccessPatientInfo;
    EditText username,password;
    Button login,register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username=(EditText)findViewById(R.id.username);
        password=(EditText) findViewById(R.id.password);
        login=(Button) findViewById(R.id.login);
        register=(Button) findViewById(R.id.register);
        dbHelperForAccessPatientInfo = new DBHelperForAccessPatientInfo(this);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });
    }

    private void Login(){
        String Email=username.getText().toString();
        String pass=password.getText().toString();
        if(Email.isEmpty()){
            Toast.makeText(LoginToAccessPatientInfoServer.this,"Email is required",Toast.LENGTH_LONG).show();
            return;
        }
        if(pass.isEmpty()){
            Toast.makeText(LoginToAccessPatientInfoServer.this,"Password os required",Toast.LENGTH_LONG).show();
            return;
        }
        if(pass.length()<6){
            Toast.makeText(LoginToAccessPatientInfoServer.this,"Min password length is 6",Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    String token= restTemplate.postForObject("http://10.0.2.2:8084//authentication/pass",new LoginformToAccessUploadDistanceServer(Email,pass), Map.class).get("token").toString();
                    HttpHeaders header = new HttpHeaders();
                    header.set("token", token);
                    if(dbHelperForAccessPatientInfo.getdatabydevice(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID))!=null){
                        dbHelperForAccessPatientInfo.update(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID),pass);
                    } else {
                        dbHelperForAccessPatientInfo.insertUserInfo(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID),pass);
                    }
                    HttpEntity<Object> entity=new HttpEntity<>(header);
                    restTemplate.postForObject("http://10.0.2.2:8084/patientInfo",entity,Boolean.class);
                    toast("patient info acquired");
                } catch (Exception e){
                    toast("unable to login");
                    System.out.println(e);
                }
            }
        }).start();
    }



    private void toast(String input){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(LoginToAccessPatientInfoServer.this,input,Toast.LENGTH_LONG).show();
            }
        });
    }
}
