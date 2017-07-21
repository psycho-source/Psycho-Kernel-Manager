/*
 * Copyright (C) 2014 OTA Update Center
 * Copyright (C) 2017 jollaman999
 * Copyright (C) 2017 Psycho-Mods
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

package com.psychokernelupdater.utils;

import android.content.Context;

import com.psychokernelupdater.DownloadReceiver;
import com.psychokernelupdater.DownloadsActivity;
import com.psychokernelupdater.psychokernelUpdaterActivity;
import com.psychokernelupdater.R;

import java.io.File;
import java.util.Date;

public class KernelInfo extends BaseInfo {
    private static final String KEY_NAME = "kernel";

    public static final InfoFactory<KernelInfo> FACTORY = new InfoFactory<>(KernelInfo.class);
    public static final Creator<KernelInfo> CREATOR = FACTORY.getParcelableCreator();

    @Override
    public String getFlashAction() {
        return DownloadsActivity.FLASH_KERNEL_ACTION;
    }

    @Override
    public int getDownloadingTitle() {
        return R.string.kernel_download_alert_title;
    }

    @Override
    public int getDownloadDoneTitle() {
        return R.string.kernel_download_done;
    }

    @Override
    public int getDownloadFailedTitle() {
        return R.string.kernel_download_failed;
    }

    @Override
    public int getFailedNotifID() {
        return Config.KERNEL_FAILED_NOTIF_ID;
    }

    @Override
    public int getFlashNotifID() {
        return Config.KERNEL_FLASH_NOTIF_ID;
    }

    @Override
    public String getNameKey() {
        return KEY_NAME;
    }

    @Override
    public String getNotifAction() {
        return psychokernelUpdaterActivity.KERNEL_NOTIF_ACTION;
    }

    @Override
    public String getDownloadAction() {
        return DownloadReceiver.DL_KERNEL_ACTION;
    }

    @Override
    public String getDownloadSdPath() {
        return Config.KERNEL_SD_PATH;
    }

    @Override
    protected File getDownloadPathFile() {
        return Config.KERNEL_DL_PATH_FILE;
    }

    @Override
    protected int getNotifTickerStr() {
        return R.string.kernel_download_ticker;
    }

    @Override
    protected int getNotifTextStr() {
        return R.string.kernel_download_title;
    }

    @Override
    protected int getNotifDetailsStr() {
        return R.string.kernel_download_details;
    }

    @Override
    protected int getNotifID() {
        return Config.KERNEL_NOTIF_ID;
    }

    @Override
    protected int getDownloadingNotifTitle() {
        return R.string.kernel_download_progress;
    }

    @Override
    protected int getDownloadDialogMessageStr() {
        return R.string.kernel_update_to;
    }

    @Override
    protected boolean isDownloading(Context ctx) {
        return Config.getInstance(ctx).isDownloadingRom();
    }

    @Override
    protected Date getPropDate() {
        return PropUtils.getKernelOtaDate();
    }

    @Override
    protected String getPropVersion() {
        return PropUtils.getKernelOtaVersion();
    }
}
