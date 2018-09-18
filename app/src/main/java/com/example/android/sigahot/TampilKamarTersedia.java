package com.example.android.sigahot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class TampilKamarTersedia extends Fragment {

    View viewExecutive,viewDouble,viewJunior,viewSuperior;
    TextView txtViewExeLuas,txtViewExeHarga,txtViewExeSisa,
            txtViewDouLuas,txtViewDouHarga,txtViewDouSisa,
            txtViewJunLuas,txtViewJunHarga,txtViewJunSisa,
            txtViewSuLuas,txtViewSuHarga,txtViewSuSisa;

    String exeLuas="",exeHarga="",exeSisa="",
        douLuas="",douHarga="",douSisa="",
        junLuas="",junHarga="",junSisa="",
        suLuas="",suHarga="",suSisa="",
        tglCheckIn="",tglCheckOut="",cabang="",
        jmlSu,jmlExe,jmlDou,jmlJun;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.tampil_kamar_tersedia,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle b = getArguments();
        tglCheckIn= b.getString("tgl_masuk");
        tglCheckOut = b.getString("tgl_keluar");
        cabang = b.getString("cabang");
        getJumlahKamar();

        viewExecutive = getView().findViewById(R.id.executiveCari);
        txtViewExeHarga = (TextView) viewExecutive.findViewById(R.id.textViewHargaCariExecutive);
        txtViewExeLuas = (TextView) viewExecutive.findViewById(R.id.textViewLuasCariExecutive);
        txtViewExeSisa = (TextView) viewExecutive.findViewById(R.id.textViewsSisaExecutive);

        viewDouble = getView().findViewById(R.id.doubleDeluxeCari);
        txtViewDouLuas = (TextView) viewDouble.findViewById(R.id.textViewLuasCariDoubleDeluxe);
        txtViewDouHarga = (TextView) viewDouble.findViewById(R.id.textViewHargaCariDoubleDluxe);
        txtViewDouSisa = (TextView) viewDouble.findViewById(R.id.textViewsSisaDoubleDeluxe);

        viewJunior = getView().findViewById(R.id.juniorCari);
        txtViewJunHarga = (TextView) viewJunior.findViewById(R.id.textViewHargaCariJuniorSuite);
        txtViewJunSisa = (TextView) viewJunior.findViewById(R.id.textViewsSisaJuniorSuite);
        txtViewJunLuas = (TextView) viewJunior.findViewById(R.id.textViewLuasCariJuniorSuite);

        viewSuperior = getView().findViewById(R.id.superiorCari);
        txtViewSuHarga = (TextView) viewSuperior.findViewById(R.id.textViewHargaCariSuperior);
        txtViewSuLuas = (TextView) viewSuperior.findViewById(R.id.textViewLuasCariSuperior);
        txtViewSuSisa = (TextView) viewSuperior.findViewById(R.id.textViewsSisaSuperior);




        setDataLuasHarga("Superior");
        setDataLuasHarga("Executive Deluxe");
        setDataLuasHarga("Junior Suite");
        setDataLuasHarga("Double Deluxe");



        setDataSisa();
    }



    private void setDataLuasHarga(final String jenis) {
        String url = Config.url+"getDataKamar.php?jenis_kamar="+jenis;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSONLuasHarga(response,jenis);
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

    private void showJSONLuasHarga(String response,String jenis){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("dataKamar");
            JSONObject data = result.getJSONObject(0);

            switch (jenis){
                    case "Superior" :
                        suHarga = data.getString("harga");
                        suLuas = data.getString("luas");
                        break;
                    case "Executive Deluxe" :
                        exeHarga = data.getString("harga");
                        exeLuas = data.getString("luas");
                        break;
                    case "Double Deluxe" :
                        douHarga = data.getString("harga");
                        douLuas = data.getString("luas");
                        break;
                    case "Junior Suite" :
                        junHarga = data.getString("harga");
                        junLuas = data.getString("luas");
                        break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        switch (jenis){
            case "Superior" :
                txtViewSuHarga.setText("Harga :Rp "+suHarga.substring(0,suHarga.length()-5)+",-");
                txtViewSuLuas.setText("Luas   : "+suLuas.substring(0,suLuas.length()-2)+"m");
                break;
            case "Executive Deluxe" :
                txtViewExeHarga.setText("Harga :Rp "+exeHarga.substring(0,exeHarga.length()-5)+",-");
                txtViewExeLuas.setText("Luas   : "+exeLuas.substring(0,exeLuas.length()-2)+"m");
                break;
            case "Double Deluxe" :
                txtViewDouHarga.setText("Harga :Rp "+douHarga.substring(0,douHarga.length()-5)+",-");
                txtViewDouLuas.setText("Luas   : "+douLuas.substring(0,douLuas.length()-2)+"m");
                break;
            case "Junior Suite" :
                txtViewJunHarga.setText("Harga :Rp "+junHarga.substring(0,junHarga.length()-5)+",-");
                txtViewJunLuas.setText("Luas   : "+junLuas.substring(0,junLuas.length()-2)+"m");
                break;
        }

    }



    private void getJumlahKamar() {
        String url = Config.url+"getJumlahKamar.php?cabang="+cabang;

        System.out.println(cabang);
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("tes1","tes");
                showJSONJumlah(response);
                Log.v("tes2","tes");
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(),error.getMessage().toString(),Toast.LENGTH_LONG).show();
                        System.out.println("tes_error"+error.getMessage());
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void showJSONJumlah(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("dataJumlahKamar");
            JSONObject data = result.getJSONObject(0);
            Log.v("tes1",jsonObject.toString());

            jmlSu = data.getString("jumlah_superior");
            jmlDou = data.getString("jumlah_doubleDeluxe");
            jmlExe = data.getString("jumlah_ExecutiveDeluxe");
            jmlJun = data.getString("jumlah_JuniorSuite");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setDataSisa() {

        String url = Config.url+"cariKamar.php?tgl_masuk="+tglCheckIn+"&tgl_keluar="+tglCheckOut+"&cabang="+cabang;
        System.out.println(tglCheckIn);
        System.out.println(tglCheckOut);

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSONSisa(response);
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

    private void showJSONSisa(String response){

        String dou="",exe="",jun="",su="";
        int sisaDou,sisaExe,sisaJun,sisaSu;

        try {
            JSONObject jsonObject = new JSONObject(response);

            JSONArray result = jsonObject.getJSONArray("dataCariKamar");
            JSONObject data = result.getJSONObject(0);
            Log.v( "tes3",data.toString());

            dou = data.getString("double_deluxe");
            exe = data.getString("executive_deluxe");
            jun = data.getString("junior_suite");
            su = data.getString("superior");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        sisaDou = Integer.valueOf(jmlDou) - Integer.valueOf(dou);
        sisaExe = Integer.valueOf(jmlExe) - Integer.valueOf(exe);
        sisaJun = Integer.valueOf(jmlJun) - Integer.valueOf(jun);
        sisaSu = Integer.valueOf(jmlSu) - Integer.valueOf(su);

        System.out.println("sisa"+sisaDou);
        System.out.println("jumlah"+jmlDou);
        System.out.println("cari"+dou);

        txtViewSuSisa.setText("Sisa Kamar   : "+String.valueOf(sisaSu));
        txtViewJunSisa.setText("Sisa Kamar   : "+String.valueOf(sisaJun));
        txtViewExeSisa.setText("Sisa Kamar   : "+String.valueOf(sisaExe));
        txtViewDouSisa.setText("Sisa Kamar   : "+String.valueOf(sisaDou));
    }




}
