package org.sfsteam.easyscrum;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by warmount on 15.06.13.
 */
public class CardActivity extends Activity {
    private static final String PACKAGE_NAME = "org.sfsteam.easyscrum";
    RelativeLayout root;
    TextView cardTv;
    String imageName;
    ImageView cardImage;
    boolean cardState;
    boolean isImage;

    private static final int SHAKE_SENSITIVITY = 15;

    private SensorManager sensorManager;
    private float accel = SensorManager.GRAVITY_EARTH * 2.0f;
    private float accelPrevious = SensorManager.GRAVITY_EARTH * 2.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.root = (RelativeLayout) this.getLayoutInflater().inflate(R.layout.card_layout, null);
        this.setContentView(root);
        setCardSystemView();
        final String cardValue = getIntent().getStringExtra("card");
        imageName = getIntent().getStringExtra("image");
        if (imageName != null) {
            cardImage = (ImageView) root.findViewById(R.id.cardImage);
            cardImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            cardImage.setImageURI(Uri.parse(imageName));
        }

        cardTv = (TextView) root.findViewById(R.id.card_num);
        cardTv.setText(Html.fromHtml(cardValue).toString());
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCard(cardTv, imageName);
            }
        });
        switch (cardValue.length()) {
            case 1:
            case 2:
                cardTv.setTextSize(200);
                break;
            case 3:
                cardTv.setTextSize(150);
                break;
        }
        cardTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(
                sensorListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void setCardSystemView() {
        if (Build.VERSION.SDK_INT > 18) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
        } else if (Build.VERSION.SDK_INT > 15) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public void showCard(View cardTv, String imageName) {
        LinearLayout startLay = (LinearLayout) root.findViewById(R.id.start);
        startLay.setVisibility(View.GONE);
        cardState = true;
        if (imageName == null) {
            cardTv.setVisibility(View.VISIBLE);
            return;
        }
        cardImage.setVisibility(View.VISIBLE);
        isImage = true;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (cardState) {
            outState.putBoolean("open", true);
        }
        if (isImage) {
            outState.putBoolean("image", true);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (!savedInstanceState.getBoolean("open")) {
            return;
        }
        LinearLayout startLay = (LinearLayout) root.findViewById(R.id.start);
        startLay.setVisibility(View.GONE);
        if (!savedInstanceState.getBoolean("image")) {
            TextView cardTv = (TextView) root.findViewById(R.id.card_num);
            cardTv.setVisibility(View.VISIBLE);
        } else {
            ImageView cardImage = (ImageView) root.findViewById(R.id.cardImage);
            cardImage.setVisibility(View.VISIBLE);
            isImage = true;
        }
        cardState = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        sensorManager.registerListener(
                sensorListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onStop() {
        sensorManager.unregisterListener(sensorListener);

        super.onStop();
    }

    private final SensorEventListener sensorListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            accelPrevious = accel;
            accel = (float) Math.sqrt((double) (x * x + y * y + z * z));
            if (accel - accelPrevious > SHAKE_SENSITIVITY) {
                showCard(cardTv,imageName);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
}
