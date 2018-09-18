package com.example.android.sigahot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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

import static android.content.Context.MODE_PRIVATE;

public class ListReservasi extends ListFragment {
    String jsonData,status;

    private static final String TAG_RESULTS="dataList";
    private static final String TAG_KODE = "kode_pemesanan";
    private static final String TAG_CABANG = "cabang";
    private static final String TAG_TGLCHECKIN ="tanggal_masuk";
    private static final String TAG_TGLCHECKOUT ="tanggal_keluar";

    JSONArray listJson = null;
    ArrayList<HashMap<String, String>> listReservasi;
    ListView list;

    public ListReservasi(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listReservasi = new ArrayList<HashMap<String,String>>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.riwayat_reservasi, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);

        list = (ListView) getView().findViewById(R.id.listRiwayatReservasi);

        Bundle b = getArguments();
        status = b.getString("status");

        getData();
    }

    private void getData() {
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    class GetDataJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            SharedPreferences prefs = getActivity().getSharedPreferences("userdata", MODE_PRIVATE);
            final String restoredText = prefs.getString("email_pengguna", null);
            HttpPost httppost = new HttpPost(Config.url+"tampilListReservasi.php?email="+restoredText+"&status_reservasi="+status);

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
        String tglCheckIn="",tglCheckOut="";
        try {
            JSONObject jsonObj = new JSONObject(jsonData);
            listJson = jsonObj.getJSONArray(TAG_RESULTS);
            listReservasi.clear();
            for(int i=0;i<listJson.length();i++){
                JSONObject c = listJson.getJSONObject(i);
                String kode = c.getString(TAG_KODE);
                String cabang = c.getString(TAG_CABANG);

                try {
                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                    Date date1 = inputFormat.parse(c.getString(TAG_TGLCHECKIN));
                    Date date2 = inputFormat.parse(c.getString(TAG_TGLCHECKOUT));

                    tglCheckIn = outputFormat.format(date1);
                    tglCheckOut = outputFormat.format(date2);
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }

                HashMap<String,String> reservasi = new HashMap<String,String>();

                reservasi.put(TAG_KODE,kode);
                reservasi.put(TAG_TGLCHECKIN,tglCheckIn);
                reservasi.put(TAG_TGLCHECKOUT,tglCheckOut);
                reservasi.put(TAG_CABANG,cabang);

                listReservasi.add(reservasi);
            }

            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), listReservasi, R.layout.item_riwayat_reservasi,
                    new String[]{TAG_KODE,TAG_CABANG,TAG_TGLCHECKIN,TAG_TGLCHECKOUT},
                    new int[]{R.id.txtKodePemesananList, R.id.txtViewCabangList, R.id.txtTglMasukList, R.id.txtTglKeluarList}
            );

            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String idd = ((TextView) view.findViewById(R.id.txtKodePemesananList)).getText().toString();

                    Intent in = new Intent(getActivity(), RincianListReservasi.class);
                    in.putExtra(TAG_KODE, idd);
                    in.putExtra("status",status);
                    startActivity(in);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
}
