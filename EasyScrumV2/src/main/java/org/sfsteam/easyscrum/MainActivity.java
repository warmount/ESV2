package org.sfsteam.easyscrum;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import org.sfsteam.easyscrum.data.DeckArrayAdapter;
import org.sfsteam.easyscrum.data.DeckDT;
import org.sfsteam.easyscrum.data.DialogMode;
import org.sfsteam.easyscrum.data.ImageDT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ActionBarActivity
implements NavigationDrawerFragment.NavigationDrawerCallbacks, DeckDialog.DeckDialogListener, DeckArrayAdapter.CardActivityCallback,DeleteDialog.DeleteDialogListener {

    private static final String POWER_OF_2 = "1,2,4,8,16,@cup";
    private static final String FIBONACCI = "1,2,3,5,8,13,@cup";
    private static final String EASY_SCRUM_LST = "easyScrum.lst";
    private static final String CUP_NAME = "cup.png";
    public static final String PREFS_NAME = "EasyScrumPrefs";
    public static final String DEFAULT_DECK = "default_deck";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    public static final String IMAGES_MAP = "images.map";

    private List<DeckDT> deckList;
    private DeckDT deckInGrid;
    private HashMap<String, ImageDT> imagesMap;
    private boolean initial = true;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setDeckList(loadDeckList());
        setImagesMap(loadImagesMap());
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int prefDeckId = settings.getInt(DEFAULT_DECK,-1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        if (prefDeckId != -1 && !deckList.isEmpty()){
            int index = getIndexById(getDeckList(), prefDeckId);
            onNavigationDrawerItemSelected(index);
            setTitleInToolbar(getDeckList().get(index).getName());
        }
    }

    public List<DeckDT> loadDeckList() {
        String path = Environment.getExternalStorageDirectory() + "/easyScrum";
        final File suspend_f = new File(path + File.separator + EASY_SCRUM_LST);
        List<DeckDT> listStr = (List<DeckDT>) loadSerializedFile(suspend_f);
        if (listStr != null) {
            return listStr;
        }
        listStr = new ArrayList<>();
        listStr.add(new DeckDT(0, getString(R.string.preplan), POWER_OF_2));
        listStr.add(new DeckDT(1, getString(R.string.plan), FIBONACCI));
        Intent transparent = new Intent(MainActivity.this, TransparentActivity.class);
        startActivity(transparent);

        return listStr;
    }

    public HashMap<String, ImageDT> loadImagesMap() {
        String path = Environment.getExternalStorageDirectory() + "/easyScrum";
        final File imagesFile = new File(path + File.separator + IMAGES_MAP);
        HashMap<String, ImageDT> imagesMap = (HashMap<String, ImageDT>) loadSerializedFile(imagesFile);
        //new install
        if (imagesMap == null) {
            imagesMap = new HashMap<>();
            imagesMap.put("cup",getNewInstallCup(path));
            return imagesMap;
        }
        //ignore if no cup in map
        if (!imagesMap.containsKey("cup")){
            return imagesMap;
        }

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        //replace with cup
        if (!settings.getBoolean("new_cup",false)){
            ImageDT cupDT = imagesMap.get("cup");
            (new File(cupDT.getPath())).delete();
            (new File(cupDT.getThumbnailPath())).delete();
            imagesMap.put("cup",getNewInstallCup(path));

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("new_cup", true);
            //never replace with new cup
            editor.commit();
        }
        return imagesMap;
    }

    private ImageDT getNewInstallCup(String path) {
        String imagePath = path + File.separator + "images/";
        String thumbPath = path + File.separator + ".thumbnails/";
        File imagesDir = new File(imagePath);
        imagesDir.mkdirs();
        imagesDir = new File(thumbPath);
        imagesDir.mkdirs();
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.cup);
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bm, 100, 150);
        ImageDT cup = new ImageDT("cup", imagePath + CUP_NAME);
        File file = new File(imagePath, CUP_NAME);
        File thumFile = new File(thumbPath, cup.getThumbnailName());
        try {
            FileOutputStream outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream = new FileOutputStream(thumFile);
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            Log.e("File not found", e.getLocalizedMessage());
        } catch (IOException e) {
            Log.e("IO", e.getLocalizedMessage());
        }
        return cup;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (position != -1) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(deckList.get(position)))
                    .commit();
        }
    }

    @Override
    public void setTextInGrid() {

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance())
                .commit();
        initial = true;
        invalidateOptionsMenu();
    }

    @Override
    public void setTitleInToolbar(String string) {
        getSupportActionBar().setTitle(string);
    }

    public void onSectionAttached(int deckId) {
        Map<Integer, DeckDT> deckMap = new HashMap<>();
        for (DeckDT d : deckList) {
            deckMap.put(d.getId(), d);
        }
        deckInGrid = deckMap.get(deckId);
        initial = false;
        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.setDeckInGrid(deckInGrid);
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mNavigationDrawerFragment != null && !mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(initial ? R.menu.global : R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_deck) {

            DeckDialog newFragment = new DeckDialog();
            //is it necessary? set this for
            newFragment.setMode(DialogMode.ADD);
            newFragment.show(getSupportFragmentManager(), "deckDialog");

            return true;
        }
        if (id == R.id.image_activity) {
            Intent intent = new Intent(MainActivity.this, ImageActivity.class);
            intent.putExtra("imageMap", getImagesMap());
            startActivityForResult(intent, 1);
            return true;
        }
        if (id == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(intent, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        DeckDT deck = ((DeckDialog) dialog).getDeck();
        Map<Integer, DeckDT> deckMap = new HashMap<>();
        for (DeckDT d : deckList) {
            deckMap.put(d.getId(), d);
        }
        //renew decks
        deckMap.put(deck.getId(), deck);

        deckList = new ArrayList<>();
        for (Map.Entry<Integer, DeckDT> entry : deckMap.entrySet()) {
            deckList.add(entry.getValue());
        }

        mNavigationDrawerFragment.setNewList();
        onNavigationDrawerItemSelected(deckList.indexOf(deck));
        setTitleInToolbar(deck.getName());
    }

    @Override
    public void onDeleteDialogPositiveClick(DialogFragment dialog) {
        mNavigationDrawerFragment.onDeleteDialogPositiveClick();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String DECKASARRAY = "deck_array";
        private static final String DECKID = "deck_id";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(DeckDT deck) {

            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putStringArray(DECKASARRAY, deck.getDeckAsArray());
            args.putInt(DECKID, deck.getId());

            fragment.setArguments(args);
            return fragment;
        }

        public static PlaceholderFragment newInstance() {

            PlaceholderFragment fragment = new PlaceholderFragment();
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            if (getArguments() != null) {
                GridView gv = (GridView) rootView.findViewById(R.id.gridView);
                DeckArrayAdapter adapter = new DeckArrayAdapter((MainActivity) getActivity(),
                        R.layout.grid_item,
                        R.id.tvText,
                        getArguments().getStringArray(DECKASARRAY)
                );
                TextView tv = (TextView) rootView.findViewById(R.id.mockText);
                tv.setVisibility(View.GONE);
                gv.setAdapter(adapter);
                gv.setVisibility(View.VISIBLE);
            } else {
                TextView tv = (TextView) rootView.findViewById(R.id.mockText);
                tv.setVisibility(View.VISIBLE);
            }
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            if (getArguments() != null) {
                ((MainActivity) activity).onSectionAttached(getArguments().getInt(DECKID));
            }
        }
    }

    public void startCardActivity(String value) {
        Intent intent = new Intent(MainActivity.this, CardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("card", value);
        startActivity(putImagePathIfExist(value, intent));
    }

    private Intent putImagePathIfExist(String value, Intent intent) {
        if (!value.startsWith("@")) {
            return intent;
        }
        ImageDT imageDT = imagesMap.get(value.substring(1, value.length()));
        if (imageDT == null) {
            return intent;
        }
        intent.putExtra("image", imageDT.getPath());
        return intent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (deckInGrid != null) {
            outState.putString("deck", deckInGrid.getDeckString());
            outState.putString("deck_name", deckInGrid.getName());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String deck = savedInstanceState.getString("deck");
        if (deck != null) {
            onNavigationDrawerItemSelected(deckList.indexOf(deck));
            setTitleInToolbar(savedInstanceState.getString("deck_name"));
        }
    }

    public List<DeckDT> getDeckList() {
        return deckList;
    }

    public void setDeckList(List<DeckDT> deckList) {
        this.deckList = deckList;
    }

    public HashMap<String, ImageDT> getImagesMap() {
        return imagesMap;
    }

    public void setImagesMap(HashMap<String, ImageDT> imagesMap) {
        this.imagesMap = imagesMap;
    }

    @Override
    public void onPause() {
        super.onPause();
        saveFile(getDeckList(), EASY_SCRUM_LST);
        saveFile(getImagesMap(), IMAGES_MAP);
    }

    public static Object loadSerializedFile(File f) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            Object o = ois.readObject();
            return o;
        } catch (Exception ex) {
            Log.e("EasyScrum", "Serialization Read Error", ex);
        }
        return null;
    }

    public static void saveFile(Object saveObject, String fileName) {
        String path = Environment.getExternalStorageDirectory() + "/easyScrum";
        final File suspend_f = new File(path + File.separator + fileName);

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        boolean keep = true;

        try {
            fos = new FileOutputStream(suspend_f);
            oos = new ObjectOutputStream(fos);

            oos.writeObject(saveObject);
        } catch (Exception e) {
            keep = false;
            Log.e("EasyScrum", "failed to suspend", e);
        } finally {
            try {
                if (oos != null) oos.close();
                if (fos != null) fos.close();
                if (keep == false) suspend_f.delete();
            } catch (Exception e) {
                Log.e("EasyScrum", "failed to close", e);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (1): {
                if (resultCode == Activity.RESULT_OK) {
                    setImagesMap((HashMap<String, ImageDT>) data.getSerializableExtra("imageMap"));
                    mNavigationDrawerFragment.setNewList();
                    onNavigationDrawerItemSelected(deckList.indexOf(deckInGrid));
                }
                break;
            }
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if (deckInGrid == null){
            return;
        }
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(DEFAULT_DECK, deckInGrid.getId());

        // Commit the edits!
        editor.commit();
    }

    private int getIndexById(List<DeckDT> list,int id){
        for (DeckDT dt: list){
            if (id == dt.getId()){
                return list.indexOf(dt);
            }
        }
        return 0;
    }
}