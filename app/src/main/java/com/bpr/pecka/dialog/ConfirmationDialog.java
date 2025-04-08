package com.bpr.pecka.dialog;


import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class ConfirmationDialog {


    public static void show(Context context, String message, ConfirmationDialogListener listener) {
        new AlertDialog.Builder(context)
                .setTitle("Confirmation")
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> {
                    listener.onConfirmed();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
