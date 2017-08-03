package com.psychokernelupdater;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class PaypalTab extends Fragment {
    Button btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.paypal, container, false);

        btn = rootView.findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent websiteBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/psychomod"));
                startActivity(websiteBrowserIntent);
            }
        });

        return rootView;
    }
}