package org.sfsteam.easyscrum;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by warmount on 15.06.13.
 */
public class CardActivity extends Activity {
    RelativeLayout root;
    TextView cardTv;
    int cardState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.root = (RelativeLayout) this.getLayoutInflater().inflate(R.layout.card_layout, null);
        this.setContentView(root);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        Bundle b = getIntent().getExtras();
        String cardValue = b.getString("card");

        cardTv = (TextView) root.findViewById(R.id.card_num);
        switch (cardValue.length()){
            case 1:
                ;
            case 2:
                cardTv.setTextSize(250);
                break;
            case 3:
                cardTv.setTextSize(200);
                break;
        }
        cardTv.setText(cardValue);
        cardTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void showCard(View v){
        LinearLayout startLay = (LinearLayout) root.findViewById(R.id.start);
        startLay.setVisibility(View.GONE);
        cardTv.setVisibility(View.VISIBLE);
        cardState = 1;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if (cardState == 1){
            outState.putBoolean("open",true);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean("open")) {
            LinearLayout startLay = (LinearLayout) root.findViewById(R.id.start);
            startLay.setVisibility(View.GONE);
            TextView cardTv = (TextView) root.findViewById(R.id.card_num);
            cardTv.setVisibility(View.VISIBLE);
            cardState = 1;
        }
    }
}
