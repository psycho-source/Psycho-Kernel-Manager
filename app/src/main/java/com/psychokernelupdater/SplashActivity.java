package com.psychokernelupdater;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity {

    static SplashActivity sp;
    ProgressBar bar;

    public static SplashActivity getInstance() {
        return sp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_spec);
        sp = SplashActivity.this;
        bar = (ProgressBar) findViewById(R.id.progressBar);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                presentActivity(bar);
            }
        }, 700);
    }

    public void presentActivity(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashActivity.this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra(new_main.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(new_main.EXTRA_CIRCULAR_REVEAL_Y, revealY);
        ActivityCompat.startActivity(SplashActivity.this, intent, options.toBundle());
    }
}
