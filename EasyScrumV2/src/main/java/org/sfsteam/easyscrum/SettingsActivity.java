package org.sfsteam.easyscrum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;


public class SettingsActivity extends ActionBarActivity {

    public static final String SENSITIVITY = "sensitivity";
    public static final String ACCELERATION = "acceleration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        int sensitivityInt = settings.getInt(SENSITIVITY,5);
        int accelerationInt = settings.getInt(ACCELERATION,5);

        SeekBar sensSeek = (SeekBar) findViewById(R.id.sensSeekBar);
        sensSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(SENSITIVITY, seekBar.getProgress());
                editor.commit();
            }
        });

        sensSeek.setProgress(sensitivityInt);

        SeekBar accSeek = (SeekBar) findViewById(R.id.accelSeekBar);
        accSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(ACCELERATION, seekBar.getProgress());
                editor.commit();
            }
        });

        accSeek.setProgress(accelerationInt);

        Button testButton = (Button) findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, CardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("card", getResources().getString(R.string.test_sensor));
                startActivity(intent);
            }
        });
    }
}