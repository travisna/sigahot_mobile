package com.example.android.sigahot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by trav_na on 3/29/18.
 */

public class InformasiHotel extends Fragment {

    TextView txtInformasi;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.informasi_hotel, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Informasi Hotel");
        txtInformasi = (TextView) getView().findViewById(R.id.txtInformasiHote);

        txtInformasi.setText(R.string.informasi_hotel);
        txtInformasi.setMovementMethod(new ScrollingMovementMethod());

    }
}
