package org.sfsteam.easyscrum.data;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.sfsteam.easyscrum.MainActivity;
import org.sfsteam.easyscrum.R;

import java.util.HashMap;

/**
 * Created by warmount on 13.04.2014.
 */
public class DeckArrayAdapter extends ArrayAdapter<String> {
    String[] cards;
    CardActivityCallback callback;

    public DeckArrayAdapter(MainActivity activity, int grid_item, int tvText, String[] data) {
        super(activity.getApplicationContext(), grid_item, tvText, data);
        this.cards = data;
        this.callback = activity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View grid = callback.getLayoutInflater().inflate(R.layout.grid_item, parent, false);
        final String cardText = cards[position];
        ImageDT imageDT = null;
        if (cardText.startsWith("@")) {
            imageDT = callback.getImagesMap().get(cardText.substring(1, cardText.length()));
        }
        if (imageDT != null) {
            ImageView cardImage = (ImageView) grid.findViewById(R.id.card_image);
            cardImage.setImageURI(Uri.parse(imageDT.getThumbnailPath()));
            cardImage.setVisibility(View.VISIBLE);
            cardImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.startCardActivity(cardText);
                }
            });
        } else {
            TextView cardTv = (TextView) grid.findViewById(R.id.tvText);
            if (cardText.length() < 5) {
                cardTv.setTextSize(50);
            }

            cardTv.setText(cardText);
            cardTv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return false;
                }
            });
            cardTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.startCardActivity(cardText);
                }
            });
            cardTv.setVisibility(View.VISIBLE);
        }
        return grid;
    }

    public interface CardActivityCallback {
        void startCardActivity(String cardText);

        LayoutInflater getLayoutInflater();

        HashMap<String, ImageDT> getImagesMap();
    }
}