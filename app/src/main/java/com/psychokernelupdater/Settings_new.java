package com.psychokernelupdater;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Settings_new extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Intent in;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.perf_theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_settings);
        toolbar.setTitle(R.string.settings_title);
        setSupportActionBar(toolbar);
        final ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowHomeEnabled(true);
        }
        mDrawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        in = new Intent(Settings_new.this, new_main.class);
                        break;
                    case R.id.downloads:
                        in = new Intent(Settings_new.this, download_new.class);
                        break;
                    case R.id.about:
                        in = new Intent(Settings_new.this, new_about.class);
                        break;
                    case R.id.donate:
                        in = new Intent(Settings_new.this, new_support.class);
                        break;
                    case R.id.spec:
                        in = new Intent(Settings_new.this, SplashActivity.class);
                        break;
                    case R.id.settings:
                        in = null;
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

        getFragmentManager().beginTransaction()
                .replace(R.id.pref_lay, new PrefFrag())
                .commitAllowingStateLoss();

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Settings_new.this, new_main.class));
        finish();
    }
}
