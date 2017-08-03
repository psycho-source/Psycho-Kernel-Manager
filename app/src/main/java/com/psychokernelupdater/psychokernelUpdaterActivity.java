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

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.psychokernelupdater.utils.BaseDownloadDialogActivity;
import com.psychokernelupdater.utils.Config;
import com.psychokernelupdater.utils.KernelInfo;
import com.psychokernelupdater.utils.PropUtils;
import com.psychokernelupdater.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class psychokernelUpdaterActivity extends BaseDownloadDialogActivity {
    public static final String KERNEL_NOTIF_ACTION = "com.psychokernelupdater.action.KERNEL_NOTIF_ACTION";
    public static final String EXTRA_FLAG_DOWNLOAD_DIALOG = "SHOW_DOWNLOAD_DIALOG";
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    NavigationView mNavigationView;
    Context context;
    TabLayout tabLayout;
    private AdView mAdView;
    private Config cfg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Runtime.getRuntime().exec("su");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.need_root_permission, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        try {
            Process process = Runtime.getRuntime().exec("su - -c whoami");
            InputStreamReader input_stream_reader = new InputStreamReader(process.getInputStream());
            String s_whoami = new BufferedReader(input_stream_reader).readLine();

            if (s_whoami == null) {
                Toast.makeText(getApplicationContext(), R.string.su_exec_error, Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            if (!s_whoami.equals("root")) {
                Toast.makeText(getApplicationContext(), R.string.root_check_failed, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.unsupported_su, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), R.string.need_storage_permission, Toast.LENGTH_LONG).show();
        }

        context = getApplicationContext();
        startMainActivity(context);
    }

    private void startMainActivity(Context context) {
        cfg = Config.getInstance(context);

        boolean data = Utils.dataAvailable(this);
        boolean wifi = Utils.wifiConnected(this);

        if (!data || !wifi) {
            final boolean nodata = !data && !wifi;

            if ((nodata && !cfg.getIgnoredDataWarn()) || (!nodata && !cfg.getIgnoredWifiWarn())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(nodata ? R.string.alert_nodata_title : R.string.alert_nowifi_title);
                builder.setMessage(nodata ? R.string.alert_nodata_message : R.string.alert_nowifi_message);
                builder.setCancelable(false);
                builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                builder.setNeutralButton(R.string.alert_wifi_settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(i);
                    }
                });
                builder.setPositiveButton(R.string.ignore, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (nodata) {
                            cfg.setIgnoredDataWarn(true);
                        } else {
                            cfg.setIgnoredWifiWarn(true);
                        }
                        dialog.dismiss();
                    }
                });

                final AlertDialog dlg = builder.create();

                dlg.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        onDialogShown(dlg);
                    }
                });
                dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        onDialogClosed(dlg);
                    }
                });
                dlg.show();
            }
        }

        CheckinReceiver.setDailyAlarm(this);

        if (!PropUtils.isKernelOtaEnabled() && !cfg.getIgnoredUnsupportedWarn()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.alert_unsupported_title);
            builder.setMessage(R.string.alert_unsupported_message);
            builder.setCancelable(false);
            builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.setPositiveButton(R.string.ignore, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cfg.setIgnoredUnsupportedWarn(true);
                    dialog.dismiss();
                }
            });

            final AlertDialog dlg = builder.create();

            dlg.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    onDialogShown(dlg);
                }
            });
            dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    onDialogClosed(dlg);
                }
            });
            dlg.show();
        }

        setContentView(R.layout.main);
        Toolbar toolbar3 = (Toolbar) findViewById(R.id.toolbar3);
        MobileAds.initialize(this, "ca-app-pub-3026712685276849~6203773285");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar3, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
                syncState();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //Set the custom toolbar
        if (toolbar3 != null) {
            setSupportActionBar(toolbar3);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        actionBarDrawerToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();
                menuItem.setChecked(true);
                Intent i;
                switch (menuItem.getItemId()) {
                    case R.id.settings:
                        i = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(i);
                        break;
                    case R.id.downloads:
                        i = new Intent(getApplicationContext(), DownloadsActivity.class);
                        startActivity(i);
                        break;
                    case R.id.home:
                        i = new Intent(getApplicationContext(), psychokernelUpdaterActivity.class);
                        startActivity(i);
                        break;
                    case R.id.donate:
                        i = new Intent(getApplicationContext(), Donate.class);
                        startActivity(i);
                        break;
                    case R.id.about:
                        i = new Intent(getApplicationContext(), About.class);
                        startActivity(i);
                        break;
                    case R.id.spec:
                        i = new Intent(getApplicationContext(), SplashActivity.class);
                        startActivity(i);
                        break;
                }
                return true;
            }
        });
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        if (cfg.hasStoredKernelUpdate()) {
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            if (tab != null) {
                tab.setIcon(R.drawable.ic_action_warning);
            }
        }

        if (!handleNotifAction(getIntent())) {
            if (cfg.hasStoredKernelUpdate() && !cfg.isDownloadingKernel()) {
                cfg.getStoredKernelUpdate().showUpdateNotif(this);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNotifAction(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void updateKernelTabIcon(boolean update) {
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        if (tab != null) {
            if (update) {
                tab.setIcon(R.drawable.ic_action_warning);
            } else {
                tab.setIcon(null);
            }
        }
    }

    private boolean handleNotifAction(Intent intent) {
        String action = intent.getAction();
        if (KERNEL_NOTIF_ACTION.equals(action)) {
            KernelInfo.FACTORY.clearUpdateNotif(this);

            if (intent.getBooleanExtra(EXTRA_FLAG_DOWNLOAD_DIALOG, false)) {
                DownloadBarFragment.showDownloadingDialog(this, cfg.getKernelDownloadID(), this);
            } else {
                KernelInfo info = KernelInfo.FACTORY.fromIntent(intent);
                if (info == null) info = cfg.getStoredKernelUpdate();
                if (info != null) info.showUpdateDialog(this, this);
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    return new AboutTab();
                case 1:
                    return new KernelTab();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.main_about);
                case 1:
                    return getResources().getString(R.string.main_kernel);
            }
            return null;
        }
    }
}
