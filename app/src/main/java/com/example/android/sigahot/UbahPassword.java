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
import java.util.ArrayList;
import java.util.List;

public class UbahPassword extends AppCompatActivity {

    EditText txtJawaban,txtEmail;
    String jawaban,email;
    Button btnKonfirmasi;
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_password);

        jawaban ="";
        email="";
        cd = new ConnectionDetector(this);
        txtEmail = (EditText) findViewById(R.id.editTextEmailLupaPass);
        txtJawaban = (EditText) findViewById(R.id.editTextUbahPassPertanyaan);

        btnKonfirmasi = (Button) findViewById(R.id.buttonKonfirmLupaPass);

        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lupaPass();
                konfimasiLupaPass k = new konfimasiLupaPass();
                k.execute(jawaban,email);
            }
        });
    }

    public void lupaPass(){
        jawaban = txtJawaban.getText().toString();
        email = txtEmail.getText().toString();
    }

    class konfimasiLupaPass extends AsyncTask<String,Void,String>{

        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(UbahPassword.this, "Mohon Tunggu Sebentar..", "Sedang memproses...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String
                    jawaban = strings[0],
                    email = strings[1];
            HttpURLConnection conn = null;

            InputStream is = null;
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("jawaban", jawaban));
            nameValuePairs.add(new BasicNameValuePair("email", email));

            String result = null;


            try{
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(
                        Config.url+"konfirmasiLupaPass.php");
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
                if(s.equalsIgnoreCase("success")){



                    Intent intent = new Intent(UbahPassword.this, ResetPass.class);
                    intent.putExtra("email",email);

                    finish();
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "Email atau Jawaban anda salah", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(UbahPassword.this, "Tidak ada Koneksi", Toast.LENGTH_SHORT).show();

            }
        }
    }

}
