package com.grmasa.soundtoggle;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class Tutorial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        WebView webview = findViewById(R.id.webview);
        webview.setVisibility(View.VISIBLE);
        String dataString = "<head><style type='text/css'>"
                + "body{margin:auto auto;text-align:center;} </style></head>"
                + "<body><img src=\"tutorial.gif\"\"/></body>";
        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.loadDataWithBaseURL("file:///android_res/drawable/", dataString, "text/html", "utf-8", null);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return false;
    }
}

