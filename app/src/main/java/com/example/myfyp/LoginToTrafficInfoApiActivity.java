package com.example.myfyp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myfyp.entity.UploadedData;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.ClientProtocolException;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.CloseableHttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.CloseableHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClients;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class LoginToTrafficInfoApiActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logintotrafficinfo);
        Button goback = (Button) findViewById(R.id.gobacktoindex);
        goback.setEnabled(false);
        try {
            SSLConnection(goback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), IndexActivity.class);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("AllowAllHostnameVerifier")
    public void SSLConnection(Button button) throws Exception {
            URL url = new URL("https://10.0.2.2:8083/traffic");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setHostnameVerifier(new AllowAllHostnameVerifier());
            SSLContext sslContext=SSLContext.getInstance("TLS");
            sslContext.init(LoadClientCertificate().getKeyManagers(), LoadServerCertificate().getTrustManagers(), null);
            toast("Authenticating, please wait");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        conn.setSSLSocketFactory(sslContext.getSocketFactory());
                        conn.setReadTimeout(7000);
                        conn.setConnectTimeout(7000);
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.connect();
                        System.out.println("status code : "+conn.getResponseCode());
                        if(conn.getResponseCode()==202){
                            toast("User approved, Connection established");
                            button.setEnabled(true);
                        } else {
                            toast("Error, Invalid certificate");
                            Intent intent = new Intent(getApplicationContext(), IndexActivity.class);
                            startActivity(intent);
                        }
                    } catch (Exception e){
                        System.out.println(e);
                    }
                }
            }).start();
    }

    private TrustManagerFactory LoadServerCertificate() throws Exception {
        //load truststore certificate --> server certificate
        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        trustStore.load(getAssets().open("server.p12"), "961008".toCharArray());
        System.out.println("===============================================");
        System.out.println("Loaded server certificates: " + trustStore.size());
        System.out.println("===============================================");
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        return trustManagerFactory;
    }

    private KeyManagerFactory LoadClientCertificate() throws Exception {
        //load client certificate --> client certificate
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(getAssets().open("client.p12"),"961008".toCharArray());
        System.out.println("===============================================");
        System.out.println("Loaded client certificates: " + keyStore.size());
        System.out.println("===============================================");
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore,"961008".toCharArray());
        return keyManagerFactory;
    }
    private void toast(String input){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(LoginToTrafficInfoApiActivity.this,input,Toast.LENGTH_LONG).show();
            }
        });
    }
}
