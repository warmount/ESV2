package org.sfsteam.easyscrum.data;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.sfsteam.easyscrum.R;

import java.util.List;
import java.util.Map;

/**
 * Created by warmount on 13.04.2014.
 */
public class ImagesMapAdapter extends ArrayAdapter<String> {
    Map<String, ImageDT> imagesMap;
    ImageActivityCallback callback;
    List<String> mapList;

    public ImagesMapAdapter(ImageActivityCallback callback, int listItm, Map<String, ImageDT> data, List<String> mapList) {
        super(callback.getApplicationContext(), listItm, mapList);
        this.imagesMap = data;
        this.callback = callback;
        this.mapList = mapList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View listItem = callback.getLayoutInflater().inflate(R.layout.list_item, parent, false);
        final TextView aliasNameTV = (TextView) listItem.findViewById(R.id.aliasName);
        ImageView imageView = (ImageView) listItem.findViewById(R.id.imageView);
        final String alias = mapList.get(position);
        imageView.setImageURI(Uri.parse(imagesMap.get(alias).getThumbnailPath()));
        aliasNameTV.setText(alias);
        aliasNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.startEditDialod(alias);
            }
        });

        ImageView deleteButton = (ImageView) listItem.findViewById(R.id.imageButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.deleteFromMap(alias);
            }
        });

        return listItem;
    }

    public interface ImageActivityCallback {
        void startEditDialod(String aliasName);

        LayoutInflater getLayoutInflater();

        Context getApplicationContext();

        void deleteFromMap(String aliasName);
    }
}