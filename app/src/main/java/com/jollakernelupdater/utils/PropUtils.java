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

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropUtils {
    private static final String GEN_OTA_PROP = "/system/jolla-kernel_updater.prop";
    private static final String KERNEL_OTA_PROP = "/proc/jolla-kernel";

    private static /*final*/ boolean KERNEL_OTA_ENABLED;
    private static String cachedKernelID = null;
    private static Date cachedKernelDate = null;
    private static String cachedKernelVer = null;
    private static String cachedKernelUname = null;

    private static String cachedSystemSdPath = null;
    private static String cachedRecoverySdPath = null;

    private static Boolean cachedNoFlash = null;
    private static String cachedRebootCmd = null;

    static {
        KERNEL_OTA_ENABLED = new File(KERNEL_OTA_PROP).exists();
    }

    // from AOSP source: packages/apps/Settings/src/com/android/settings/DeviceInfoSettings.java
    private static final String PROC_VERSION_REGEX =
            "\\w+\\s+" + /* ignore: Linux */
            "\\w+\\s+" + /* ignore: version */
            "([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
            "\\(([^\\s@]+@[^\\s@]+)\\)+\\s+" + /* group 2: (xxxxxx@xxxxx.constant) */
            // "(gcc" followed by anything up to two consecutive ")"
            // separated by only white space (which seems to be the norm)
            "\\(gcc.*\\)\\s+" +
            "([^\\s]+)\\s+" + /* group 3: #26 */
            "(?:SMP\\s+)?" + /* ignore: SMP (optional) */
            "(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
            "(.+)"; /* group 4: date */

    public static boolean isKernelOtaEnabled() {
        return KERNEL_OTA_ENABLED;
    }

    static String getKernelOtaID() {
        if (!KERNEL_OTA_ENABLED) return null;
        if (cachedKernelID == null) {
            readKernelOtaProp();
        }
        return cachedKernelID;
    }

    public static Date getKernelOtaDate() {
        if (!KERNEL_OTA_ENABLED) return null;
        if (cachedKernelDate == null) {
            readKernelOtaProp();
        }
        return cachedKernelDate;
    }

    public static String getKernelOtaVersion() {
        if (!KERNEL_OTA_ENABLED) return null;
        if (cachedKernelVer == null) {
            readKernelOtaProp();
        }
        return cachedKernelVer;
    }

    public static String getKernelVersion() {
        if (cachedKernelUname == null) {
            ShellCommand cmd = new ShellCommand();
            ShellCommand.CommandResult procVerResult = cmd.sh.runWaitFor("cat /proc/version");
            if (procVerResult.stdout.length() == 0) return null;

            Pattern p = Pattern.compile(PROC_VERSION_REGEX);
            Matcher m = p.matcher(procVerResult.stdout);

            if (!m.matches() || m.groupCount() < 4) {
                return null;
            } else {
                cachedKernelUname = (new StringBuilder(m.group(1)).append("\n").append(
                        m.group(2)).append(" ").append(m.group(3)).append("\n")
                        .append(m.group(4))).toString();
            }
        }
        return cachedKernelUname;
    }

    static String getSystemSdPath() {
        if (cachedSystemSdPath == null) {
            readGenOtaProp();
        }
        return cachedSystemSdPath;
    }

    public static String getRecoverySdPath() {
        if (cachedRecoverySdPath == null) {
            readGenOtaProp();
        }
        return cachedRecoverySdPath;
    }

    public static boolean getNoFlash() {
        if (cachedNoFlash == null) {
            readGenOtaProp();
        }
        return cachedNoFlash;
    }

    public static String getRebootCmd() {
        if (cachedRebootCmd == null) {
            readGenOtaProp();
        }
        return cachedRebootCmd;
    }

    @SuppressLint("SdCardPath")
    private static String getDefaultRecoverySdPath() {
        String userPath = "";
        if (Environment.isExternalStorageEmulated() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            userPath = "/0";
        }

        return "/sdcard" + userPath;
    }
    private static void readGenOtaProp() {
        if (!new File(GEN_OTA_PROP).exists()) {
            cachedNoFlash = LegacyCompat.getNoflash();
            cachedRebootCmd = LegacyCompat.getRebootCmd();
            cachedSystemSdPath = LegacyCompat.getSystemSdPath();
            cachedRecoverySdPath = LegacyCompat.getRecoverySdPath();

            if (cachedNoFlash == null) cachedNoFlash = false;
            if (cachedRebootCmd == null) cachedRebootCmd = "reboot recovery";
            if (cachedSystemSdPath == null) cachedSystemSdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (cachedRecoverySdPath == null) cachedRecoverySdPath = getDefaultRecoverySdPath();
            return;
        }

        ShellCommand cmd = new ShellCommand();
        ShellCommand.CommandResult catResult = cmd.sh.runWaitFor("cat " + GEN_OTA_PROP);
        if (catResult.stdout.length() == 0) return;

        try {
            JSONObject genOtaProp = new JSONObject(catResult.stdout);
            cachedNoFlash = genOtaProp.optBoolean("noflash", false);
            cachedRebootCmd = genOtaProp.optString("rebootcmd", "reboot recovery");
            cachedSystemSdPath = genOtaProp.optString("system_sdpath", Environment.getExternalStorageDirectory().getAbsolutePath());
            cachedRecoverySdPath = genOtaProp.optString("recovery_sdpath", getDefaultRecoverySdPath());
        } catch (JSONException e) {
            Log.e(Config.LOG_TAG + "ReadOTAProp", "Error in ota.prop file!");
        }
    }

    private static void readKernelOtaProp() {
        if (!KERNEL_OTA_ENABLED) return;

        ShellCommand cmd = new ShellCommand();
        ShellCommand.CommandResult catResult = cmd.sh.runWaitFor("cat " + KERNEL_OTA_PROP);
        if (catResult.stdout.length() == 0) return;

        try {
            JSONObject kernelOtaProp = new JSONObject(catResult.stdout);
            cachedKernelID = kernelOtaProp.getString("kernel-name");
            cachedKernelVer = kernelOtaProp.getString("version");
            cachedKernelDate = Utils.parseDate(kernelOtaProp.getString("buildtime"));
            Log.i(Config.LOG_TAG+ "ReadOTAProp", "kernel-name: " + cachedKernelID);
            Log.i(Config.LOG_TAG+ "ReadOTAProp", "version: " + cachedKernelVer);
            Log.i(Config.LOG_TAG+ "ReadOTAProp", "buildtime: " + cachedKernelDate);
        } catch (JSONException e) {
            Log.e(Config.LOG_TAG + "ReadOTAProp", "Error in jolla-kernel.prop file!");
        }
    }

    private static class LegacyCompat {
        static final String OTA_REBOOT_CMD_PROP = "otaupdater.rebootcmd";
        static final String OTA_NOFLASH_PROP = "otaupdater.noflash";
        static final String OTA_SYSTEM_SD_PATH_PROP = "otaupdater.sdcard.os";
        static final String OTA_RECOVERY_SD_PATH_PROP = "otaupdater.sdcard.recovery";

        static String getRebootCmd() {
            ShellCommand cmd = new ShellCommand();
            ShellCommand.CommandResult propResult = cmd.sh.runWaitFor("getprop " + OTA_REBOOT_CMD_PROP);
            if (propResult.stdout.length() == 0) return null;
            return propResult.stdout;
        }

        static Boolean getNoflash() {
            ShellCommand cmd = new ShellCommand();
            ShellCommand.CommandResult propResult = cmd.sh.runWaitFor("getprop " + OTA_NOFLASH_PROP);
            if (propResult.stdout.length() == 0) return null;
            return propResult.stdout.equals("1") || propResult.stdout.equalsIgnoreCase("true");
        }

        @SuppressLint("SdCardPath")
        static String getSystemSdPath() {
            ShellCommand cmd = new ShellCommand();
            ShellCommand.CommandResult propResult = cmd.sh.runWaitFor("getprop " + OTA_SYSTEM_SD_PATH_PROP);
            if (propResult.stdout.length() == 0) return null;
            return propResult.stdout;
        }

        @SuppressLint("SdCardPath")
        static String getRecoverySdPath() {
            ShellCommand cmd = new ShellCommand();
            ShellCommand.CommandResult propResult = cmd.sh.runWaitFor("getprop " + OTA_RECOVERY_SD_PATH_PROP);
            if (propResult.stdout.length() == 0) return null;
            return propResult.stdout;
        }
    }
}
