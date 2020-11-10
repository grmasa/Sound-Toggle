package com.grmasa.soundtoggle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView done, step, step_text;
    private Button permission_button, tutorial_button, skip_tutorial;
    private SharedPreferences sharedpreferences;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        step = findViewById(R.id.step);
        step.setTextSize(25);
        step_text = findViewById(R.id.step_text);
        done = findViewById(R.id.all_good_text);
        permission_button = findViewById(R.id.request_permission);
        permission_button.setVisibility(View.GONE);
        tutorial_button = findViewById(R.id.tutorial_button);
        tutorial_button.setVisibility(View.GONE);
        WebView webview = findViewById(R.id.webview);
        tutorial_button.setOnClickListener(v -> {
            webview.setVisibility(View.VISIBLE);
            String dataString = "<head><style type='text/css'>"
                    +"body{margin:auto auto;text-align:center;} </style></head>"
                    +"<body><img src=\"tutorial.gif\"\"/></body>";
            webview.setBackgroundColor(Color.TRANSPARENT);
            webview.loadDataWithBaseURL("file:///android_res/drawable/",dataString,"text/html","utf-8",null);
        });
        skip_tutorial = findViewById(R.id.skip_tutorial);
        skip_tutorial.setVisibility(View.GONE);
        skip_tutorial.setOnClickListener(v -> {
            skip_tutorial.setVisibility(View.GONE);
            webview.setVisibility(View.GONE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("tutorial", "done");
            editor.apply();
            step.setVisibility(View.GONE);
            tutorial_button.setVisibility(View.GONE);
            done.setVisibility(View.VISIBLE);
            step_text.setVisibility(View.GONE);
        });

        sharedpreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    public void onResume() {
        super.onResume();
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager.isNotificationPolicyAccessGranted()
                && sharedpreferences.getString("tutorial", "").equals("done")) {
            hideTutorial();
        } else {
            showTutorial(notificationManager);
        }
    }

    private void showTutorial(NotificationManager notificationManager) {
        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            step.setText(getResources().getString(R.string.step1));
            step_text.setText(getResources().getString(R.string.permission_text));
            permission_button.setVisibility(View.VISIBLE);
        } else if (!sharedpreferences.getString("tutorial", "").equals("done")) {
            if(permission_button.getVisibility()== View.VISIBLE){
                permission_button.setVisibility(View.GONE);
            }
            step.setText(getResources().getString(R.string.step2));
            step_text.setText(getResources().getString(R.string.tutorial_text));
            tutorial_button.setVisibility(View.VISIBLE);
            skip_tutorial.setVisibility(View.VISIBLE);
        }
        done.setVisibility(View.GONE);
    }

    private void hideTutorial() {
        if(permission_button.getVisibility()== View.VISIBLE){
            permission_button.setVisibility(View.GONE);
        } else if(tutorial_button.getVisibility()== View.VISIBLE){
            tutorial_button.setVisibility(View.GONE);
            skip_tutorial.setVisibility(View.GONE);
            step_text.setVisibility(View.GONE);
        }
        done.setVisibility(View.VISIBLE);
    }

    public void requestPermission(View view) {
        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
        this.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.tutorial){
            Intent myIntent = new Intent(this, Tutorial.class);
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}