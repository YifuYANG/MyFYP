package com.example.myfyp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myfyp.entity.UploadedData;
import com.example.myfyp.vo.LoginformToAccessTrafficInfoServer;
import com.example.myfyp.vo.LoginformToAccessUploadDistanceServer;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class LoginToTrafficInfoApiActivity extends AppCompatActivity {

    EditText username,password;
    Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logintotrafficinfo);
        username=(EditText)findViewById(R.id.username);
        password=(EditText) findViewById(R.id.password);
        login=(Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logintoserver();
            }
        });
    }

    private void logintoserver(){
        String user=username.getText().toString();
        String pass=password.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    String token= restTemplate.postForObject("http://10.0.2.2:8083/login",new LoginformToAccessTrafficInfoServer(user,pass),Map.class).get("token").toString();
                    accessTraffic("login successfully following token established from server ->"+token);
                } catch (Exception e){
                    System.out.println(e);
                }
            }
        }).start();
    }

    private void accessTraffic(String token){
        if(token!=null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        RestTemplate restTemplate = new RestTemplate();
                        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                        HttpHeaders header = new HttpHeaders();
                        header.set("token", token);
                        HttpEntity<UploadedData> request=new HttpEntity<>(null,header);
                        Boolean ifaccess=restTemplate.postForObject("http://10.0.2.2:8083/traffic",request,Boolean.class);
                        if(ifaccess){
                            toast("Traffic info received");
                            Intent intent = new Intent(getApplicationContext(), IndexActivity.class);
                            startActivity(intent);
                        } else {
                            toast("Unable to access database");
                        }
                    } catch (Exception e){
                        System.out.println(e);
                    }
                }
            }).start();
        } else {
            System.out.println("un login user");
        }
    }

    private void toast(String input){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(LoginToTrafficInfoApiActivity.this,input,Toast.LENGTH_LONG).show();
            }
        });
    }
}
