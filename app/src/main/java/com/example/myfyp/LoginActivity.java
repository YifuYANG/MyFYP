package com.example.myfyp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myfyp.dbhelper.DBHelper;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ArrayList<String> trustId;
    private FirebaseAuth mAuth;
    EditText username,password;
    Button login,register;
    private LocationRequest locationRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        username=(EditText)findViewById(R.id.username);
        password=(EditText) findViewById(R.id.password);
        login=(Button) findViewById(R.id.login);
        register=(Button) findViewById(R.id.register);
        mAuth=FirebaseAuth.getInstance();
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
    }

    private void Login(){
        String Email=username.getText().toString();
        String pass=password.getText().toString();
        if(Email.isEmpty()){
            Toast.makeText(LoginActivity.this,"Email is required",Toast.LENGTH_LONG).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            Toast.makeText(LoginActivity.this,"Email is not valid",Toast.LENGTH_LONG).show();
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
        mAuth.signInWithEmailAndPassword(Email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    DatabaseReference reference = FirebaseDatabase.getInstance("https://yifuyangfyp-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    reference.addListenerForSingleValueEvent (new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                            String driverlisence=(String) datasnapshot.child("driverlicense").getValue();
                            String password=(String) datasnapshot.child("password").getValue();
                            if(dbHelper.getdatabydevice(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID))!=null){
                                dbHelper.update(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID),driverlisence,password);
                            } else {
                                dbHelper.insertUserInfo(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID),driverlisence,password);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    Intent intent = new Intent(getApplicationContext(),Index.class);
                    startActivity(intent);
                    //CheckTrustDevice(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
                } else {
                    Toast.makeText(LoginActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void AddTrustDevice(){
        DatabaseReference reference = FirebaseDatabase.getInstance("https://yifuyangfyp-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        String deviceId= Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        trustId.add(deviceId);
        reference.child("trustDeviceId").setValue(trustId);
        System.out.println("new trust dvice added"+" -> "+deviceId);
    }

    private void CheckTrustDevice(String device){
        DatabaseReference reference = FirebaseDatabase.getInstance("https://yifuyangfyp-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                trustId=(ArrayList<String>) datasnapshot.child("trustDeviceId").getValue();
                if(!trustId.contains(device)){
                    mAuth.getInstance().signOut();
                } else {
                    System.out.println("this is a trusted device");
                    Intent intent = new Intent(getApplicationContext(),Index.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
