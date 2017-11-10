package com.psychokernelupdater;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.psychokernelupdater.utils.Config;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity {

    private CardView oldCard;
    private List<String> suResult = null;
    private int notaneasteregg = 0;
    DrawerLayout drawerLayout;
    NavigationView mNavigationView;
    private Config cfg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cfg = Config.getInstance(getApplicationContext());
        if (cfg.getThemeSt())
            setTheme(R.style.AppThemeDark);

        else
            setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        Toolbar toolbar7 = (Toolbar) findViewById(R.id.toolbar7);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        setSupportActionBar(toolbar7);
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

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

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

    // Method that converts List<String> to String
    public static String listToString(List<String> list) {
        StringBuilder Builder = new StringBuilder();
        for(String out : list){
            Builder.append(out);
        }
        return Builder.toString();
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
        switch(item.getItemId()){
            case R.id.custom_profile:
                Intent i = new Intent(this, ProfileLoaderActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

