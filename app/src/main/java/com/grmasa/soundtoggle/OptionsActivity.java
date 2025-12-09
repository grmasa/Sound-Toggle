package com.grmasa.soundtoggle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class OptionsActivity extends AppCompatActivity {

    private CheckBox cbExcludeNormal, cbExcludeVibrate, cbExcludeSilent;
    private MaterialSwitch swVisualSilentMode;

    private static final String KEY_EXCLUDED_MODES = "excluded_modes";
    private static final String KEY_TOGGLE_OPTION = "toggle_option";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        cbExcludeNormal = findViewById(R.id.cb_exclude_normal);
        cbExcludeVibrate = findViewById(R.id.cb_exclude_vibrate);
        cbExcludeSilent = findViewById(R.id.cb_exclude_silent);
        swVisualSilentMode = findViewById(R.id.sw_visual_silent_mode);

        Button btnSave = findViewById(R.id.btn_save_exclusions);

        loadPreferences();

        btnSave.setOnClickListener(v -> {
            savePreferences();
            Toast.makeText(this, "Options saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        Set<String> excluded = prefs.getStringSet(KEY_EXCLUDED_MODES, new HashSet<>());
        cbExcludeNormal.setChecked(excluded.contains("NORMAL"));
        cbExcludeVibrate.setChecked(excluded.contains("VIBRATE"));
        cbExcludeSilent.setChecked(excluded.contains("SILENT"));

        int option = prefs.getInt(KEY_TOGGLE_OPTION, 0);
        swVisualSilentMode.setChecked(option == 1);
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = getSharedPreferences("prefs", MODE_PRIVATE).edit();

        Set<String> excluded = new HashSet<>();
        if (cbExcludeNormal.isChecked()) excluded.add("NORMAL");
        if (cbExcludeVibrate.isChecked()) excluded.add("VIBRATE");
        if (cbExcludeSilent.isChecked()) excluded.add("SILENT");
        editor.putStringSet(KEY_EXCLUDED_MODES, excluded);

        int option = swVisualSilentMode.isChecked() ? 1 : 0;
        editor.putInt(KEY_TOGGLE_OPTION, option);

        editor.apply();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return false;
    }
}