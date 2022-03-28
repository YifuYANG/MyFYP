package com.example.myfyp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfyp.dbhelper.DBHelperForAccessUploadDistanceServer;

import com.example.myfyp.vo.LoginformToAccessUploadDistanceServer;
import com.example.myfyp.vo.UploadedData;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executor;

public class LoginToUploadDistanceServerActivity extends AppCompatActivity {

    private DBHelperForAccessUploadDistanceServer dbHelperForAccessUploadDistanceServer;
    private ArrayList<String> trustId;
    EditText username,password;
    Button login,register;
    private String value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logintouploaddistanceserver);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
           value = extras.getString("key");
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
        BiometricPrompt biometricPrompt = new BiometricPrompt(LoginToUploadDistanceServerActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Intent intent = new Intent(getApplicationContext(), OverTakeActivity.class);
                if(value!=null&&value.equals("needlogintoauthedtouploaddata")){
                    intent.putExtra("key", "loginpassed");
                    value=null;
                    startActivity(intent);
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
        //create biometric dialog box
        BiometricPrompt.PromptInfo promptInfo;

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login")
                .setDescription("finger print is needed to login")
                .setNegativeButtonText("Cancel")
                .build();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
            }
        });
    }
    private void toast(String input){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(LoginToUploadDistanceServerActivity.this,input,Toast.LENGTH_LONG).show();
            }
        });
    }
}
