package com.example.android.sigahot;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.savvi.rangedatepicker.CalendarPickerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CariKamar extends android.support.v4.app.Fragment {

    CalendarPickerView calendar;
    Button btnCari;
    private RadioGroup radioGroup;
    RadioButton radioButton;
    String cabang;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.cari_kamar,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        final Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 10);

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -10);

        cabang = "";

        calendar = (CalendarPickerView) getView().findViewById(R.id.calendar_view);

        calendar.init(lastYear.getTime(), nextYear.getTime())//
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDate(new Date());

        btnCari = (Button) getView().findViewById(R.id.buttonCariKamar);


        radioGroup = (RadioGroup) getView().findViewById(R.id.radioGroupCabang);


        btnCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startCari();
            }
        });

    }

    private void startCari(){
        List<Date> rangeTanggal = calendar.getSelectedDates();
        Date c = Calendar.getInstance().getTime();
        Date tglCheckIn = rangeTanggal.get(0);
        Date tglCheckOut = rangeTanggal.get(rangeTanggal.size()-1);
        Bundle b = new Bundle();


        if(tglCheckIn.compareTo(c)<0)
            Toast.makeText(getContext(),"Tanggal masuk tidak boleh kurang dari tanggal sekarang",Toast.LENGTH_SHORT).show();
        else {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            String tglMasuk = dateformat.format(tglCheckIn);
            String tglKeluar = dateformat.format(tglCheckOut);


            int idCabang = radioGroup.getCheckedRadioButtonId();
            radioButton = (RadioButton) getView().findViewById(idCabang);
            cabang = radioButton.getText().toString();

            if (cabang == "")
                Toast.makeText(getContext(), "Tolong isikan cabang", Toast.LENGTH_SHORT).show();
            else {
                android.support.v4.app.Fragment tampilKamar = new TampilKamarTersedia();

                b.putString("tgl_masuk", tglMasuk);
                b.putString("tgl_keluar", tglKeluar);
                b.putString("cabang", cabang);

                tampilKamar.setArguments(b);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, tampilKamar);

                ft.commit();
            }
        }
    }
}
