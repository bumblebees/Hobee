package bumblebees.hobee.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import bumblebees.hobee.R;


public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }




}
