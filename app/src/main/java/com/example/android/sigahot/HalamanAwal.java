package com.example.android.sigahot;

import android.os.Bundle;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by TRAVIS'S COMP on 26/03/2018.
 */

public class HalamanAwal extends Fragment {

    Button btnProfil,btnInformasiHotel,btnInformasiKamar,btnCariKamar;
    public HalamanAwal(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.halaman_awal, container, false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(" ");

        btnCariKamar = (Button) getView().findViewById(R.id.buttonCariKamar);
        btnInformasiKamar = (Button) getView().findViewById(R.id.buttonInformasiKamar);
        btnInformasiHotel = (Button) getView().findViewById(R.id.buttonInformasiHotel);
        btnProfil = (Button) getView().findViewById(R.id.buttonProfil);


        btnInformasiHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragmentInfoHotel = new InformasiHotel();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container,fragmentInfoHotel);

                ft.commit();
            }
        });


        btnProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragmentProfil = new DetilProfil();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container,fragmentProfil);

                ft.commit();
            }
        });

        btnInformasiKamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment informasiKamar = new InformasiKamar();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container,informasiKamar);

                ft.commit();
            }
        });

        btnCariKamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment cariKamar = new CariKamar();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container,cariKamar);

                ft.commit();
            }
        });
    }
}
