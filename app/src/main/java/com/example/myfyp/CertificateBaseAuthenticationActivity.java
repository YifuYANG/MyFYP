package com.example.myfyp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class CertificateBaseAuthenticationActivity extends AppCompatActivity {


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
    private void SSLConnection(Button button) throws Exception {
            URL url = new URL("https://10.0.2.2:8083/traffic");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setHostnameVerifier(new AllowAllHostnameVerifier());
        final TrustManager[] trustAllCerts = new TrustManager[]{

                new X509TrustManager() {

                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
            SSLContext sslContext=SSLContext.getInstance("TLS");
            //present two type of SSL authentication certificates 1. keystore : self sign certificate 2. sslopen : certificate chain
            //sslContext.init(LoadClientCertificate().getKeyManagers(), LoadServerCertificate().getTrustManagers(), null);
            sslContext.init(LoadClientCertificate().getKeyManagers(), trustAllCerts, null);
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
                        }
                    } catch (Exception e){
                        try {
                            if(conn.getResponseCode()!=202) {
                                toast("Error, Server does not response");
                                Intent intent = new Intent(getApplicationContext(), IndexActivity.class);
                                startActivity(intent);
                            }
                        } catch (IOException ioException) {
                            toast("Error, Server does not response");
                            Intent intent = new Intent(getApplicationContext(), IndexActivity.class);
                            startActivity(intent);
                        }
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
        keyStore.load(getAssets().open("client_1.p12"),"961008".toCharArray());
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
                Toast.makeText(CertificateBaseAuthenticationActivity.this,input,Toast.LENGTH_LONG).show();
            }
        });
    }
}
