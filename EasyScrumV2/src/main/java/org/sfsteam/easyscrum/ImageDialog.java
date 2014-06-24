package org.sfsteam.easyscrum;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.sfsteam.easyscrum.data.ImageDT;

import java.util.HashMap;

/**
 * Created by warmount on 15.04.2014.
 */
public class ImageDialog extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ImageDialogListener {
        public void onDialogPositiveClick(String oldKey, String name, String path);
        public Context getApplicationContext();

    }

    // Use this instance of the interface to deliver action events
    ImageDialogListener mListener;
    String aliasName;
    String path;
    TextView pathTV;
    TextView aliasTV;

    // Override the Fragment.onAttach() method to instantiate the DialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the DialogListener so we can send events to the host
            mListener = (ImageDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ImageDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final HashMap<String, ImageDT> imageMap = ((ImageActivity) getActivity()).getImagesMap();
        final String editAlias = ((ImageActivity) getActivity()).getEditAlias();
        View dialogView = inflater.inflate(R.layout.image_dialog, null);
        aliasTV = (TextView) dialogView.findViewById(R.id.alias_name);
        pathTV = (TextView) dialogView.findViewById(R.id.alias_path);
        if (editAlias != null) {
            aliasTV.setText(editAlias);
            pathTV.setText(imageMap.get(editAlias).getPath());
        }
        builder.setView(dialogView);
        Button cancelButton = (Button) dialogView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ImageActivity) getActivity()).setEditAlias(null);
                dismiss();
            }
        });

        Button okButton = (Button) dialogView.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String alias = aliasTV.getText().toString();
                String path1 = pathTV.getText().toString();
                ImageDT dt = new ImageDT(alias, path1);
                if (dt.equals(imageMap.get(alias))){
                    showToastTop(getActivity(),R.string.same_image);
                    return;
                }

                mListener.onDialogPositiveClick(editAlias, alias, path1);
                ((ImageActivity) getActivity()).setEditAlias(null);
                dismiss();
            }
        });
        ImageView searchImage = (ImageView) dialogView.findViewById(R.id.search_button);
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), FileChooser.class);
                startActivityForResult(intent, 2);
            }
        });

        // Create the AlertDialog object and return it
        return builder.show();
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (2): {
                if (resultCode == Activity.RESULT_OK) {
                    TextView pathTV = getPathTV();

                    pathTV.setText(data.getStringExtra("path"));
                    setPathTV(pathTV);
                    TextView tv2 = getAliasTV();

                    String alias = data.getStringExtra("alias");
                    tv2.setText(alias.substring(0, alias.indexOf(".")));
                    setAliasTV(tv2);
                }
                break;
            }
        }
    }

    private void showToastTop(Activity activity, int string) {
        Toast toast = Toast.makeText(activity, string, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    public TextView getPathTV() {
        return pathTV;
    }

    public void setPathTV(TextView pathTV) {
        this.pathTV = pathTV;
    }

    public TextView getAliasTV() {
        return aliasTV;
    }

    public void setAliasTV(TextView aliasTV) {
        this.aliasTV = aliasTV;
    }
}
