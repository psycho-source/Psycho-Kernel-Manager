package com.psychokernelupdater;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.psychokernelupdater.utils.APIUtils;
import com.psychokernelupdater.utils.BaseDownloadDialogActivity;
import com.psychokernelupdater.utils.BaseInfo;
import com.psychokernelupdater.utils.Config;
import com.psychokernelupdater.utils.DownloadDialogCallback;
import com.psychokernelupdater.utils.KernelInfo;
import com.psychokernelupdater.utils.PropUtils;
import com.psychokernelupdater.utils.Utils;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class new_main extends BaseDownloadDialogActivity {

    public static final String KERNEL_NOTIF_ACTION = "com.psychokernelupdater.action.KERNEL_NOTIF_ACTION";
    public static final String EXTRA_FLAG_DOWNLOAD_DIALOG = "SHOW_DOWNLOAD_DIALOG";
    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    TextView updKern;
    ActionBarDrawerToggle actionBarDrawerToggle;
    RelativeLayout rootLayout;
    private Config cfg;
    private boolean fetching = false;
    private boolean is_init = true;
    private int AVAIL_UPDATES_IDX = -1;
    private DrawerLayout mDrawerLayout;
    private Intent in;
    private int revealX, revealY;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.NewAppTheme);
        super.onCreate(savedInstanceState);

        cfg = Config.getInstance(getApplicationContext());

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

        CheckinReceiver.setDailyAlarm(new_main.this);

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

        setContentView(R.layout.new_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Intent intent = getIntent();
                if (intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) && intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
                    rootLayout = (RelativeLayout) findViewById(R.id.root_main);
                    rootLayout.setVisibility(View.INVISIBLE);
                    revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
                    revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);
                    ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
                    if (viewTreeObserver.isAlive()) {
                        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                revealActivity(revealX, revealY);
                                rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        });
                    }
                } else {
                    rootLayout = (RelativeLayout) findViewById(R.id.root_main);
                    rootLayout.setVisibility(View.VISIBLE);
                }
            }
        }).start();
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3026712685276849~7595670218");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new);
        setSupportActionBar(toolbar);
        final ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
        }
        mDrawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {
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
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        NavigationView mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        in = null;
                        break;
                    case R.id.downloads:
                        in = new Intent(new_main.this, download_new.class);
                        break;
                    case R.id.about:
                        in = new Intent(new_main.this, new_about.class);
                        break;
                    case R.id.donate:
                        in = new Intent(new_main.this, new_support.class);
                        break;
                    case R.id.spec:
                        in = new Intent(new_main.this, SplashActivity.class);
                        break;
                    case R.id.settings:
                        in = new Intent(new_main.this, Settings_new.class);
                        break;
                }
                mDrawerLayout.closeDrawers();
                if (in != null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(in);
                            finish();
                        }
                    }, 250);
                }
                return true;
            }
        });
        CardView updInfo = (CardView) findViewById(R.id.update_info);
        TextView device = (TextView) findViewById(R.id.kern_dev_info);
        TextView kernVer = (TextView) findViewById(R.id.kern_kern_info);
        TextView psyKern = (TextView) findViewById(R.id.psy_kern_info);
        TextView psyKernHead = (TextView) findViewById(R.id.psy_kern_head);
        updKern = (TextView) findViewById(R.id.kern_upd_info);
        updateStatus();
        updInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cfg.hasStoredKernelUpdate()) {
                    KernelInfo info = cfg.getStoredKernelUpdate();
                    if (info.isUpdate()) {
                        info.showUpdateDialog(new_main.this, (DownloadDialogCallback) new_main.this);
                    } else {
                        cfg.clearStoredKernelUpdate();
                        updKern.setText(R.string.updates_none);

                        if (!fetching) {
                            checkForKernelUpdates();
                        }
                    }
                } else if (!fetching) {
                    checkForKernelUpdates();
                }
            }
        });
        device.setText(android.os.Build.DEVICE.toLowerCase(Locale.US));
        kernVer.setText(PropUtils.getKernelVersion());
        if (PropUtils.isKernelOtaEnabled()) {
            String kernelVersion = PropUtils.getKernelOtaVersion();
            if (kernelVersion == null) kernelVersion = getString(R.string.kernel_version_unknown);
            Date kernelDate = PropUtils.getKernelOtaDate();
            if (kernelDate != null) {
                kernelVersion += " (" + DateFormat.getDateTimeInstance().format(kernelDate) + ")";
            }
            psyKern.setText(kernelVersion);
            checkForKernelUpdates();
            AVAIL_UPDATES_IDX = 10;
        } else {
            if (cfg.hasStoredKernelUpdate())
                cfg.clearStoredKernelUpdate();
            psyKernHead.setText(R.string.kernel_unsupported);
            psyKern.setText(R.string.kernel_unsupported_summary);
            updInfo.setVisibility(View.GONE);
        }

        if (!handleNotifAction(getIntent())) {
            if (cfg.hasStoredKernelUpdate() && !cfg.isDownloadingKernel()) {
                cfg.getStoredKernelUpdate().showUpdateNotif(this);
            }
        }

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AppUpdater appUpdater = new AppUpdater(new_main.this);
        appUpdater.setDisplay(Display.DIALOG);
        appUpdater.setCancelable(true);
        appUpdater.setButtonDoNotShowAgain("");
        appUpdater.setButtonDoNotShowAgainClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Nothing as it don't even exist. :p
            }
        });
        appUpdater.setTitleOnUpdateAvailable("Kernel Manager Update Available!");
        appUpdater.setUpdateFrom(UpdateFrom.XML);
        appUpdater.setUpdateXML("https:raw.githubusercontent.com/psycho-source/x3/master/update.xml");
        appUpdater.start();
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
    public void onResume() {
        super.onResume();
        updateStatus();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        cfg.clearStoredKernelUpdate();
        super.onDestroy();
    }

    public void updateStatus() {
        updateStatus(cfg.getStoredKernelUpdate());
    }

    public void updateStatus(KernelInfo info) {
        if (AVAIL_UPDATES_IDX == -1)
            return;
        else {
            if (info != null && info.isUpdate()) {
                updKern.setText(R.string.updates_new + info.name + info.version);
            } else {
                updKern.setText(R.string.updates_none);
                Toast.makeText(new_main.this, R.string.kernel_toast_no_update, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkForKernelUpdates() {
        if (fetching) return;
        if (!PropUtils.isKernelOtaEnabled()) return;

        APIUtils.fetchKernelInfo(new_main.this, new BaseInfo.InfoLoadAdapter<KernelInfo>(KernelInfo.class, new_main.this) {

            @Override
            public void onStart(APIUtils.APITask task) {
                fetching = true;
                updKern.setText(R.string.updates_checking);
            }

            @Override
            public void onInfoLoaded(KernelInfo info) {
                if (is_init) {
                    is_init = false;
                } else {
                    updateStatus(info);
                }
                if (info.isUpdate())
                    info.showUpdateDialog(new_main.this, (DownloadDialogCallback) new_main.this);
            }

            @Override
            public void onError(String message, JSONObject respObj) {
                updKern.setText(getString(R.string.update_fetch_error, message));
                Toast.makeText(new_main.this, message == null || message.isEmpty() ? getString(R.string.toast_update_fetch_error) : message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(boolean success) {
                fetching = false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void revealActivity(int x, int y) {
        float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
        circularReveal.setDuration(800);
        circularReveal.setInterpolator(new AccelerateInterpolator());
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Splash.getInstance().finish();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNotifAction(intent);
    }
}
