package com.example.myfyp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        Button id = (Button) findViewById(R.id.idofdevice);
        id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OverTakeActivity.class);
                startActivity(intent);
            }
        });

        Button acquireinfo = (Button) findViewById(R.id.acquireinfo);
        acquireinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AcquireTrafficActivity.class);
                startActivity(intent);
            }
        });

        Button acquirepatientinfo = (Button) findViewById(R.id.acquirepatientinfo);
        acquirepatientinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginToAccessPatientInfoServer.class);
                startActivity(intent);
            }
        });
    }
}
