package com.psychokernelupdater;

import android.app.Dialog;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.psychokernelupdater.utils.Config;
import com.psychokernelupdater.utils.DialogCallback;

import java.util.ArrayList;

public class PrefFrag extends PreferenceFragment implements DialogCallback {

    private final ArrayList<Dialog> dlgs = new ArrayList<>();

    private Config cfg;


    private CheckBoxPreference notifPref;
    private CheckBoxPreference wifidlPref;
    private CheckBoxPreference autodlPref;
    private Preference resetWarnPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cfg = Config.getInstance(getActivity().getApplicationContext());

        addPreferencesFromResource(R.xml.settings);

        notifPref = (CheckBoxPreference) findPreference("notif_pref");
        notifPref.setChecked(cfg.getShowNotif());

        wifidlPref = (CheckBoxPreference) findPreference("wifidl_pref");
        wifidlPref.setChecked(cfg.getWifiOnlyDl());

        autodlPref = (CheckBoxPreference) findPreference("autodl_pref");
        autodlPref.setChecked(cfg.getAutoDlState());

        resetWarnPref = findPreference("resetwarn_pref");
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == notifPref) {
            cfg.setShowNotif(notifPref.isChecked());
        } else if (preference == wifidlPref) {
            cfg.setWifiOnlyDl(wifidlPref.isChecked());
        } else if (preference == autodlPref) {
            cfg.setAutoDlState(autodlPref.isChecked());
        } else if (preference == resetWarnPref) {
            cfg.clearIgnored();
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        return true;
    }

    @Override
    public void onDialogShown(Dialog dlg) {
        dlgs.add(dlg);
    }

    @Override
    public void onDialogClosed(Dialog dlg) {
        dlgs.remove(dlg);
    }
}
