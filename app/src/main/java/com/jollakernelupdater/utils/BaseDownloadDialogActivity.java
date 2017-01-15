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
import android.os.Bundle;

import com.jollakernelupdater.DownloadBarFragment;

import org.jetbrains.annotations.NotNull;

public abstract class BaseDownloadDialogActivity extends BaseDialogActivity implements DownloadDialogCallback {
    private static final String KEY_DOWNLOAD_ID = "downloadID";

    protected Long dialogDownloadID = null;

    @Override
    protected void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(KEY_DOWNLOAD_ID)) {
            DownloadBarFragment.showDownloadingDialog(this, savedInstanceState.getLong(KEY_DOWNLOAD_ID, -1), this);
        }
    }

    @Override
    public void onDownloadDialogShown(long dlID, Dialog dlg) {
        dialogDownloadID = dlID;
    }

    @Override
    public void onDownloadDialogClosed(long dlID, Dialog dlg) {
        dialogDownloadID = null;
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dialogDownloadID != null) outState.putLong(KEY_DOWNLOAD_ID, dialogDownloadID);
    }
}
