package org.sfsteam.easyscrum;

/**
 * Created by warmount on 23.06.2014.
 */

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;

import org.sfsteam.easyscrum.data.FileArrayAdapter;
import org.sfsteam.easyscrum.data.Option;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileChooser extends ListActivity {

    private File currentDir;
    private FileArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDir = new File(Environment.getExternalStorageDirectory() + "/easyScrum/images/");
        fill(currentDir);
    }

    private void fill(File f) {
        File[] dirs = f.listFiles();
        this.setTitle(getString(R.string.current_folder) + f.getName());
        List<Option> dir = new ArrayList<>();
        List<Option> fls = new ArrayList<>();
        try {
            for (File ff : dirs) {

                if (ff.isDirectory()) {
                    if (ff.getName().startsWith(".")) {
                        continue;
                    }
                    dir.add(new Option(ff.getName(), getString(R.string.folder), ff.getAbsolutePath()));
                } else {
                    int dotposition = ff.getName().lastIndexOf(".");
                    String extention = ff.getName().substring(dotposition + 1, ff.getName().length());

                    if (extention.equalsIgnoreCase("png") || extention.equalsIgnoreCase("jpg") ||
                            extention.equalsIgnoreCase("jpeg") || extention.equalsIgnoreCase("gif")) {
                        fls.add(new Option(ff.getName(), getString(R.string.file_size) + ff.length(), ff.getAbsolutePath()));
                    }
                }
            }
        } catch (Exception e) {

        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if (!f.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            dir.add(0, new Option("..", getString(R.string.parent_folder), f.getParent()));
        }
        adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view, dir);
        this.setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Option o = adapter.getItem(position);
        if (o.getData().equalsIgnoreCase(getString(R.string.folder)) || o.getData().equalsIgnoreCase(getString(R.string.parent_folder))) {
            currentDir = new File(o.getPath());
            fill(currentDir);
        } else {
            onFileClick(o);
        }
    }

    private void onFileClick(Option o) {
        Intent intent = new Intent();
        intent.putExtra("path", o.getPath());
        intent.putExtra("alias", o.getName());
        setResult(RESULT_OK, intent);
        finish();
    }
}
