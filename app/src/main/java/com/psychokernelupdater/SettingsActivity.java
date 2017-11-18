/*
 * Copyright (C) 2014 OTA Update Center
 * Copyright (C) 2017 jollaman999
 * Copyright (C) 2017 Psycho-Mods
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.psychokernelupdater;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.psychokernelupdater.utils.Config;
import com.psychokernelupdater.utils.DialogCallback;

import java.util.ArrayList;

public class SettingsActivity extends PreferenceActivity implements DialogCallback {

    private final ArrayList<Dialog> dlgs = new ArrayList<>();

    private Config cfg;

    private CheckBoxPreference notifPref;
    private CheckBoxPreference wifidlPref;
    private CheckBoxPreference autodlPref;
    private Preference resetWarnPref;
    private SwitchPreference themeSwitch;

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {


        cfg = Config.getInstance(getApplicationContext());

        if (cfg.getThemeSt())
            setTheme(R.style.SettingsThemeDark);

        else
            setTheme(R.style.SettingsTheme);

        super.onCreate(savedInstanceState);

        final ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        addPreferencesFromResource(R.xml.settings);

        notifPref = (CheckBoxPreference) findPreference("notif_pref");
        notifPref.setChecked(cfg.getShowNotif());

        wifidlPref = (CheckBoxPreference) findPreference("wifidl_pref");
        wifidlPref.setChecked(cfg.getWifiOnlyDl());

        autodlPref = (CheckBoxPreference) findPreference("autodl_pref");
        autodlPref.setChecked(cfg.getAutoDlState());

        resetWarnPref = findPreference("resetwarn_pref");

        themeSwitch = (SwitchPreference) findPreference("theme_switch");
        themeSwitch.setChecked(cfg.getThemeSt());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return false;
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
        } else if (preference == themeSwitch) {
            cfg.setThemeSt(themeSwitch.isChecked());
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (Dialog dlg : dlgs) {
            if (dlg.isShowing()) dlg.dismiss();
        }
        dlgs.clear();
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
