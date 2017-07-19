/*
 * Copyright (C) 2014 OTA Update Center
 * Copyright (C) 2017 jollaman999
 * Copyright (C) 2017 Psycho-Mods
 *  ** (Code from Kevin @ TeslaCoil) **
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

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;

class ShellCommand {

    final SH sh;

    ShellCommand() {
        sh = new SH("sh");
    }

    class CommandResult {
        final String stdout;
        final String stderr;
        final Integer exit_value;

        CommandResult(Integer exit_value_in, String stdout_in, String stderr_in)
        {
            exit_value = exit_value_in;
            stdout = stdout_in;
            stderr = stderr_in;
        }
    }

    public class SH {
        private String SHELL = "sh";

        SH(String SHELL_in) {
            SHELL = SHELL_in;
        }

        Process run(String s) {
            Process process;
            try {
                process = Runtime.getRuntime().exec(SHELL);
                DataOutputStream toProcess = new DataOutputStream(process.getOutputStream());
                toProcess.writeBytes("exec " + s + "\n");
                toProcess.flush();
            } catch(Exception e) {
                Log.e(Config.LOG_TAG + "ShellCmd", "Exception while trying to run: '" + s + "' " + e.getMessage());
                process = null;
            }
            return process;
        }

        @SuppressWarnings("deprecation")
        private String getStreamLines(InputStream is) {
            String out = null;
            StringBuffer buffer = null;
            DataInputStream dis = new DataInputStream(is);

            try {
                if (dis.available() > 0) {
                    buffer = new StringBuffer(dis.readLine());
                    while(dis.available() > 0)
                        buffer.append("\n").append(dis.readLine());
                }
                dis.close();
            } catch (Exception ex) {
                Log.e(Config.LOG_TAG + "ShellCmd", ex.getMessage());
            }
            if (buffer != null)
                out = buffer.toString();
            return out;
        }

        public CommandResult runWaitFor(String s) {
            Process process = run(s);
            Integer exit_value = null;
            String stdout = null;
            String stderr = null;
            if (process != null) {
                try {
                    exit_value = process.waitFor();

                    stdout = getStreamLines(process.getInputStream());
                    stderr = getStreamLines(process.getErrorStream());

                } catch(InterruptedException | NullPointerException e) {
                    Log.e(Config.LOG_TAG + "ShellCmd", "runWaitFor " + e.toString());
                }
            }
            return new CommandResult(exit_value, stdout, stderr);
        }
    }
}
