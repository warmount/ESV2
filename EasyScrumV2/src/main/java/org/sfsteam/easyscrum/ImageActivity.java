package org.sfsteam.easyscrum;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.sfsteam.easyscrum.data.ImageDT;
import org.sfsteam.easyscrum.data.ImagesMapAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.sfsteam.easyscrum.MainActivity.IMAGES_MAP;
import static org.sfsteam.easyscrum.MainActivity.saveFile;


public class ImageActivity extends ActionBarActivity implements ImageDialog.ImageDialogListener,
        ImagesMapAdapter.ImageActivityCallback, DeleteDialog.DeleteDialogListener {

    private HashMap<String, ImageDT> imagesMap;
    private String editAlias;
    private ImagesMapAdapter adapter;
    private String aliasName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        setImagesMap((HashMap<String, ImageDT>) getIntent().getSerializableExtra("imageMap"));
        ListView imagesList = (ListView) findViewById(R.id.listView);
        List<String> imagesListMap = getListImages();
        adapter = new ImagesMapAdapter(this, R.layout.list_item, getImagesMap(), imagesListMap);
        imagesList.setAdapter(adapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.add_image) {
            ImageDialog newFragment = new ImageDialog();
            newFragment.show(getSupportFragmentManager(), "imageDialog");

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public HashMap<String, ImageDT> getImagesMap() {
        return imagesMap;
    }

    public void setImagesMap(HashMap<String, ImageDT> imagesMap) {
        this.imagesMap = imagesMap;
    }

    public String getEditAlias() {
        return editAlias;
    }

    public void setEditAlias(String editAlias) {
        this.editAlias = editAlias;
    }

    @Override
    public void onPause() {
        super.onPause();
        saveFile(getImagesMap(), IMAGES_MAP);
    }

    @Override
    public void startEditDialod(String aliasName) {
        ImageDialog newFragment = new ImageDialog();
        setEditAlias(aliasName);
        newFragment.setAliasName(aliasName);
        newFragment.setPath(getImagesMap().get(aliasName).getPath());
        newFragment.show(getSupportFragmentManager(), "imageDialog");
    }

    @Override
    public void deleteFromMap(String aliasName) {
        this.aliasName = aliasName;
        DeleteDialog ddialog = new DeleteDialog();
        ddialog.show(getSupportFragmentManager(), "deleteDialog");
    }

    private void renewAdapter() {
        List<String> imagesListMap = getListImages();
        adapter.clear();
        adapter.addAll(imagesListMap);
        adapter.notifyDataSetChanged();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("imageMap", getImagesMap());
        setResult(Activity.RESULT_OK, resultIntent);
    }

    @Override
    public void onDialogPositiveClick(String oldKey, String name, String path) {
        if (oldKey != null) {
            getImagesMap().remove(oldKey);
        }
        ImageDT image = new ImageDT(name, path);
        getImagesMap().put(name, image);
        saveThumbnail(image);
        renewAdapter();
    }

    private void saveThumbnail(ImageDT image) {
        Bitmap bm = BitmapFactory.decodeFile(image.getPath());
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bm, 100, 150);
        File thumFile = new File(image.getThumbnailPath());
        try {
            FileOutputStream outStream = new FileOutputStream(thumFile);
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            Log.e("File not found", e.getLocalizedMessage());
        } catch (IOException e) {
            Log.e("IO", e.getLocalizedMessage());
        }

    }

    private List<String> getListImages() {
        List<String> imagesListMap = new ArrayList<>();
        for (Map.Entry<String, ImageDT> imageAlias : getImagesMap().entrySet()) {
            imagesListMap.add(imageAlias.getKey());
        }
        return imagesListMap;
    }


    @Override
    public void onDeleteDialogPositiveClick(DialogFragment dialog) {
        getImagesMap().remove(aliasName);
        renewAdapter();
        Toast.makeText(getApplicationContext(), String.format(getString(R.string.delete_from_map), aliasName), Toast.LENGTH_SHORT).show();
    }
}