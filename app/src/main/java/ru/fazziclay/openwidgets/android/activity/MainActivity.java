
package ru.fazziclay.openwidgets.android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.MessageFormat;
import java.util.Iterator;

import ru.fazziclay.fazziclaylibs.FileUtils;
import ru.fazziclay.openwidgets.R;
import ru.fazziclay.openwidgets.UpdateChecker;
import ru.fazziclay.openwidgets.android.service.WidgetsUpdaterService;
import ru.fazziclay.openwidgets.deprecated.widgets.WidgetsManager;
import ru.fazziclay.openwidgets.deprecated.widgets.data.BaseWidget;
import ru.fazziclay.openwidgets.deprecated.widgets.data.DateWidget;
import ru.fazziclay.openwidgets.deprecated.widgets.data.WidgetsData;
import ru.fazziclay.openwidgets.util.NotificationUtils;


public class MainActivity extends AppCompatActivity {
    private static AppCompatActivity instance;
    public static AppCompatActivity getInstance() {
        return instance;
    }
    public static void setInstance(MainActivity instance) {
        MainActivity.instance = instance;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationUtils.createNotifyChannel(this, "ForegroundWidgetsUpdaterService", "name", "description");
        NotificationUtils.createNotifyChannel(this, "DebugTest", "name", "description");
        NotificationUtils.createNotifyChannel(this, "UpdateChecker", "name", "description");

    }

    @Override
    protected void onResume() {
        super.onResume();

        setInstance(this);
        WidgetsData.setFilePath(this);
        DebugActivity.setOnlyDebugFlagPath(this);

        if ( FileUtils.read(DebugActivity.onlyDebugFlagPath).equals("1") ) {
            Intent intent = new Intent();
            intent.setClass(this, DebugActivity.class);
            startActivity(intent);
            return;
        }

        setContentView(R.layout.activity_main);
        setTitle(R.string.activityTitle_main);

        WidgetsData.loadIsNot();

        loadMainButtons();      // главные кнопок
        loadWidgetsButtons();   // кнопки виджетов
        loadUpdateChecker();    // update checker

        WidgetsUpdaterService.startIsNot(this);
    }

    private void loadMainButtons() {
        Button aboutButton = findViewById(R.id.button_about);
        Button settingsButton = findViewById(R.id.button_settings);

        aboutButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), AboutActivity.class);
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void loadWidgetsButtons() {
        LinearLayout widgetsButtonsSlot = findViewById(R.id.widgetsButtonsSlot);

        Iterator<Integer> iterator = WidgetsManager.getIterator();
        int i = 0;
        while (iterator.hasNext()) {
            int widgetId = iterator.next();
            BaseWidget widget = WidgetsManager.getWidgetById(widgetId);

            Button button = new Button(this);
            button.setAllCaps(false);
            Intent intent = new Intent().putExtra("widget_id", widgetId);
            CharSequence widgetName = getText(R.string.widgetName_unsupported);
            boolean isSupported = false;

            assert widget != null;
            if (widget.widgetType == DateWidget.type) {
                intent.setClass(this, DateWidgetConfiguratorActivity.class);
                isSupported = true;
                widgetName = getText(R.string.widgetName_date);
            }

            button.setText(MessageFormat.format("{0} ({1})", widgetName, widgetId));
            if (isSupported) {
                button.setOnClickListener(v -> startActivity(intent));
            }

            widgetsButtonsSlot.addView(button);
            i++;
        }

        if (i > 0) {
            findViewById(R.id.widgetsIsNoneText).setVisibility(View.GONE);
            CheckBox checkBox = findViewById(R.id.checkBox_widgetsIdMode);
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(WidgetsUpdaterService.idMode);
            checkBox.setOnClickListener(v -> WidgetsUpdaterService.idMode = checkBox.isChecked());
        }
    }



    private void loadUpdateChecker() {
        UpdateChecker.sendNewUpdateAvailableNotification(this);
        UpdateChecker.getVersion((status, build, name, download_url) -> runOnUiThread(() -> {
            if (status != 0 && status != 3) {
                LinearLayout updateCheckerLayout = findViewById(R.id.updateChecker);
                LinearLayout updateCheckerButtonsLayout = findViewById(R.id.updateChecker_buttonsLayout);
                TextView updateCheckerText = findViewById(R.id.updateChecker_text);

                if (status == 2) {
                    updateCheckerText.setText(R.string.updateChecker_newFormatVersion);
                    Button siteButton = new Button(this);
                    siteButton.setText(R.string.updateChecker_button_toSite);
                    siteButton.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/fazziclay/openwidgets/releases"));
                        startActivity(browserIntent);
                    });
                    updateCheckerButtonsLayout.addView(siteButton);
                }

                if (status == -1) {
                    updateCheckerText.setText(R.string.updateChecker_versionHight);
                    Button siteButton = new Button(this);
                    siteButton.setText(R.string.updateChecker_button_toSite);
                    siteButton.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/fazziclay/openwidgets/releases"));
                        startActivity(browserIntent);
                    });

                    Button downloadButton = new Button(this);
                    downloadButton.setText(R.string.updateChecker_button_download);
                    downloadButton.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(download_url));
                        startActivity(browserIntent);
                    });
                    updateCheckerButtonsLayout.addView(siteButton);
                    updateCheckerButtonsLayout.addView(downloadButton);
                }

                if (status == 1) {
                    updateCheckerText.setText(R.string.updateChecker_updateAvailable);
                    Button siteButton = new Button(this);
                    siteButton.setText(R.string.updateChecker_button_toSite);
                    siteButton.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/fazziclay/openwidgets/releases"));
                        startActivity(browserIntent);
                    });

                    Button downloadButton = new Button(this);
                    downloadButton.setText(R.string.updateChecker_button_download);
                    downloadButton.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(download_url));
                        startActivity(browserIntent);
                    });
                    updateCheckerButtonsLayout.addView(siteButton);
                    updateCheckerButtonsLayout.addView(downloadButton);
                }

                updateCheckerLayout.setVisibility(View.VISIBLE);
            }
        }));
    }
}