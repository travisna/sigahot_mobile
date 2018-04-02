package com.example.android.sigahot;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by TRAVIS'S COMP on 26/03/2018.
 */

public class UbahProfil extends Fragment {

    String nama="",tgl="",noIdentitas="",noTelpon="",alamat="";
    EditText txtTanggal,txtNama,txtNoIdentitas,txtNoTelpon,txtAlamat;
    Calendar calendar;
    Button btnUbah;
    ConnectionDetector cd;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.ubah_profil, container, false);
    }


    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Ubah Profil");

        Bundle b = getArguments();

        nama = b.getString("nama");
        noIdentitas = b.getString("noIdentitas");
        noTelpon = b.getString("noTelepon");
        alamat = b.getString("alamat");
        tgl = b.getString("tanggalLahir");


        cd = new ConnectionDetector(getActivity());

        txtNama = (EditText) getView().findViewById(R.id.txtUbahNama);
        txtNoIdentitas = (EditText) getView().findViewById(R.id.txtUbahNoIdent);
        txtNoTelpon = (EditText) getView().findViewById(R.id.txtUbahNoTelpon);
        txtAlamat = (EditText) getView().findViewById(R.id.txtUbahAlamat);
        btnUbah = (Button) getView().findViewById(R.id.btnUbah);

        calendar = Calendar.getInstance();
        txtTanggal = (EditText) getView().findViewById(R.id.txtUbahTanggal);

        setData();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };


        txtTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnUbah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ubahProfil();
            }
        });

    }

    private void setData(){
        txtNama.setText(nama);

        if(nama.equals("null"))
            txtAlamat.setText("");
        else
            txtAlamat.setText(alamat);

        if(noTelpon.equals("null"))
            txtNoTelpon.setText("");
        else
            txtNoTelpon.setText(noTelpon);

        if(noIdentitas.equals("null"))
            txtNoIdentitas.setText("");
        else
            txtNoIdentitas.setText(noIdentitas);

        if(tgl.equals("null"))
            txtTanggal.setText("");
        else
            txtTanggal.setText(tgl);
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        txtTanggal.setText(sdf.format(calendar.getTime()));
    }

    private void ubahProfil(){
        final String namaUbah = txtNama.getText().toString(),
                alamatUbah = txtAlamat.getText().toString(),
                noTelponUbah = txtNoTelpon.getText().toString(),
                noIdenUbah = txtNoIdentitas.getText().toString(),
                tglUbah = txtTanggal.getText().toString();

        class ubahProfilPelanggan extends AsyncTask<Void,Void,String>{
            private Dialog loadingDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(getActivity(), "Mohon Tunggu Sebentar..", "Sedang memproses...");

            }

            @Override
            protected String doInBackground(Void... voids) {
                SharedPreferences prefs = getActivity().getSharedPreferences("userdata", MODE_PRIVATE);
                final String restoredText = prefs.getString("email_pengguna", null);
                HashMap<String,String> add = new HashMap<>();
                add.put("email",restoredText);
                add.put("no_identitas",noIdenUbah.toString().trim());
                add.put("alamat",alamatUbah.toString().trim());
                add.put("nama",namaUbah.toString().trim());
                add.put("nomor_telepon",noTelponUbah.toString().trim());
                add.put("tanggal_lahir",tglUbah.toString().trim());


                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.url+"updatePelanggan.php",add);
                return res;
            }

            @Override
            protected void onPostExecute(String result) {
                String s = result.trim();


                loadingDialog.dismiss();

                if(cd.isConnected()){
                    if(s.equalsIgnoreCase("success")){
                        Fragment fragment = new DetilProfil();

                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container,fragment);

                        ft.commit();


                    }else {
                        Toast.makeText(getActivity(), "Terjadi Kesalahan", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(getActivity(), "Tidak ada Koneksi", Toast.LENGTH_SHORT).show();

                }
            }
        }

        ubahProfilPelanggan ubahP = new ubahProfilPelanggan();
        ubahP.execute();
    }
}
