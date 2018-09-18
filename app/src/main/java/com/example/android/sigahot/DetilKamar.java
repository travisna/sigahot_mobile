package com.example.android.sigahot;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import org.w3c.dom.Text;

/**
 * Created by trav_na on 3/31/18.
 */

public class DetilKamar extends Fragment {

    ImageView imgKamar;
    TextView txtLuas,txtPemandangan,txtJumlahBed1,txtJumlahBed2,txtJumlahBed3,txtHarga;
    String luas,pemandangan,jumlahBedKing,jumlahBedDouble,jumlahBedTwin,harga,jenisKamar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.detil_kamar,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle b = getArguments();
        jenisKamar=b.getString("jenis_kamar");

        luas="";
        pemandangan="";
        jumlahBedTwin="";
        jumlahBedKing="";
        jumlahBedDouble="";
        harga="";


        txtLuas = (TextView) getView().findViewById(R.id.txtViewLuas);
        txtPemandangan = (TextView) getView().findViewById(R.id.txtViewPemandangan);
        txtJumlahBed1 = (TextView) getView().findViewById(R.id.txtJumlahBed1);
        txtJumlahBed2 = (TextView) getView().findViewById(R.id.txtJumlahBed2);
        txtJumlahBed3 = (TextView) getView().findViewById(R.id.txtJumlahBed3);
        txtHarga = (TextView) getView().findViewById(R.id.textViewHarga);


        imgKamar = (ImageView) getView().findViewById(R.id.imageViewKamar);
        Drawable drawable;

        if(jenisKamar.equals("Superior")) {
            drawable = getResources().getDrawable(R.drawable.superior);
            imgKamar.setImageDrawable(drawable);
        }
        else if(jenisKamar.equals("Double Deluxe"))
        {
            drawable = getResources().getDrawable(R.drawable.double_deluxe);
            imgKamar.setImageDrawable(drawable);
        }
        else if(jenisKamar.equals("Executive Deluxe"))
        {
            drawable = getResources().getDrawable(R.drawable.executiv_deluxe);
            imgKamar.setImageDrawable(drawable);
        }
        else {
            drawable = getResources().getDrawable(R.drawable.junior_suite);
            imgKamar.setImageDrawable(drawable);
        }

        setData();

    }

    public void setData(){
        String url = Config.url+"getDataKamar.php?jenis_kamar="+jenisKamar;

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
            JSONArray result = jsonObject.getJSONArray("dataKamar");
            JSONObject data = result.getJSONObject(0);

            luas = data.getString("luas");
            pemandangan = data.getString("pemandangan");
            jumlahBedTwin = data.getString("jumlah_twin");
            jumlahBedKing = data.getString("jumlah_king");
            jumlahBedDouble = data.getString("jumlah_double");
            harga = data.getString("harga");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        txtHarga.setText("Harga :Rp "+harga.substring(0,harga.length()-5)+",-");
        txtPemandangan.setText("Pemandangan : "+pemandangan);

        txtJumlahBed1.setText("Jumlah Twin  : "+jumlahBedTwin);
        txtJumlahBed2.setText("Jumlah Double    : "+jumlahBedDouble);
        txtJumlahBed3.setText("Jumlah King  : "+jumlahBedKing);
        String kuadrat="m<sup>2</sup>";
        txtLuas.setText("Luas   : "+luas.substring(0,luas.length()-2)+Html.fromHtml(kuadrat));

    }




}
