/*
 * Copyright (C) 2014 OTA Update Center
 * Copyright (C) 2017 jollaman999
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jollakernelupdater.utils;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public abstract class BaseDialogActivity extends AppCompatActivity implements DialogCallback {
    private final ArrayList<Dialog> dialogs = new ArrayList<>();

    @Override
    protected void onPause() {
        super.onPause();

        for (Dialog dlg : dialogs) {
            if (dlg.isShowing()) dlg.dismiss();
        }
        dialogs.clear();
    }

    @Override
    public void onDialogShown(Dialog dlg) {
        dialogs.add(dlg);
    }

    @Override
    public void onDialogClosed(Dialog dlg) {
        dialogs.remove(dlg);
    }
}
