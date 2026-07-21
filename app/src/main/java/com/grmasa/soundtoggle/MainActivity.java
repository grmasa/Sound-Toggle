package com.grmasa.soundtoggle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
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
import android.widget.Toast;

import org.woheller69.freeDroidWarn.FreeDroidWarn;

public class MainActivity extends AppCompatActivity {

    private TextView done, step, step_text;
    private Button permission_button, tutorial_button, skip_tutorial;
    private SharedPreferences sharedpreferences;
    private NotificationManager notificationManager;

    private final ActivityResultLauncher<Intent> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> checkAndUpdateUI());

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        sharedpreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);

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
                    + "body{margin:auto auto;text-align:center;} </style></head>"
                    + "<body><img src=\"tutorial.gif\"\"/></body>";
            webview.setBackgroundColor(Color.TRANSPARENT);
            webview.loadDataWithBaseURL("file:///android_res/drawable/", dataString, "text/html", "utf-8", null);
        });

        skip_tutorial = findViewById(R.id.skip_tutorial);
        skip_tutorial.setVisibility(View.GONE);
        skip_tutorial.setOnClickListener(v -> {
            skip_tutorial.setVisibility(View.GONE);
            webview.setVisibility(View.GONE);
            sharedpreferences.edit().putString("tutorial", "done").apply();
            checkAndUpdateUI(); // Update UI dynamically
        });
        permission_button.setOnClickListener(this::requestPermission);

        new Thread(() -> runOnUiThread(() -> FreeDroidWarn.showWarningOnUpgrade(this, 36))).start();
        checkAndUpdateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAndUpdateUI();
    }

    private void checkAndUpdateUI() {
        if (notificationManager.isNotificationPolicyAccessGranted()
                && "done".equals(sharedpreferences.getString("tutorial", ""))) {
            hideTutorial();
        } else {
            showTutorial();
        }
    }

    private void showTutorial() {
        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            step.setText(getResources().getString(R.string.step1));
            step_text.setText(getResources().getString(R.string.permission_text));
            permission_button.setVisibility(View.VISIBLE);
        } else if (!"done".equals(sharedpreferences.getString("tutorial", ""))) {
            permission_button.setVisibility(View.GONE);
            step.setText(getResources().getString(R.string.step2));
            step_text.setText(getResources().getString(R.string.tutorial_text));
            tutorial_button.setVisibility(View.VISIBLE);
            skip_tutorial.setVisibility(View.VISIBLE);
        }
        done.setVisibility(View.GONE);
    }

    private void hideTutorial() {
        permission_button.setVisibility(View.GONE);
        tutorial_button.setVisibility(View.GONE);
        skip_tutorial.setVisibility(View.GONE);
        step_text.setVisibility(View.GONE);
        step.setVisibility(View.GONE);
        done.setVisibility(View.VISIBLE);
    }

    public void requestPermission(View view) {
        Toast.makeText(this, R.string.ask_access, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

        try {
            permissionLauncher.launch(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.error_settings, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.options) {
            startActivity(new Intent(this, OptionsActivity.class));
            return true;
        } else if (id == R.id.tutorial) {
            startActivity(new Intent(this, Tutorial.class));
            return true;
        } else if (id == R.id.action_exit) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}