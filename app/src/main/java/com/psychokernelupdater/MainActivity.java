package com.psychokernelupdater;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    RelativeLayout rootLayoutSpec;
    private CardView oldCard;
    private List<String> suResult = null;
    private int notaneasteregg = 0;
    private DrawerLayout mDrawerLayout;
    private Intent in;
    private int revealX, revealY;
    private AdView mAdView;

    // Method that interprets a profile and sets it
    public static void setProfile(int profile) {
        int numProfiles = 3;
        if (profile > numProfiles || profile < 0) {
            setProp(0);
        } else {
            setProp(profile);
        }

    }

    // Method that sets system property
    private static void setProp(final int profile) {
        new AsyncTask<Object, Object, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                Shell.SU.run("setprop persist.spectrum.profile " + profile);
                return null;
            }
        }.execute();
    }

    // Method that converts List<String> to String
    public static String listToString(List<String> list) {
        StringBuilder Builder = new StringBuilder();
        for (String out : list) {
            Builder.append(out);
        }
        return Builder.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.NewAppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spectrum_new);
        rootLayoutSpec = (RelativeLayout) findViewById(R.id.spec_main);
        final Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) && intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
            rootLayoutSpec.setVisibility(View.INVISIBLE);
            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);
            ViewTreeObserver viewTreeObserver = rootLayoutSpec.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        rootLayoutSpec.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_spectrum);
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
                        in = new Intent(MainActivity.this, new_main.class);
                        break;
                    case R.id.downloads:
                        in = new Intent(MainActivity.this, download_new.class);
                        break;
                    case R.id.about:
                        in = new Intent(MainActivity.this, new_about.class);
                        break;
                    case R.id.donate:
                        in = new Intent(MainActivity.this, new_support.class);
                        break;
                    case R.id.spec:
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

        // Define existing CardViews
        final CardView card0 = (CardView) findViewById(R.id.card0);
        final CardView card1 = (CardView) findViewById(R.id.card1);
        final CardView card2 = (CardView) findViewById(R.id.card2);
        final CardView card3 = (CardView) findViewById(R.id.card3);
        final int balColor = ContextCompat.getColor(this, R.color.colorBalance);
        final int perColor = ContextCompat.getColor(this, R.color.colorPerformance);
        final int batColor = ContextCompat.getColor(this, R.color.colorBattery);
        final int gamColor = ContextCompat.getColor(this, R.color.colorGaming);

        // Ensure root access
        if (!checkSU())
            return;

        // Check for Spectrum Support
        if (!checkSupport())
            return;

        // Get profile descriptions
        getDesc();

        // Highlight current profile
        initSelected();

        // Set system property on click
        card0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardClick(card0, 0, balColor);
                if (notaneasteregg == 1) {
                    notaneasteregg++;
                } else {
                    notaneasteregg = 0;
                }
            }
        });

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardClick(card1, 1, perColor);
                if (notaneasteregg == 3) {
                    Intent intent = new Intent(MainActivity.this, ProfileLoaderActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    notaneasteregg = 0;
                }
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardClick(card2, 2, batColor);
                if (notaneasteregg == 2) {
                    notaneasteregg++;
                } else {
                    notaneasteregg = 0;
                }
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardClick(card3, 3, gamColor);
                notaneasteregg = 1;
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    // Method that detects the selected profile on launch
    private void initSelected() {
        SharedPreferences profile = this.getSharedPreferences("profile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = profile.edit();

        suResult = Shell.SU.run("getprop persist.spectrum.profile");

        if (suResult != null) {
            String result = listToString(suResult);

            if (result.contains("0")) {
                CardView card0 = (CardView) findViewById(R.id.card0);
                int balColor = ContextCompat.getColor(this, R.color.colorBalance);
                card0.setCardBackgroundColor(balColor);
                oldCard = card0;
                editor.putString("profile", "0");
                editor.apply();
            } else if (result.contains("1")) {
                CardView card1 = (CardView) findViewById(R.id.card1);
                int perColor = ContextCompat.getColor(this, R.color.colorPerformance);
                card1.setCardBackgroundColor(perColor);
                oldCard = card1;
                editor.putString("profile", "1");
                editor.apply();
            } else if (result.contains("2")) {
                CardView card2 = (CardView) findViewById(R.id.card2);
                int batColor = ContextCompat.getColor(this, R.color.colorBattery);
                card2.setCardBackgroundColor(batColor);
                oldCard = card2;
                editor.putString("profile", "2");
                editor.apply();
            } else if (result.contains("3")) {
                CardView card3 = (CardView) findViewById(R.id.card3);
                int gamColor = ContextCompat.getColor(this, R.color.colorGaming);
                card3.setCardBackgroundColor(gamColor);
                oldCard = card3;
                editor.putString("profile", "3");
                editor.apply();
            } else {
                editor.putString("profile", "custom");
                editor.apply();
            }
        }
    }

    // Method to check if the device is rooted
    private boolean checkSU() {
        if (!Shell.SU.available()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material);
            dialog.setTitle("Root access not available");
            dialog.setMessage("Spectrum cannot function without Superuser access");
            dialog.setCancelable(false);
            AlertDialog root = dialog.create();
            root.show();
            return false;
        } else
            return true;
    }

    // Method that reads and sets profile descriptions
    private void getDesc() {
        TextView desc0 = (TextView) findViewById(R.id.desc0);
        String balDesc;
        String kernel;

        suResult = Shell.SU.run("getprop persist.spectrum.kernel");
        kernel = listToString(suResult);
        if (kernel.isEmpty())
            return;
        balDesc = desc0.getText().toString();
        balDesc = balDesc.replaceAll("\\bElectron\\b", kernel);
        desc0.setText(balDesc);
    }

    // Method to check if kernel supports
    private boolean checkSupport() {
        suResult = Shell.SU.run("getprop spectrum.support");
        String support = listToString(suResult);

        if (!support.isEmpty())
            return true;
        else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material);
            dialog.setTitle("Spectrum not supported!");
            dialog.setMessage("Please contact your kernel dev and ask them to add Spectrum support.");
            dialog.setCancelable(false);
            AlertDialog supportDialog = dialog.create();
            supportDialog.show();
            suResult = Shell.SU.run("getprop persist.spectrum.profile");
            String defProfile = listToString(suResult);
            if (!defProfile.isEmpty() && !defProfile.contains("0"))
                setProfile(0);
            return false;
        }
    }

    // Method that completes card onClick tasks
    private void cardClick(CardView card, int prof, int color) {
        if (oldCard != card) {
            ColorStateList ogColor = card.getCardBackgroundColor();
            card.setCardBackgroundColor(color);
            if (oldCard != null)
                oldCard.setCardBackgroundColor(ogColor);
            setProfile(prof);
            oldCard = card;
            SharedPreferences profile = this.getSharedPreferences("profile", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = profile.edit();
            editor.putString("profile", String.valueOf(prof));
            editor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPreferences first = this.getSharedPreferences("firstFind", Context.MODE_PRIVATE);
        if (!first.getBoolean("firstFind", true)) {
            getMenuInflater().inflate(R.menu.nav, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.custom_profile:
                Intent i = new Intent(this, ProfileLoaderActivity.class);
                startActivity(i);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MainActivity.this, new_main.class));
        finish();
    }

    protected void revealActivity(int x, int y) {
        float finalRadius = (float) (Math.max(rootLayoutSpec.getWidth(), rootLayoutSpec.getHeight()) * 1.1);
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayoutSpec, x, y, 0, finalRadius);
        circularReveal.setDuration(800);
        circularReveal.setInterpolator(new AccelerateInterpolator());
        rootLayoutSpec.setVisibility(View.VISIBLE);
        circularReveal.start();
        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                SplashActivity.getInstance().finish();
            }
        });
    }
}

