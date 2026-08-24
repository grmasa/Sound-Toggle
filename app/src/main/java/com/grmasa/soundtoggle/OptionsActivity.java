package com.grmasa.soundtoggle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class OptionsActivity extends AppCompatActivity {

    private final List<CheckBox> checkBoxes = new ArrayList<>();
    private MaterialSwitch swVisualSilentMode, swVisualTile;

    private static final String KEY_EXCLUDED_MODES = "excluded_modes";
    private static final String KEY_TOGGLE_OPTION = "toggle_option";
    private static final String dKEY_TOGGLE_TILE_OPTION = "toggle_tile_option";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        LinearLayout checkBoxLayout = findViewById(R.id.ll_exclude_modes);
        for (SoundModes.Mode mode : SoundModes.MODES) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(mode.name);
            checkBox.setTag(mode.name);
            checkBoxLayout.addView(checkBox);
            checkBoxes.add(checkBox);
        }

        swVisualSilentMode = findViewById(R.id.sw_visual_silent_mode);
        swVisualTile = findViewById(R.id.sw_visual_tile_mode);

        Button btnSave = findViewById(R.id.btn_save_exclusions);

        loadPreferences();

        btnSave.setOnClickListener(v -> {
            savePreferences();
            Toast.makeText(this, this.getString(R.string.options_saved), Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        Set<String> excluded = prefs.getStringSet(KEY_EXCLUDED_MODES, new HashSet<>());
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(excluded.contains(checkBox.getTag().toString()));
        }

        int option = prefs.getInt(KEY_TOGGLE_OPTION, 0);
        swVisualSilentMode.setChecked(option == 1);
        int optionTile = prefs.getInt(dKEY_TOGGLE_TILE_OPTION, 1);
        swVisualTile.setChecked(optionTile == 1);
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = getSharedPreferences("prefs", MODE_PRIVATE).edit();

        Set<String> excluded = new HashSet<>();
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) {
                excluded.add(checkBox.getTag().toString());
            }
        }
        editor.putStringSet(KEY_EXCLUDED_MODES, excluded);

        int option = swVisualSilentMode.isChecked() ? 1 : 0;
        editor.putInt(KEY_TOGGLE_OPTION, option);
        int optionTile = swVisualTile.isChecked() ? 1 : 0;
        editor.putInt(dKEY_TOGGLE_TILE_OPTION, optionTile);

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