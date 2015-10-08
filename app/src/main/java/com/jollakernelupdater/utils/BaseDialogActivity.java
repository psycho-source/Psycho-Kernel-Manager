package com.jollakernelupdater.utils;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public abstract class BaseDialogActivity extends AppCompatActivity implements DialogCallback {
    private final ArrayList<Dialog> dlgs = new ArrayList<Dialog>();

    @Override
    protected void onPause() {
        super.onPause();

        for (Dialog dlg : dlgs) {
            if (dlg.isShowing()) dlg.dismiss();
        }
        dlgs.clear();
    }

    @Override
    public void onDialogShown(Dialog dlg) {
        dlgs.add(dlg);
    }

    @Override
    public void onDialogClosed(Dialog dlg) {
        dlgs.remove(dlg);
    }
}
