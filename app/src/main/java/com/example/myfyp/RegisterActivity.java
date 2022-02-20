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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;


public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText driverid,password,repassword,Email;
    Button register;
    //DBHelper userinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();
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
        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Userinfo userinfo=new Userinfo(driverlicense,email,trustDeviceId,pass);
                            FirebaseDatabase.getInstance("https://yifuyangfyp-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(userinfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this,"Registered successful",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(RegisterActivity.this,"Registered unsuccessful",Toast.LENGTH_LONG).show();
                                        System.out.println(task.getException());
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this,task.getException().toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}