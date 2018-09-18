package com.example.android.sigahot;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    TextView textViewRegistrasi;
    Button btnLogin,btnUbahPass;
    EditText txtEmail,txtPassword;
    private String email,password;
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        SharedPreferences prefs = getSharedPreferences("userdata", MODE_PRIVATE);
        String restoredText = prefs.getString("email_pengguna", null);

        if (restoredText != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        }

        textViewRegistrasi = findViewById(R.id.textViewRegister);
        btnLogin = findViewById(R.id.buttonLogin);
        txtEmail = findViewById(R.id.editTextLoginEmail);
        txtPassword = findViewById(R.id.editTextLoginPassword);
        btnUbahPass = findViewById(R.id.buttonLupaPassword);

        email="";
        password="";

        cd = new ConnectionDetector(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLogin();
            }
        });

        btnUbahPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, UbahPassword.class);


                startActivity(intent);
            }
        });

        textViewRegistrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,RegistrasiActivity.class);
                startActivity(i);
            }
        });
    }

    public void startLogin(){
        email = txtEmail.getText().toString();
        password = txtPassword.getText().toString();

        login(email,password);
    }

    private void login(final String email,String password){

        class loginAsync extends AsyncTask<String,Void,String>{
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(LoginActivity.this, "Mohon Tunggu Sebentar..", "Sedang memproses...");
            }

            @Override
            protected String doInBackground(String... strings) {
                String
                        email = strings[0],
                        pass = strings[1];
                HttpURLConnection conn = null;

                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("kata_sandi", pass));
                String result = null;


                try{
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost(
                            Config.url+"login.php");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();

                    is = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                String s = result.trim();


                loadingDialog.dismiss();

                if(cd.isConnected()){
                    if(s.equalsIgnoreCase("pelanggan")){

                        SharedPreferences.Editor editor = getSharedPreferences("userdata", MODE_PRIVATE).edit();
                        editor.putString("email_pengguna",email);
                        editor.commit();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                        finish();
                        startActivity(intent);
                    } else if (s.equalsIgnoreCase("pegawai")) {
                        SharedPreferences.Editor editor = getSharedPreferences("userdata", MODE_PRIVATE).edit();
                        editor.putString("email_pengguna",email);
                        editor.commit();

                        Intent intent = new Intent(LoginActivity.this, ActivityPegawai.class);

                        finish();
                        startActivity(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), "Email atau Password salah", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this, "Tidak ada Koneksi", Toast.LENGTH_SHORT).show();

                }
            }
        }
        loginAsync la = new loginAsync();
        la.execute(email, password);
    }
}
