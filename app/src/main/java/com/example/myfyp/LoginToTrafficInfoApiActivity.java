package com.example.myfyp;

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
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.CloseableHttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.CloseableHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClients;

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
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

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
                try {
                    test();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private final static String MY_URL = "https://10.0.2.2:8083/traffic";
    private void logintoserver() throws FileNotFoundException {
    }
    private void test() throws Exception {

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(getAssets().open("client.p12"),"961008".toCharArray());
        TrustManagerFactory trustManagerFactory=TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        try {
            TrustManager[] trustManager = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };
            URL url = new URL("https://10.0.2.2:8083/traffic");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            SSLContext sslContext=SSLContext.getInstance("TLS");
            sslContext.init(null, trustManager, new SecureRandom());

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
                        System.out.println(conn.getInputStream());
//                        Socket socket=sslContext.getSocketFactory().createSocket("10.0.2.2",8083);
//                        socket.setKeepAlive(true);
//                        System.out.println(socket.getInputStream().read());
                    } catch (Exception e){
                        System.out.println("this is my fucking exception-->"+e);
                    }
                }
            }).start();
        } catch (Exception e){
            System.out.println("this is my second fking e-------->"+e);
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
