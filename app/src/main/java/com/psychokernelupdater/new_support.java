package com.psychokernelupdater;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class new_support extends AppCompatActivity {

    TextView num;
    private DrawerLayout mDrawerLayout;
    private Intent in;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_support);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3026712685276849~7595670218");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_about);
        toolbar.setTitle(R.string.donate);
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
                        in = new Intent(new_support.this, new_main.class);
                        break;
                    case R.id.downloads:
                        in = new Intent(new_support.this, download_new.class);
                        break;
                    case R.id.about:
                        in = new Intent(new_support.this, new_about.class);
                        break;
                    case R.id.donate:
                        in = null;
                        break;
                    case R.id.spec:
                        in = new Intent(new_support.this, SplashActivity.class);
                        break;
                    case R.id.settings:
                        in = new Intent(new_support.this, Settings_new.class);
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
        final CardView paytm = (CardView) findViewById(R.id.paytm_info);
        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(new_support.this);
                final View diagContent = LayoutInflater.from(builder.getContext()).inflate(R.layout.paytm, null);
                num = (TextView) diagContent.findViewById(R.id.number);
                num.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        ClipboardManager cManager = (ClipboardManager) new_support.this.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData cData = ClipData.newPlainText("number", num.getText());
                        cManager.setPrimaryClip(cData);
                        Toast.makeText(new_support.this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
                builder.setView(diagContent).show();
            }
        });
        CardView paypal = (CardView) findViewById(R.id.paypal_info);
        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/psychomod")));
            }
        });
        mInterstitialAd = new InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-3026712685276849/9845185760");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        CardView goog = (CardView) findViewById(R.id.freeVid);
        goog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        });
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
        startActivity(new Intent(new_support.this, new_main.class));
        finish();
    }
}
