package com.example.myfyp;



import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class BiometricAuthenticationActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometricauthentication);
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
                toast("Login success");
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

    private void toast(String input){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(BiometricAuthenticationActivity.this,input,Toast.LENGTH_LONG).show();
            }
        });
    }
}
