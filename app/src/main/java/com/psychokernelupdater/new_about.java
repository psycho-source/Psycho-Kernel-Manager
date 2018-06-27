package com.psychokernelupdater;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.psychokernelupdater.utils.Config;

public class new_about extends AppCompatActivity {

    private static String github = "https://www.github.com/";
    private DrawerLayout mDrawerLayout;
    private Intent in;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_about);
        toolbar.setTitle(R.string.about_he);
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
                        in = new Intent(new_about.this, new_main.class);
                        break;
                    case R.id.downloads:
                        in = new Intent(new_about.this, download_new.class);
                        break;
                    case R.id.about:
                        in = null;
                        break;
                    case R.id.donate:
                        in = new Intent(new_about.this, new_support.class);
                        break;
                    case R.id.spec:
                        in = new Intent(new_about.this, SplashActivity.class);
                        break;
                    case R.id.settings:
                        in = new Intent(new_about.this, Settings_new.class);
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
        CardView manInfo = (CardView) findViewById(R.id.man_info);
        manInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.SITE_GITHUB_URL)));
            }
        });
        RelativeLayout ota = (RelativeLayout) findViewById(R.id.otaUpdCen);
        ota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(github + "OTAUpdateCenter")));
            }
        });
        RelativeLayout spec = (RelativeLayout) findViewById(R.id.specKern);
        spec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(github + "frap129/spectrum")));
            }
        });
        RelativeLayout jolla = (RelativeLayout) findViewById(R.id.jolla);
        jolla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(github + "jollaman999")));
            }
        });
        RelativeLayout appupd = (RelativeLayout) findViewById(R.id.appUpd);
        appupd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(github + "javiersantos/AppUpdater")));
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.contrib:
                startActivity(new Intent(new_about.this, ContributorsActivity.class));
                return true;
            case R.id.license:
                startActivity(new Intent(new_about.this, LicenseActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(new_about.this, new_main.class));
        finish();
    }
}
