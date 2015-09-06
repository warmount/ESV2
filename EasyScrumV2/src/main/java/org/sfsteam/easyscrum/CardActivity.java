package org.sfsteam.easyscrum;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by warmount on 15.06.13.
 */
public class CardActivity extends Activity {
    RelativeLayout root;
    TextView cardTv;
    String imageName;
    ImageView cardImage;
    boolean cardOpen;
    boolean isImage;

    private int shakeSensitivity;

    private SensorManager sensorManager;
    private float accel = SensorManager.GRAVITY_EARTH * 2.0f;
    private float accelPrevious = SensorManager.GRAVITY_EARTH * 2.0f;
    private boolean isAnimated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.root = (RelativeLayout) this.getLayoutInflater().inflate(R.layout.card_layout, null);
        this.setContentView(root);
        setCardSystemView();
        final String cardValue = getIntent().getStringExtra("card");
        imageName = getIntent().getStringExtra("image");
        setImageAnimatedOrNot();

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

        final SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        int sensitivityInt = settings.getInt(SettingsActivity.SENSITIVITY,15);
        int accelerationInt = settings.getInt(SettingsActivity.ACCELERATION,2);

        shakeSensitivity = sensitivityInt;
        accel = SensorManager.GRAVITY_EARTH * accelerationInt;
        accelPrevious = SensorManager.GRAVITY_EARTH * accelerationInt;

        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(
                sensorListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void setImageAnimatedOrNot() {
        if (imageName == null) {
            return;
        }
        if (imageName.toUpperCase().endsWith(".GIF")) {
            setWebViewFromBitmapParam(imageName);
            return;
        }
        cardImage = (ImageView) root.findViewById(R.id.cardImage);
        cardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cardImage.setImageURI(Uri.parse(imageName));

    }

    private void setWebViewFromBitmapParam(String imageName) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageName, options);
        String cssString = "width:100%;";

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        if (Float.valueOf(options.outHeight)/Float.valueOf(options.outWidth) >
                Float.valueOf(size.y)/Float.valueOf(size.x)){
            cssString = "height:100%;";
        }
        setWebImage(imageName, cssString, (WebView) root.findViewById(R.id.webImage));
    }

    private void setWebImage(String imageName, String cssString, WebView webView) {
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                finish();
                return false;
            }
        });
        String htmlText = "<html><body><div align='center'>" +
                "<img style=\"margin: auto; position: absolute;" +
                "  top: 0; left: 0; bottom: 0; right: 0;" +
                cssString + "\" src=\"file://" +imageName+"\"></div></body></html>";
        webView.loadDataWithBaseURL("", htmlText, "text/html","utf-8", "");
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
        cardOpen = true;
        if (imageName == null) {
            cardTv.setVisibility(View.VISIBLE);
            return;
        }
        isImage = true;
        if (imageName.toUpperCase().endsWith(".GIF")){
            WebView webImage = (WebView) root.findViewById(R.id.webImage);
            webImage.setVisibility(View.VISIBLE);
            isAnimated = true;
            return;
        }
        cardImage.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (cardOpen) {
            outState.putBoolean("open", true);
        }
        if (isImage) {
            outState.putBoolean("image", true);
        }
        if (isAnimated) {
            outState.putBoolean("animated", true);
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
        cardOpen = true;
        if (!savedInstanceState.getBoolean("image")) {
            TextView cardTv = (TextView) root.findViewById(R.id.card_num);
            cardTv.setVisibility(View.VISIBLE);
            return;
        }
        isImage = true;
        if (!savedInstanceState.getBoolean("animated")) {
            ImageView cardImage = (ImageView) root.findViewById(R.id.cardImage);
            cardImage.setVisibility(View.VISIBLE);
            return;
        }
        isAnimated = true;
        WebView webImage = (WebView) root.findViewById(R.id.webImage);
        webImage.setVisibility(View.VISIBLE);
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
            if (accel - accelPrevious > shakeSensitivity) {
                showCard(cardTv,imageName);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };
}
