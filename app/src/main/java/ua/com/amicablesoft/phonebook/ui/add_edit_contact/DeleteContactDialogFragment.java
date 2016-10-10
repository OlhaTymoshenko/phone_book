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

public class DeleteContactDialogFragment extends DialogFragment {
    DeleteContactDialogListener deleteContactDialogListener;

    public interface DeleteContactDialogListener {
        void onOKButtonClick();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            deleteContactDialogListener = (DeleteContactDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DeleteContactDialogListener");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_delete_contact)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteContactDialogListener.onOKButtonClick();
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
