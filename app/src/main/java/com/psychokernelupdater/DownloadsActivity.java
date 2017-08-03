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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.psychokernelupdater.utils.BaseDownloadDialogActivity;
import com.psychokernelupdater.utils.BaseInfo;
import com.psychokernelupdater.utils.Config;
import com.psychokernelupdater.utils.KernelInfo;
import com.psychokernelupdater.utils.PropUtils;

import java.io.DataOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DownloadsActivity extends BaseDownloadDialogActivity {

    public static final String FLASH_KERNEL_ACTION = "com.jollakernelupdater.action.FLASH_KERNEL_ACTION";
    public static boolean is_called_by_DownloadList;
    public static String DownloadList_File_Name;
    private final ArrayList<Dialog> dlgs = new ArrayList<>();
    DrawerLayout drawerLayout;
    NavigationView mNavigationView;
    private AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String extState = Environment.getExternalStorageState();
        if (!extState.equals(Environment.MEDIA_MOUNTED) && !extState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Toast.makeText(this, extState.equals(Environment.MEDIA_SHARED) ? R.string.toast_nosd_shared : R.string.toast_nosd_error, Toast.LENGTH_LONG).show();
            finish();
        }

        setContentView(R.layout.downloads);

        Toolbar toolbar1 = (Toolbar) findViewById(R.id.toolbar1);

        MobileAds.initialize(this, "ca-app-pub-3026712685276849~6203773285");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        setSupportActionBar(toolbar1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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
                }
                return true;
            }
        });

        String action = getIntent().getAction();
        if (action != null && action.equals(FLASH_KERNEL_ACTION)) {
            showFlashDialog(KernelInfo.FACTORY.fromIntent(getIntent()));
        }
    }

    @Override
    protected void onPause() {
        for (Dialog dlg : dlgs) {
            if (dlg.isShowing()) dlg.dismiss();
        }
        dlgs.clear();
        super.onPause();

    }

    protected void showFlashDialog(final BaseInfo info) {
        if (PropUtils.getNoFlash()) { //can't flash programmatically, must flash manually
            showNoFlashDialog(info.getDownloadFileName());
        }

        String[] installOpts = getResources().getStringArray(R.array.install_options);
        final boolean[] selectedOpts = new boolean[installOpts.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_install_title);
        builder.setMultiChoiceItems(installOpts, selectedOpts, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                selectedOpts[which] = isChecked;
            }
        });
        builder.setPositiveButton(R.string.install, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(DownloadsActivity.this);
                builder.setTitle(R.string.alert_install_title);
                builder.setMessage(R.string.alert_install_message);
                builder.setPositiveButton(R.string.install, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (is_called_by_DownloadList) {
                            String file_path = PropUtils.getRecoverySdPath() + Config.KERNEL_SD_PATH + DownloadList_File_Name;
                            flashFiles(new String[]{file_path}, selectedOpts[0], selectedOpts[2], selectedOpts[1]);
                        } else {
                            flashFiles(new String[]{info.getRecoveryFilePath()}, selectedOpts[0], selectedOpts[2], selectedOpts[1]);
                        }
                        DownloadList_File_Name = null;
                        is_called_by_DownloadList = false;
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                builder.show();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
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

    private void showNoFlashDialog(String file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_install_title);
        builder.setMessage(getString(R.string.alert_noinstall_message, file));
        builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    private void flashFiles(String[] files, boolean backup, boolean wipeCache, boolean wipeData) {
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("mkdir -p /cache/recovery/\n");
            os.writeBytes("rm -f /cache/recovery/command\n");
            os.writeBytes("rm -f /cache/recovery/extendedcommand\n");
            os.writeBytes("echo 'boot-recovery' >> /cache/recovery/command\n");

            //no official cwm for sony, so use extendedcommand. sony devices cannot use regular command file
            if (Build.MANUFACTURER.toLowerCase(Locale.US).contains("sony")) {
                if (backup) {
                    os.writeBytes("echo 'backup_rom /sdcard/clockworkmod/backup/ota_" +
                            new SimpleDateFormat("yyyy-MM-dd_HH.mm", Locale.US).format(new Date()) +
                            "' >> /cache/recovery/extendedcommand\n");
                }
                if (wipeData) {
                    os.writeBytes("echo 'format(\"/data\");' >> /cache/recovery/extendedcommand\n");
                }
                if (wipeCache) {
                    os.writeBytes("echo 'format(\"/cache\");' >> /cache/recovery/extendedcommand\n");
                }

                for (String file : files) {
                    os.writeBytes("echo 'install_zip(\"" + file + "\");' >> /cache/recovery/extendedcommand\n");
                }
            } else {
                if (backup) {
                    os.writeBytes("echo '--nandroid' >> /cache/recovery/command\n");
                }
                if (wipeData) {
                    os.writeBytes("echo '--wipe_data' >> /cache/recovery/command\n");
                }
                if (wipeCache) {
                    os.writeBytes("echo '--wipe_cache' >> /cache/recovery/command\n");
                }

                for (String file : files) {
                    os.writeBytes("echo '--update_package=" + file + "' >> /cache/recovery/command\n");
                }
            }

            String rebootCmd = PropUtils.getRebootCmd();
            if (!rebootCmd.equals("$$NULL$$")) {
                os.writeBytes("sync\n");
                if (rebootCmd.endsWith(".sh")) {
                    os.writeBytes("sh " + rebootCmd + "\n");
                } else {
                    os.writeBytes(rebootCmd + "\n");
                }
            }

            os.writeBytes("sync\n");
            os.writeBytes("exit\n");
            os.flush();
            p.waitFor();
            ((PowerManager) getSystemService(POWER_SERVICE)).reboot("recovery");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
