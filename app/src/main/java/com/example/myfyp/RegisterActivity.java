package com.example.myfyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import java.util.ArrayList;


public class RegisterActivity extends AppCompatActivity {

    EditText driverid,password,repassword,Email;
    Button register;
    //DBHelper userinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        driverid=(EditText)findViewById(R.id.driverid);
        password=(EditText) findViewById(R.id.password);
        repassword=(EditText) findViewById(R.id.repassword);
        Email=(EditText) findViewById(R.id.email);
        register=(Button) findViewById(R.id.register);
        //userinfo = new DBHelper(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register();
            }
        });
    }
    private void Register(){
        String driverlicense=driverid.getText().toString();
        String email=Email.getText().toString();
        String pass=password.getText().toString();
        String repass=repassword.getText().toString();
        ArrayList<String> trustDeviceId=new ArrayList<>();
        ArrayList<String> DriverId=new ArrayList<>();
        trustDeviceId.add("This is a list of trusted devices");
        if(driverlicense.equals("")){
            Toast.makeText(RegisterActivity.this,"Driver license is required",Toast.LENGTH_LONG).show();
            return;
        }
        if(pass.equals("")){
            Toast.makeText(RegisterActivity.this,"password is required",Toast.LENGTH_LONG).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(RegisterActivity.this,"Insufficient Email form",Toast.LENGTH_LONG).show();
            return;
        }
        if(!pass.equals(repass)){
            Toast.makeText(RegisterActivity.this,"passwords no match",Toast.LENGTH_LONG).show();
            return;
        }
    }
}