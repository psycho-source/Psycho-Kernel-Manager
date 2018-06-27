package com.psychokernelupdater;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.makeText;

public class Splash extends AppCompatActivity {

    static Splash spl;
    ImageView logo;
    TextView brand;
    CardView perm_card;
    RelativeLayout store, su;
    Animation slide_up;
    int flag_perm = 0;
    private SharedPreferences isFirst;
    private boolean first;

    public static Splash getInstance() {
        return spl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        spl = Splash.this;
        logo = (ImageView) findViewById(R.id.logo);
        brand = (TextView) findViewById(R.id.brand);
        perm_card = (CardView) findViewById(R.id.perm_card);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        isFirst = PreferenceManager.getDefaultSharedPreferences(this);
        first = isFirst.getBoolean("FirstLaunch", true);
        if (!first) {
            if (isRootGranted()) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final ObjectAnimator logoAnim = ObjectAnimator.ofFloat(logo, "TranslationY", -500f);
                            logoAnim.setDuration(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    logoAnim.start();
                                }
                            });
                            logoAnim.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    final ObjectAnimator brandAnim = ObjectAnimator.ofFloat(brand, "TranslationY", -200f);
                                    brandAnim.setDuration(1000);
                                    brand.setVisibility(View.VISIBLE);
                                    brand.setAlpha(0);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            brandAnim.start();
                                            brand.animate().alpha(1).setDuration(1000).start();
                                        }
                                    });
                                    brandAnim.addListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            presentActivity(brand);
                                                        }
                                                    });
                                                }
                                            }, 250);
                                        }
                                    });
                                }
                            });
                        }
                    }).start();
                } else {
                    makeText(getApplicationContext(), "Please grant Storage Permissions :( ", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                finish();
            }
        } else {
            //Show Intro
            ObjectAnimator logoAnim = ObjectAnimator.ofFloat(logo, "TranslationY", -500f);
            logoAnim.setDuration(1000).start();
            logoAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ObjectAnimator brandAnim = ObjectAnimator.ofFloat(brand, "TranslationY", -200f);
                    brandAnim.setDuration(1000).start();
                    brand.setVisibility(View.VISIBLE);
                    brand.setAlpha(0);
                    brand.animate().alpha(1).setDuration(1000).start();
                    brandAnim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            perm_card.setVisibility(View.VISIBLE);
                            perm_card.setAlpha(0);
                            perm_card.animate().alpha(1).setDuration(1000).start();
                            perm_card.startAnimation(slide_up);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Tap on permissions and allow them to continue", Toast.LENGTH_SHORT).show();
                                }
                            }, 1000);
                        }
                    });
                }
            });
            store = (RelativeLayout) findViewById(R.id.stor_perm);
            su = (RelativeLayout) findViewById(R.id.su_perm);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    store.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                store.setClickable(false);
                                store.setAlpha(0.3f);
                                flag_perm += 22;
                                if (flag_perm == 64) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    presentActivity(brand);
                                                }
                                            }, 250);
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    su.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (isRootGranted()) {
                                su.setClickable(false);
                                su.setAlpha(0.3f);
                                flag_perm += 42;
                                if (flag_perm == 64) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            presentActivity(brand);
                                        }
                                    }, 250);
                                }
                            }
                        }
                    });
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences.Editor editor = isFirst.edit();
                    editor.putBoolean("FirstLaunch", !first);
                    editor.apply();
                }
            }).start();
        }
    }

    public boolean isRootGranted() {
        try {
            Runtime.getRuntime().exec("su");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.need_root_permission, Toast.LENGTH_LONG).show();
            finish();
            return false;
        }
        return true;
    }

    public void presentActivity(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(Splash.this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);
        Intent intent = new Intent(Splash.this, new_main.class);
        intent.putExtra(new_main.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(new_main.EXTRA_CIRCULAR_REVEAL_Y, revealY);
        ActivityCompat.startActivity(Splash.this, intent, options.toBundle());
    }

}
