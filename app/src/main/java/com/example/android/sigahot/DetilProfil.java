package com.example.android.sigahot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by TRAVIS'S COMP on 27/03/2018.
 */

public class DetilProfil extends Fragment {

    Button btnUbahProfil;
    String
            nama="",
            email="",
            noIdentitas="",
            noTelpon="",
            alamat="",
            tglLahir="";
    TextView txtNama,txtNoIdentitas,txtEmail,txtNoTelpon,txtAlamat,txtTanggalLahir;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.detil_profil, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Rincian Profil");

        txtNama = (TextView) getView().findViewById(R.id.txtDetilNama);
        txtEmail = (TextView) getView().findViewById(R.id.txtDetilEmail);
        txtNoIdentitas = (TextView) getView().findViewById(R.id.txtDetilNoIdent);
        txtNoTelpon = (TextView) getView().findViewById(R.id.txtDetilNoTelpon);
        txtAlamat = (TextView) getView().findViewById(R.id.txtDetilAlamat);
        txtTanggalLahir = (TextView) getView().findViewById(R.id.txtDetilTanggal);
        btnUbahProfil = (Button) getView().findViewById(R.id.btnUbahProfil);

        getDatas();

        btnUbahProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment ubahFragment = new UbahProfil();

                Bundle bundle = new Bundle();
                bundle.putString("nama",nama);
                bundle.putString("noIdentitas",noIdentitas);
                bundle.putString("noTelepon",noTelpon);
                bundle.putString("alamat",alamat);
                bundle.putString("tanggalLahir",txtTanggalLahir.getText().toString());

                ubahFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container,ubahFragment);

                ft.commit();
            }
        });
        getActivity().setTitle("Rincian Profil");
    }

    private void getDatas() {
        SharedPreferences prefs = getActivity().getSharedPreferences("userdata", MODE_PRIVATE);
        final String restoredText = prefs.getString("email_pengguna", null);
        String url = Config.url+"getDataPengguna.php?email="+restoredText.toString().trim();

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),error.getMessage().toString(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    private void showJSON(String response){

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("dataPengguna");
            JSONObject data = result.getJSONObject(0);

            nama = data.getString("nama");
            email = data.getString("email");
            noIdentitas = data.getString("no_identitas");
            noTelpon = data.getString("nomor_telepon");
            tglLahir = data.getString("tanggal_lahir");
            alamat = data.getString("alamat");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        txtNama.setText(nama);
        txtEmail.setText(email);

        if(noIdentitas.equals("null"))
            txtNoIdentitas.setText(R.string.belum_ada);
        else
            txtNoIdentitas.setText(noIdentitas);//

        if(noTelpon.equals("null"))
            txtNoTelpon.setText(R.string.belum_ada);
        else
            txtNoTelpon.setText(noTelpon);//

        if(alamat.equals("null"))
            txtAlamat.setText(R.string.belum_ada);
        else
            txtAlamat.setText(alamat);//

        if(tglLahir.equals("null"))
            txtTanggalLahir.setText(R.string.belum_ada);
        else {
            try {
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                Date date = inputFormat.parse(tglLahir);
                String outputDateStr = outputFormat.format(date);
                txtTanggalLahir.setText(outputDateStr);
            } catch (ParseException e) {

            }
        }
    }
}
