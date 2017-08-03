package com.psychokernelupdater;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class About extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView mNavigationView;
    TabLayout tabLayout;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar6);
        MobileAds.initialize(this, "ca-app-pub-3026712685276849~6203773285");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        setSupportActionBar(toolbar);
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
                    case R.id.spec:
                        i = new Intent(getApplicationContext(), SplashActivity.class);
                        startActivity(i);
                        break;
                }
                return true;
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
