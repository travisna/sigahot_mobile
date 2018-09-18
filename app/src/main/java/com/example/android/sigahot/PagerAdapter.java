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
        DetilKamar detilKamar = new DetilKamar();
        Bundle bundle = new Bundle();
        switch (position){
            case 0:
                bundle.putString("jenis_kamar","Superior");
                detilKamar.setArguments(bundle);
                return detilKamar;
            case 1:
                bundle.putString("jenis_kamar","Double Deluxe");
                detilKamar.setArguments(bundle);
                return detilKamar;
            case 2:
                bundle.putString("jenis_kamar","Executive Deluxe");
                detilKamar.setArguments(bundle);
                return detilKamar;
            case 3:
                bundle.putString("jenis_kamar","Junior Suite");
                detilKamar.setArguments(bundle);
                return  detilKamar;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
