package com.bpr.pecka.dialog;


import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.bpr.pecka.R;

public class ConfirmationDialog {


    public static void show(Context context, ConfirmationDialogListener listener, String message, String title) {
        String positiveText = context.getString(R.string.positive_text);
        String negativeText = context.getString(R.string.negative_text);

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, (dialog, which) -> listener.onConfirmed())
                .setNegativeButton(negativeText, null)
                .show();
    }
}
