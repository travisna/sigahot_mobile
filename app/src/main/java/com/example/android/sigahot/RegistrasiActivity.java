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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.ArrayList;
import java.util.List;

public class RegistrasiActivity extends AppCompatActivity {

    EditText txtNama,txtEmail,txtPassword,txtJawaban;
    Button btnRegistrasi;
    ConnectionDetector cd;
    private RadioGroup radioGroup;
    RadioButton radioButton;

    String nama,email,password,jawaban,jenisKelamin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);



        txtNama = findViewById(R.id.editTextRegNama);
        txtEmail = findViewById(R.id.editTextRegEmail);
        txtPassword = findViewById(R.id.editTextRegPassword);
        txtJawaban = findViewById(R.id.editTextUbahPassPertanyaan);
        btnRegistrasi = findViewById(R.id.buttonRegistrasi);
        radioGroup = findViewById(R.id.radioGroupJenisKelamin);


        nama="";
        email="";
        password="";
        jawaban="";
        jenisKelamin="";

        cd = new ConnectionDetector(this);

        btnRegistrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegistrasi();
            }
        });
    }

    public void startRegistrasi(){
        int cek = 1;

        nama = txtNama.getText().toString();
        email = txtEmail.getText().toString();
        password = txtPassword.getText().toString();
        jawaban = txtJawaban.getText().toString();

        if(nama.length()>50){
            Toast.makeText(RegistrasiActivity.this,"Nama tidak boleh lebih dari 50 karakter", Toast.LENGTH_SHORT).show();
            cek = 0;
        }

        if(password.length()>16){
            Toast.makeText(RegistrasiActivity.this,"Password tidak boleh lebih dari 16 karakter", Toast.LENGTH_SHORT).show();
            cek = 0;
        }

        if(jawaban.length()>16){
            Toast.makeText(RegistrasiActivity.this,"Jawaban pertanyaan unik tidak boleh lebih dari 16 karakter", Toast.LENGTH_SHORT).show();
            cek = 0;
        }

        int idJenisKelamin = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(idJenisKelamin);
        jenisKelamin = radioButton.getText().toString();


        if(cek==1)
            registrasi(nama,email,password,jawaban,jenisKelamin);
    }

    private void registrasi(final String nama, final String email, String password, String jawaban, String jenisK){


        class registrasiAsync extends AsyncTask<String,Void,String>{

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(RegistrasiActivity.this, "Mohon Tunggu Sebentar..", "Sedang memproses...");
            }

            @Override
            protected String doInBackground(String... strings) {
                String
                        namaLengkap = strings[0],
                        emailPengguna = strings[1],
                        pass = strings[2],
                        jawaban = strings[3],
                        jenisKel = strings[4];

                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("nama",namaLengkap));
                nameValuePairs.add(new BasicNameValuePair("email", emailPengguna));
                nameValuePairs.add(new BasicNameValuePair("kata_sandi", pass));
                nameValuePairs.add(new BasicNameValuePair("pertanyaan_unik", jawaban));
                nameValuePairs.add(new BasicNameValuePair("jenis_kelamin", jenisKel));

                String result = null;

                try{
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            Config.url+"registrasi.php");
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

                        SharedPreferences.Editor editor = getSharedPreferences("userdata", MODE_PRIVATE).edit();
                        editor.putString("email_pengguna",email);
                        editor.commit();


                        Intent intent = new Intent(RegistrasiActivity.this, MainActivity.class);
                        finish();
                        Toast.makeText(getApplicationContext(), "Selamat datang"+nama, Toast.LENGTH_LONG).show();
                        startActivity(intent);
                    }else if(s.equalsIgnoreCase("error02")) {
                        Toast.makeText(getApplicationContext(), "Email sudah digunakan oleh pengguna lain", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Terjadi kesalahan", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(RegistrasiActivity.this, "Tidak ada Koneksi", Toast.LENGTH_SHORT).show();

                }
            }
        }
        registrasiAsync ra = new registrasiAsync();
        ra.execute(nama,email,password,jawaban,jenisK);
    }
}
