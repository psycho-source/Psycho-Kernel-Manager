/*
 * Copyright (C) 2015 jollaman999
 * Copyright (C) 2014 OTA Update Center
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

package com.jollakernelupdater;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.jollakernelupdater.utils.Config;
import com.jollakernelupdater.utils.DialogCallback;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class DownloadListFragment extends ListFragment {

    private final ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
    private ArrayAdapter<FileInfo> fileAdapter = null;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.download_list, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFileList();
    }

    @Override
    public void onDestroy() {
        if (fileAdapter != null) {
            fileList.clear();
            fileAdapter = null;
        }
        super.onDestroy();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final FileInfo info = fileList.get(position);
        final Dialog dlg = new AlertDialog.Builder(getActivity())
                .setTitle(info.toString())
                .setIcon(R.drawable.ic_archive)
                .setItems(new String[]{
                        getString(R.string.flash), getString(R.string.delete)
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                        case 0:
                            DownloadsActivity.is_called_by_DownloadList = true;
                            DownloadsActivity.DownloadList_File_Name = info.toString();

                            Intent intent = new Intent(getActivity(), DownloadsActivity.class);
                            intent.setAction(DownloadsActivity.FLASH_KERNEL_ACTION);
                            getActivity().startActivity(intent);
                            break;
                        case 1:
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            //Yes button clicked
                                            if (info.file.delete()) {
                                                Toast.makeText(getActivity(), R.string.toast_delete, Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), R.string.toast_delete_error, Toast.LENGTH_SHORT).show();
                                            }
                                            updateFileList();
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(R.string.are_you_sure)
                                    .setPositiveButton(R.string.yes, dialogClickListener)
                                    .setNegativeButton(R.string.no, dialogClickListener).show();
                            break;
                        }
                    }
                })
                .create();

        dlg.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if (getActivity() instanceof DialogCallback) ((DialogCallback) getActivity()).onDialogShown(dlg);
            }
        });
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (getActivity() instanceof DialogCallback) ((DialogCallback) getActivity()).onDialogClosed(dlg);
            }
        });
        dlg.show();
    }

    protected void updateFileList() {
        File dir = Config.KERNEL_DL_PATH_FILE;
        File[] files = dir.listFiles();
        fileList.clear();
        for (File file : files) {
            if (file.isDirectory()) continue;
            fileList.add(new FileInfo(file));
        }

        if (fileAdapter == null) {
            fileAdapter = new ArrayAdapter<FileInfo>(getActivity(), R.layout.download_file, fileList);
            setListAdapter(fileAdapter);
        } else {
            fileAdapter.notifyDataSetChanged();
        }
    }

    protected class FileInfo {
        private File file;
        private String name;

        public FileInfo(File file) {
            this.file = file;

            name = file.getName();
            if (!name.endsWith(".zip")) return;

            name = name.substring(0, name.length() - 4);
            if (!name.contains("__")) return;
        }

        @Override
        public String toString() {
            return getString(R.string.downloads_file, name)  + ".zip";
        }
    }
}
