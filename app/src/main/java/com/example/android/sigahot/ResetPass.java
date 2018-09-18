package com.example.android.sigahot;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class ResetPass extends AppCompatActivity {

    EditText txtPassBaru;
    Button btnReset;
    ConnectionDetector cd;
    private String passBaru,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        passBaru = "";
        email = getIntent().getStringExtra("email");
        txtPassBaru = (EditText) findViewById(R.id.editTextPasswordBaru);
        btnReset = (Button) findViewById(R.id.buttonGantiPassBaru);

        cd = new ConnectionDetector(this);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gantiPass();
            }
        });
    }

    public void gantiPass(){
        passBaru = txtPassBaru.getText().toString();
        gantiPasswordPelanggan g = new gantiPasswordPelanggan();
        g.execute();
    }

    class gantiPasswordPelanggan extends AsyncTask<Void,Void,String> {
        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(ResetPass.this, "Mohon Tunggu Sebentar..", "Sedang memproses...");
        }

        @Override
        protected String doInBackground(Void... voids) {
            HashMap<String,String> add = new HashMap<>();
            add.put("kataSandi",passBaru);
            add.put("email",email);

            RequestHandler rh = new RequestHandler();
            String res = rh.sendPostRequest(Config.url+"resetPassword.php",add);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            String s = result.trim();


            loadingDialog.dismiss();

            if(cd.isConnected()){
                if(s.equalsIgnoreCase("success")){
                    Toast.makeText(getApplicationContext(), "Berhasil ubah kata sandi", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ResetPass.this, LoginActivity.class);
                    finish();
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Tidak ada Koneksi", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
