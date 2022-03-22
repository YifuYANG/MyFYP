package com.example.myfyp;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.myfyp.dbhelper.DBHelperForAccessUploadDistanceServer;
import com.example.myfyp.vo.License;

import com.example.myfyp.vo.LoginformToAccessGetPatientInfoServer;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.Executor;

public class BiometricAuthenticationActivity extends AppCompatActivity {

    private String value;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometricauthentication);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("token");
        }
        Button button = findViewById(R.id.fingerprint_login);
        TextView msg = findViewById(R.id.msg);
        //check if user can use finger print
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS: // we can use biometric sensor
                msg.setText("App can authenticate using biometrics");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE: // device does not have fingerprint sensor
                msg.setText("No biometric features available on this device");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE: // biometric sensor unavailable
                msg.setText("Biometric features are currently unavailable");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                msg.setText("No finger print saved in this device");
                break;
        }


        Executor executor = ContextCompat.getMainExecutor(this); //executor will give the result of the authentication
        BiometricPrompt biometricPrompt = new BiometricPrompt(BiometricAuthenticationActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                //connect to server and extract patient info
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(value!=null){
                                if(ifsucess(value)){
                                    toast("patient info acquired");
                                    Intent intent = new Intent(getApplicationContext(), AcquirePatientInfo.class);
                                    startActivity(intent);
                                }
                            }else {
                                toast("you need to login to access the server");
                                Intent intent = new Intent(getApplicationContext(), LoginToAccessPatientInfoServer.class);
                                startActivity(intent);
                            }
                        } catch (Exception e){
                            toast(e.toString());
                        }
                    }
                }).start();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
        //create biometric dialog box
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login")
                .setDescription("finger print is needed to login")
                .setNegativeButtonText("cancel")
                .build();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
            }
        });
    }

    private Boolean ifsucess(String token){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        HttpHeaders header = new HttpHeaders();
        header.set("token", token);
        HttpEntity<Object> entity=new HttpEntity<>(header);
        return restTemplate.postForObject("http://10.0.2.2:8084/patientInfo",entity,Boolean.class);
    }

    private void toast(String input){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(BiometricAuthenticationActivity.this,input,Toast.LENGTH_LONG).show();
            }
        });
    }
}
