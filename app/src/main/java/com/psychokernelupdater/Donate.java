package com.psychokernelupdater;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.psychokernelupdater.utils.Config;

public class Donate extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView mNavigationView;
    TabLayout tabLayout;
    private AdView mAdView;
    private int RC_LOGIN = 100;
    private Config cfg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        cfg = Config.getInstance(getApplicationContext());
        if (cfg.getThemeSt())
            setTheme(R.style.AppThemeDark);

        else
            setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donate);
        Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar2);

        MobileAds.initialize(this, "ca-app-pub-3026712685276849~6203773285");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        setSupportActionBar(toolbar2);
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

        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        Donate.SectionsPagerAdapter mSectionsPagerAdapter = new Donate.SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Donate.this, DialogActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Donate.this, fab, getString(R.string.transition_dialog));
                startActivityForResult(intent, RC_LOGIN, options.toBundle());
            }
        });

    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    return new PaypalTab();
                case 1:
                    return new PaytmTab();
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
                    return getResources().getString(R.string.donate_paypal);
                case 1:
                    return getResources().getString(R.string.donate_paytm);
            }
            return null;
        }
    }

}
