package com.grmasa.soundtoggle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class OptionsActivity extends AppCompatActivity {

    private CheckBox cbExcludeNormal, cbExcludeVibrate, cbExcludeSilent;
    private static final String KEY_EXCLUDED_MODES = "excluded_modes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        cbExcludeNormal = findViewById(R.id.cb_exclude_normal);
        cbExcludeVibrate = findViewById(R.id.cb_exclude_vibrate);
        cbExcludeSilent = findViewById(R.id.cb_exclude_silent);
        Button btnSave = findViewById(R.id.btn_save_exclusions);

        loadExclusions();

        btnSave.setOnClickListener(v -> {
            saveExclusions();
            Toast.makeText(this, "Exclusions saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void loadExclusions() {
        SharedPreferences sharedpreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        Set<String> excluded = sharedpreferences.getStringSet(KEY_EXCLUDED_MODES, new HashSet<>());

        cbExcludeNormal.setChecked(excluded.contains("NORMAL"));
        cbExcludeVibrate.setChecked(excluded.contains("VIBRATE"));
        cbExcludeSilent.setChecked(excluded.contains("SILENT"));
    }

    private void saveExclusions() {
        Set<String> excluded = new HashSet<>();
        if (cbExcludeNormal.isChecked()) excluded.add("NORMAL");
        if (cbExcludeVibrate.isChecked()) excluded.add("VIBRATE");
        if (cbExcludeSilent.isChecked()) excluded.add("SILENT");

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        prefs.edit().putStringSet(KEY_EXCLUDED_MODES, excluded).apply();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return false;
    }
}
