package com.example.android.sigahot;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class RincianListReservasi extends AppCompatActivity {
    String kode,status,jsonData,
            cabang,jenis,tanggalPesan,tglmulai,tglselesai,dewasa,anak,permintaan,statusReservasi;
    ConnectionDetector cd;

    private static final String TAG_KODE = "kode_pemesanan";
    private static final String TAG_RESULTS="dataRincianKamar";
    private static final String TAG_JENIS = "jenis";
    private static final String TAG_JUMLAH = "jumlah";
    private static final String TAG_HARGA = "harga";

    TextView txtViewKode,txtViewCabang,txtViewJenisPesan,txtViewTanggalPesan,txtViewTglMasuk,txtViewTglKeluar,txtViewDewasa,txtViewAnak,txtViewPermintaan,txtViewStatus,textViewHeaderStatus;
    ListView list;
    Button  btnBatal,btnUnduh;

    JSONArray listJson = null;
    ArrayList<HashMap<String, String>> listKamar;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.rincian_reservasi);

        listKamar = new ArrayList<HashMap<String,String>>();

        Intent i = getIntent();
        kode= i.getStringExtra(TAG_KODE);
        status = i.getStringExtra("status");

        cd = new ConnectionDetector(this);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        txtViewKode = (TextView) findViewById(R.id.textViewIDPemesanan);
        txtViewCabang = (TextView) findViewById(R.id.textViewCabangDetilReser);
        txtViewJenisPesan = (TextView) findViewById(R.id.textViewJenisPemesanan);
        txtViewTanggalPesan = (TextView) findViewById(R.id.textViewTglReservasi);
        txtViewTglMasuk = (TextView) findViewById(R.id.textViewTglCheckInDetil);
        txtViewTglKeluar = (TextView) findViewById(R.id.textViewTglCheckOutDetil);
        txtViewDewasa = (TextView) findViewById(R.id.textViewDetilDewasa);
        txtViewAnak = (TextView) findViewById(R.id.textViewDetilAnak);
        txtViewPermintaan = (TextView) findViewById(R.id.textViewDetilPermintaan);
        txtViewStatus = (TextView) findViewById(R.id.textViewRincianStatusReservasi);
        textViewHeaderStatus = (TextView) findViewById(R.id.textViewHeaderStatus);

        txtViewKode.setText(kode);

        btnBatal = (Button) findViewById(R.id.buttonBatalReservasi);
        btnUnduh = (Button) findViewById(R.id.buttonUnduhTandaTerima);

        if(status.equals("'Check-In','Batal'")) {
            btnBatal.setVisibility(View.GONE);
            btnUnduh.setVisibility(View.INVISIBLE);

            textViewHeaderStatus.setVisibility(View.VISIBLE);
            txtViewStatus.setVisibility(View.VISIBLE);
            txtViewStatus.setText(statusReservasi);
        }


        list = findViewById(R.id.listViewKamarHarga);
        getDataKamar();
        getDataRincian();

        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                batalReservasi();
            }
        });

        btnUnduh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = Config.url+"tandaTerimaReservasi.php?kode_pemesanan="+kode;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


    }

    private void batalReservasi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah anda yakin ingin membatalkan reservasi ini?");

        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(RincianListReservasi.this);
                dlgAlert.setMessage("Berhasil Membatalkan Reservasi");
                dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateStatusReservasi();
                        finish();
                    } });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });
        builder.setCancelable(true);
        builder.create().show();
    }

    private void updateStatusReservasi() {
        batalkanReservasi br = new batalkanReservasi();
        br.execute();
    }

    class batalkanReservasi extends AsyncTask<Void,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler rh = new RequestHandler();
            HashMap<String,String> add = new HashMap<>();
            add.put(TAG_KODE,kode);
            String res = rh.sendPostRequest(Config.url+"batalkanReservasi.php",add);
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter mAdapter = listView.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += mView.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void getDataRincian() {
        String url = Config.url+"tampilRincianReservasi.php?kode_pemesanan="+kode;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.getMessage().toString(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("dataRincian");
            JSONObject collegeData = result.getJSONObject(0);
            tglmulai = collegeData.getString("tanggal_masuk");
            tglselesai = collegeData.getString("tanggal_keluar");
            tanggalPesan = collegeData.getString("tanggal");
            cabang = collegeData.getString("cabang");
            jenis = collegeData.getString("jenis_pemesanan");
            dewasa = collegeData.getString("jml_dewasa");
            anak = collegeData.getString("jml_anak");
            permintaan = collegeData.getString("permintaan_khusus");
            statusReservasi = collegeData.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy");

            Date date1 = inputFormat.parse(tglmulai);
            Date date2 = inputFormat.parse(tglselesai);
            Date date3 = inputFormat.parse(tanggalPesan);

            String outputDateStr1 = outputFormat.format(date1);
            String outputDateStr2 = outputFormat.format(date2);
            String outputDateStr3 = outputFormat.format(date3);

            txtViewTglMasuk.setText(outputDateStr1);
            txtViewTglKeluar.setText(outputDateStr2);
            txtViewTanggalPesan.setText(outputDateStr3);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }


        txtViewStatus.setText(statusReservasi);
        txtViewCabang.setText(cabang);
        txtViewJenisPesan.setText(jenis);
        txtViewDewasa.setText(dewasa);
        txtViewAnak.setText(anak);
        txtViewPermintaan.setText(permintaan);
    }

    private void getDataKamar() {
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    class GetDataJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());

            HttpPost httppost = new HttpPost(Config.url+"getRincianReservasiKamar.php?kode_pemesanan="+kode);

            // Depends on your web service
            httppost.setHeader("Content-type", "application/json");

            InputStream inputStream = null;
            String result = null;
            try {
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                result = sb.toString();
            } catch (Exception e) {
                // Oops
            }
            finally {
                try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            jsonData = s;
            showList();
        }

    }

    private void showList() {
        try {
            JSONObject jsonObj = new JSONObject(jsonData);
            listJson = jsonObj.getJSONArray(TAG_RESULTS);
            listKamar.clear();
            for(int i=0;i<listJson.length();i++){
                JSONObject c = listJson.getJSONObject(i);
                String jenis = c.getString(TAG_JENIS);
                String jumlah = c.getString(TAG_JUMLAH);
                String harga = c.getString(TAG_HARGA);


                HashMap<String,String> reservasi = new HashMap<String,String>();

                reservasi.put(TAG_JENIS,jenis);
                reservasi.put(TAG_JUMLAH,"x"+jumlah);
                reservasi.put(TAG_HARGA,"Rp "+harga.substring(0,harga.length()-5)+",-");

                listKamar.add(reservasi);
            }

            ListAdapter adapter = new SimpleAdapter(
                    this, listKamar, R.layout.rincian_reservasi_kamar,
                    new String[]{TAG_JENIS,TAG_JUMLAH,TAG_HARGA},
                    new int[]{R.id.txtRincianReservasiJenisKamar, R.id.txtViewRincianReservasiJumlahKamar, R.id.txtRincianReservasiHarga}
            );

            list.setAdapter(adapter);
            setListViewHeightBasedOnChildren(list);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataKamar();
    }


}
