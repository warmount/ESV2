package org.sfsteam.easyscrum;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import org.sfsteam.easyscrum.data.DeckDT;
import org.sfsteam.easyscrum.data.DialogMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, DeckDialog.DeckDialogListener {

    public static final String POWER_OF_2 = "1,2,4,8,16";
    public static final String FIBONACCI = "1,2,3,5,8,13";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private static final String SUSPEND_FILE = "easyScrum.lst";

    private List<DeckDT> deckList;
    private DeckDT deckInGrid;
    private boolean initial = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setDeckList(loadDeckList());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    public List<DeckDT> loadDeckList() {
        final File cache_dir = this.getCacheDir();
        final File suspend_f = new File(cache_dir.getAbsoluteFile() + File.separator + SUSPEND_FILE);
        List<DeckDT> listStr = (List<DeckDT>) loadSerializedList(suspend_f);
        if (listStr == null) {
            listStr = new ArrayList<>();
            listStr.add(new DeckDT(0, getString(R.string.preplan), POWER_OF_2));
            listStr.add(new DeckDT(1, getString(R.string.plan), FIBONACCI));
        }
        return listStr;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
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
        mTitle = getTitle();
        invalidateOptionsMenu();
    }

    public void onSectionAttached(int deckId) {
        Map<Integer,DeckDT> deckMap = new HashMap<>();
        for (DeckDT d : deckList) {
            deckMap.put(d.getId(), d);
        }
        deckInGrid = deckMap.get(deckId);
        initial = false;
        mTitle = deckInGrid.getName();
        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.setDeckInGrid(deckInGrid);
            invalidateOptionsMenu();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mNavigationDrawerFragment!=null && !mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(initial ? R.menu.global : R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.add_deck) {

            DeckDialog newFragment = new DeckDialog();
            //is it necessary?
            newFragment.setMode(DialogMode.ADD);
            newFragment.show(getSupportFragmentManager(), "deckDialog");

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
        startActivity(intent);
    }

    public Object loadSerializedList(File f) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            Object o = ois.readObject();
            return o;
        } catch (Exception ex) {
            Log.e("EasyScrum", "Serialization Read Error", ex);
        }
        return null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (deckInGrid != null) {
            outState.putString("deck", deckInGrid.getDeckString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String deck = savedInstanceState.getString("deck");
        if (deck != null) {
            onNavigationDrawerItemSelected(deckList.indexOf(deck));
        }
    }

    public List<DeckDT> getDeckList() {
        return deckList;
    }

    public void setDeckList(List<DeckDT> deckList) {
        this.deckList = deckList;
    }

    private void saveList() {
        final File cache_dir = this.getCacheDir();
        final File suspend_f = new File(cache_dir.getAbsoluteFile() + File.separator + SUSPEND_FILE);

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        boolean keep = true;

        try {
            fos = new FileOutputStream(suspend_f);
            oos = new ObjectOutputStream(fos);

            oos.writeObject(this.deckList);
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
    public void onPause() {
        super.onPause();
        saveList();
    }

    static class DeckArrayAdapter extends ArrayAdapter<String> {
        String[] cards;
        MainActivity activity;
        public DeckArrayAdapter(MainActivity activity, int grid_item, int tvText, String[] data) {
            super(activity.getApplicationContext(), grid_item,tvText,data);
            this.cards = data;
            this.activity = activity;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final View grid = activity.getLayoutInflater().inflate(R.layout.grid_item, parent, false);
            final TextView cardTv = (TextView) grid.findViewById(R.id.tvText);
            final String cardText = cards[position];
            if (cardText.length()<5){
                cardTv.setTextSize(50);
            }

            cardTv.setText(cardText);
            cardTv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return  false;
                }
            });
            cardTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.startCardActivity(cardText);
                }
            });

            return grid;
        }
    }
}
