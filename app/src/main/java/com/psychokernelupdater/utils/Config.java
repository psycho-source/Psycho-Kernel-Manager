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
import android.content.SharedPreferences;

import java.io.File;

public class Config {

    public static final String VERSION = "2.0";

    public static final String LOG_TAG = "psycho::";
    public static final String SITE_GITHUB_URL = "https://github.com/psycho-source/Psycho-Kernel";
    public static final String KERNEL_SD_PATH = "/Psycho-Kernel/";
    static final String HTTPC_UA = "Psycho-Mods Updater App";
    static final String SITE_BASE_URL = "https://psycho-mods.000webhostapp.com/";
    static final String KERNEL_PULL_URL = "Psycho-Kernel_json/";
    static final int KERNEL_NOTIF_ID = 200;
    static final int KERNEL_FAILED_NOTIF_ID = 201;
    static final int KERNEL_FLASH_NOTIF_ID = 202;
    private static final String SITE_CHANGELOG_URL_PART1 = "https://raw.githubusercontent.com/";
    private static final String SITE_CHANGELOG_URL_PART2_EN = "psycho-source/Psycho-Kernel/master/flashable/META-INF/com/google/android/aroma/psycho.txt";
    static final String SITE_CHANGELOG_URL_EN = SITE_CHANGELOG_URL_PART1 +
            SITE_CHANGELOG_URL_PART2_EN;
    private static final String SITE_CHANGELOG_URL_PART2_KO = "psycho-source/Psycho-Kernel/master/flashable/META-INF/com/google/android/aroma/psycho.txt";
    static final String SITE_CHANGELOG_URL_KO = SITE_CHANGELOG_URL_PART1 +
            SITE_CHANGELOG_URL_PART2_KO;
    private static final String BASE_DL_PATH = PropUtils.getSystemSdPath();
    private static final String KERNEL_DL_PATH = BASE_DL_PATH + "/Psycho-Kernel/";
    public static final File KERNEL_DL_PATH_FILE = new File(KERNEL_DL_PATH);
    private static final File DL_PATH_FILE = new File(BASE_DL_PATH);
    private static final String PREFS_NAME = "prefs";
    private static Config instance = null;

    static {
        //noinspection ResultOfMethodCallIgnored
        DL_PATH_FILE.mkdirs();
        //noinspection ResultOfMethodCallIgnored
        KERNEL_DL_PATH_FILE.mkdirs();
    }

    private final SharedPreferences PREFS;
    private boolean showNotif = true;
    private boolean wifiOnlyDl = true;
    private boolean autoDl = false;
    private boolean themeSt = false;
    private boolean ignoredUnsupportedWarn = false;
    private boolean ignoredDataWarn = false;
    private boolean ignoredWifiWarn = false;
    private KernelInfo storedKernelUpdate = null;
    private long romDownloadID = -1;
    private long kernelDownloadID = -1;

    private Config(Context ctx) {
        ctx = ctx.getApplicationContext();
        assert ctx != null;
        PREFS = ctx.getSharedPreferences(PREFS_NAME, 0);

        showNotif = PREFS.getBoolean("showNotif", showNotif);
        wifiOnlyDl = PREFS.getBoolean("wifiOnlyDl", wifiOnlyDl);

        autoDl = PREFS.getBoolean("autoDl", autoDl);
        themeSt = PREFS.getBoolean("themeSt", themeSt);

        ignoredUnsupportedWarn = PREFS.getBoolean("ignoredUnsupportedWarn", ignoredUnsupportedWarn);
        ignoredDataWarn = PREFS.getBoolean("ignoredDataWarn", ignoredDataWarn);
        ignoredWifiWarn = PREFS.getBoolean("ignoredWifiWarn", ignoredWifiWarn);

        if (PREFS.contains("kernel_info_name")) {
            if (PropUtils.isKernelOtaEnabled()) {
                storedKernelUpdate = KernelInfo.FACTORY.fromSharedPrefs(PREFS);
            } else {
                clearStoredKernelUpdate();
            }
        }

        kernelDownloadID = PREFS.getLong("kernelDownloadID", kernelDownloadID);
    }

    public static synchronized Config getInstance(Context ctx) {
        if (instance == null) instance = new Config(ctx);
        return instance;
    }

