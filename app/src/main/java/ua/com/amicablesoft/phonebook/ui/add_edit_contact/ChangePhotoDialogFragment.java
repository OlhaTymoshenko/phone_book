package ua.com.amicablesoft.phonebook.ui.add_edit_contact;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import ua.com.amicablesoft.phonebook.R;

/**
 * Created by lapa on 06.10.16.
 */

public class ChangePhotoDialogFragment extends DialogFragment {
    ChangePhotoDialogListener changePhotoDialogListener;

    public interface ChangePhotoDialogListener {
        void onTakePhotoClick();
        void onChoosePhotoClick();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            changePhotoDialogListener = (ChangePhotoDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DeleteContactDialogListener");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_change_photo_dialog)
                .setItems(R.array.photo_actions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            changePhotoDialogListener.onTakePhotoClick();
                        } else if (which == 1) {
                            changePhotoDialogListener.onChoosePhotoClick();
                        }
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismissAllowingStateLoss();
                    }
                });
        return builder.create();
    }
}

