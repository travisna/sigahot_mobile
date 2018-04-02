package com.example.android.sigahot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by trav_na on 3/29/18.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm,int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        InformasiKamar informasiKamar = new InformasiKamar();
        Bundle bundle = new Bundle();
        switch (position){
            case 0:
                bundle.putString("jenis_kamar","Superior");
                informasiKamar.setArguments(bundle);
                return informasiKamar;
            case 1:
                bundle.putString("jenis_kamar","Double Deluxe");
                informasiKamar.setArguments(bundle);
                return informasiKamar;
            case 2:
                bundle.putString("jenis_kamar","Executive Deluxe");
                informasiKamar.setArguments(bundle);
                return informasiKamar;
            case 3:
                bundle.putString("jenis_kamar","Junior Suite");
                informasiKamar.setArguments(bundle);
                return  informasiKamar;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