    public boolean getThemeSt() {
        return themeSt;
    }

    public void setThemeSt(boolean themeSt) {
        this.themeSt = themeSt;
        putBoolean("themeSt", themeSt);
    }

    public boolean getShowNotif() {
        return showNotif;
    }

    public void setShowNotif(boolean showNotif) {
        this.showNotif = showNotif;
        putBoolean("showNotif", showNotif);
    }

    public boolean getWifiOnlyDl() {
        return wifiOnlyDl;
    }

    public void setWifiOnlyDl(boolean wifiOnlyDl) {
        this.wifiOnlyDl = wifiOnlyDl;
        putBoolean("wifiOnlyDl", wifiOnlyDl);
    }

    public boolean getAutoDlState() {
        return autoDl;
    }

    public void setAutoDlState(boolean autoDl) {
        this.autoDl = autoDl;
        putBoolean("autoDl", autoDl);
    }

    public void clearIgnored() {
        ignoredUnsupportedWarn = false;
        ignoredDataWarn = false;
        ignoredWifiWarn = false;

        synchronized (PREFS) {
            SharedPreferences.Editor editor = PREFS.edit();
            editor.putBoolean("ignoredUnsupportedWarn", ignoredUnsupportedWarn);
            editor.putBoolean("ignoredDataWarn", ignoredDataWarn);
            editor.putBoolean("ignoredWifiWarn", ignoredWifiWarn);
            editor.apply();
        }
    }

    public boolean getIgnoredUnsupportedWarn() {
        return ignoredUnsupportedWarn;
    }

    public void setIgnoredUnsupportedWarn(boolean ignored) {
        this.ignoredUnsupportedWarn = ignored;
        putBoolean("ignoredUnsupportedWarn", ignored);
    }

    public boolean getIgnoredDataWarn() {
        return ignoredDataWarn;
    }

    public void setIgnoredDataWarn(boolean ignored) {
        this.ignoredDataWarn = ignored;
        putBoolean("ignoredDataWarn", ignored);
    }

    public boolean getIgnoredWifiWarn() {
        return ignoredWifiWarn;
    }

    public void setIgnoredWifiWarn(boolean ignored) {
        this.ignoredWifiWarn = ignored;
        putBoolean("ignoredWifiWarn", ignored);
    }

    public boolean hasStoredKernelUpdate() {
        return storedKernelUpdate != null;
    }

    public KernelInfo getStoredKernelUpdate() {
        return storedKernelUpdate;
    }

    private void storeKernelUpdate(KernelInfo info) {
        this.storedKernelUpdate = info;
        synchronized (PREFS) {
            SharedPreferences.Editor editor = PREFS.edit();
            info.putToSharedPrefs(editor);
            editor.apply();
        }
    }

    public void clearStoredKernelUpdate() {
        storedKernelUpdate = null;
        synchronized (PREFS) {
            SharedPreferences.Editor editor = PREFS.edit();
            KernelInfo.FACTORY.clearFromSharedPrefs(editor);
            editor.apply();
        }
    }

    boolean isDownloadingRom() {
        return romDownloadID != -1;
    }

    private void storeKernelDownloadID(long downloadID) {
        kernelDownloadID = downloadID;
        putLong("kernelDownloadID", kernelDownloadID);
    }

    public long getKernelDownloadID() {
        return kernelDownloadID;
    }

    public boolean isDownloadingKernel() {
        return kernelDownloadID != -1;
    }

    public void clearDownloadingKernel() {
        if (romDownloadID != -1) storeKernelDownloadID(-1);
    }

    void storeDownloadID(long downloadID) {
        storeKernelDownloadID(downloadID);
    }

    void storeUpdate(BaseInfo info) {
        storeKernelUpdate((KernelInfo) info);
    }

    void clearStoredUpdate() {
        clearStoredKernelUpdate();
    }

    private void putBoolean(String name, boolean value) {
        synchronized (PREFS) {
            SharedPreferences.Editor editor = PREFS.edit();
            editor.putBoolean(name, value);
            editor.apply();
        }
    }

    private void putLong(String name, long value) {
        synchronized (PREFS) {
            SharedPreferences.Editor editor = PREFS.edit();
            editor.putLong(name, value);
            editor.apply();
        }
    }
}
