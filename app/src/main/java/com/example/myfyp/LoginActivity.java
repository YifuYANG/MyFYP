package com.example.myfyp;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myfyp.dbhelper.DBHelper;

import com.example.myfyp.vo.LoginformToAccessUploadDistanceServer;
import com.example.myfyp.vo.UploadedData;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ArrayList<String> trustId;
    EditText username,password;
    Button login,register;
    private String value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username=(EditText)findViewById(R.id.username);
        password=(EditText) findViewById(R.id.password);
        login=(Button) findViewById(R.id.login);
        register=(Button) findViewById(R.id.register);
        dbHelper = new DBHelper(this);
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
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
           value = extras.getString("key");
        }
    }

    private void Login(){
        String Email=username.getText().toString();
        String pass=password.getText().toString();
        if(Email.isEmpty()){
            Toast.makeText(LoginActivity.this,"Email is required",Toast.LENGTH_LONG).show();
            return;
        }
        if(pass.isEmpty()){
            Toast.makeText(LoginActivity.this,"Password os required",Toast.LENGTH_LONG).show();
            return;
        }
        if(pass.length()<6){
            Toast.makeText(LoginActivity.this,"Min password length is 6",Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    String token= restTemplate.postForObject("http://10.0.2.2:8081//authentication/pass",new LoginformToAccessUploadDistanceServer(Email,pass), Map.class).get("token").toString();
                    if(token!=null){
                        HttpHeaders header = new HttpHeaders();
                        header.set("token", token);
                        HttpEntity<UploadedData> entity_2=new HttpEntity<>(null,header);
                        String driverlisence =restTemplate.postForObject("http://10.0.2.2:8081/license",entity_2,Map.class).get("license").toString();
                        if(dbHelper.getsize()==0){
                            dbHelper.insertUserInfo(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID),driverlisence,pass);
                        } else {
                            if(dbHelper.getdatabydevice(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID))!=null){
                                dbHelper.update(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID),driverlisence,pass);
                            } else {
                                dbHelper.insertUserInfo(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID),driverlisence,pass);
                            }
                        }
                        Intent intent = new Intent(getApplicationContext(), OverTakeActivity.class);
                        if(value!=null&&value.equals("needlogintoauthedtouploaddata")){
                            intent.putExtra("key", "loginpassed");
                            value=null;
                        }

                        startActivity(intent);
                    } else {
                        toast("unable to login");
                    }
                } catch (Exception e){
                    System.out.println(e);
                }
            }
        }).start();
    }
    private void toast(String input){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(LoginActivity.this,input,Toast.LENGTH_LONG).show();
            }
        });
    }
}
