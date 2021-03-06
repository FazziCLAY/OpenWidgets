package ru.fazziclay.openwidgets.android.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import ru.fazziclay.openwidgets.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Android
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.activityTitle_settings);

        Button toDebugButton = findViewById(R.id.button_toDebug);
        toDebugButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(this, DebugActivity.class);
            startActivity(intent);
        });

        Button appLanguageButton = findViewById(R.id.button_appLanguage);
        appLanguageButton.setOnClickListener(v -> {
            Configuration config = new Configuration();

            String[] list = {"English", "Russian"};
            String[] codes = {"en", "ru"};
            int current = 0;

            new AlertDialog.Builder(this)
                    .setSingleChoiceItems(list, current, null)
                    .setPositiveButton(getText(R.string.APPLY), (dialog, whichButton) -> {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();

                        Locale locale = new Locale(codes[selectedPosition]);
                        Locale.setDefault(locale);

                        config.locale = locale;
                        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

                        Intent intent = new Intent();
                        intent.setClass(this, this.getClass());
                        startActivity(intent);
                        finish();
                    })
                    .show();
        });


    }
}