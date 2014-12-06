package org.sfsteam.easyscrum;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.sfsteam.easyscrum.data.DeckDT;
import org.sfsteam.easyscrum.data.DialogMode;

import java.util.List;

/**
 * Created by warmount on 15.04.2014.
 */
public class DeckDialog extends DialogFragment {

    public static final String COMMA = ",";
    private String deckName;
    private String deckAsString;
    private DeckDT deck;
    private DialogMode mode;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface DeckDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
//        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    DeckDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the DialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the DialogListener so we can send events to the host
            mListener = (DeckDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DeckDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (mode == null) {
            mode = DialogMode.valueOf(savedInstanceState.getString("mode"));
        }

        List<DeckDT> lst = ((MainActivity) getActivity()).getDeckList();
        deck = getDeckFromSaved(savedInstanceState, lst);
        builder.setTitle(mode == DialogMode.ADD ? R.string.add_new : R.string.edit);
        View dialogView = inflater.inflate(R.layout.deck_dialog, null);
        builder.setView(dialogView);

        final EditText string = (EditText) dialogView.findViewById(R.id.deck_string);
        string.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                string.post(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() == null) {
                            return;
                        }
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(string, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        string.requestFocus();

        if (mode == DialogMode.EDIT) {
            setDeckToFields(dialogView);
        }
        Button okButton = (Button) dialogView.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setDeckFromFields(mode)) {
                    mListener.onDialogPositiveClick(DeckDialog.this);
                    dismiss();
                }
            }
        });

        Button cancelButton = (Button) dialogView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        // Create the AlertDialog object and return it
        return builder.show();
    }

    public DeckDT getDeckFromSaved(Bundle savedInstanceState, List<DeckDT> lst) {
        if (deck != null) {
            return deck;
        }
        if (savedInstanceState == null) {
            return null;
        }
        int savedDeckId = savedInstanceState.getInt("deckId");
        for (DeckDT deckDt : lst) {
            if (deckDt.getId() == savedDeckId) {
                return deckDt;
            }
        }
        return null;

    }

    private void setDeckToFields(View dialogView) {
        EditText name = (EditText) dialogView.findViewById(R.id.deck_name);
        name.setText(deck.getName());
        EditText string = (EditText) dialogView.findViewById(R.id.deck_string);
        string.setText(deck.getDeckString());
    }

    private boolean setDeckFromFields(DialogMode mode) {
        deckName = ((EditText) getDialog().findViewById(R.id.deck_name)).getText().toString();
        deckAsString = ((EditText) getDialog().findViewById(R.id.deck_string)).getText().toString();
        MainActivity activity = ((MainActivity) getActivity());

        if (deckAsString.trim().isEmpty()) {
            showToastTop(activity, R.string.empty_string);
            return false;
        }
        String[] splitArray = deckAsString.split(COMMA);
        int commaOccur = deckAsString.length() - deckAsString.replace(COMMA, "").length();
        if (splitArray.length <= commaOccur) {
            showToastTop(activity, R.string.empty_card);
            return false;
        }
        for (String card : splitArray) {
            if (card.trim().isEmpty()) {
                showToastTop(activity, R.string.empty_card);
                return false;
            }
        }

        if (deckName.trim().isEmpty()) {
            deckName = deckAsString;
        }
        List<DeckDT> deckList = activity.getDeckList();
        for (DeckDT deckForCheck : deckList) {
            if (deck != null && deckForCheck.getId() == deck.getId()) {
                continue;
            }
            if (deckForCheck.getName().equals(deckName)) {
                showToastTop(activity, R.string.have_name);
                return false;
            }
            if (deckForCheck.getDeckString().equals(deckAsString)) {
                showToastTop(activity, R.string.have_string);
                return false;
            }
        }

        switch (mode) {
            case ADD:
                int maxId = 0;
                for (DeckDT deckForCheck : deckList) {
                    if (deckForCheck.getId() > maxId) {
                        maxId = deckForCheck.getId();
                    }
                }
                deck = new DeckDT(maxId + 1, deckName.trim(), deckAsString.replaceAll(",\\s+",COMMA));
                break;
            case EDIT:
                deck.setDeckString(deckAsString.replaceAll(",\\s+",COMMA));
                deck.setName(deckName.trim());
                break;
        }

        return true;
    }

    private void showToastTop(MainActivity activity, int string) {
        Toast toast = Toast.makeText(activity, string, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    public DeckDT getDeck() {
        return deck;
    }

    public void setDeck(DeckDT deck) {
        this.deck = deck;
    }

    public void setMode(DialogMode mode) {
        this.mode = mode;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("deckName", deckName);
        outState.putString("deckString", deckAsString);
        outState.putString("mode", mode.toString());
        if (deck != null) {
            outState.putInt("deckId", deck.getId());
        }
    }
}
