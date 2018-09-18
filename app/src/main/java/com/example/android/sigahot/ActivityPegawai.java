package com.example.android.sigahot;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;

public class ActivityPegawai extends AppCompatActivity {

    Spinner spinnerTahun;
    Button btnLaporan5Pelanggan,btnLaporanPelangganBaru,btnKeluar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pegawai);

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2015; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, years);

        spinnerTahun = findViewById(R.id.spinnerTahun);
        spinnerTahun.setAdapter(adapter);

        btnLaporanPelangganBaru = (Button) findViewById(R.id.btnLaporanPelangganBaru);
        btnLaporan5Pelanggan = (Button) findViewById(R.id.btnLaporan5Tamu);
        btnKeluar = (Button) findViewById(R.id.buttonLogOutPegawai);

        btnLaporan5Pelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = Config.url+"laporan5Customer.php?tahun="+spinnerTahun.getSelectedItem().toString();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        btnLaporanPelangganBaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = Config.url+"laporanCustomerBaru.php?tahun="+spinnerTahun.getSelectedItem().toString();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        btnKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityPegawai.this);
                builder.setTitle("Konfirmasi");
                builder.setMessage("Apakah anda yakin ingin keluar?")
                        .setCancelable(false)
                        .setPositiveButton("Tidak", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                //Set the fragment initially

                            }
                        })
                        .setNegativeButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityPegawai.this.finish();
                                SharedPreferences preferences = getSharedPreferences("userdata", getApplicationContext().MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.commit();
                                Intent in = new Intent(ActivityPegawai.this, LoginActivity.class);
                                startActivity(in);

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

}
