package com.example.locationmarker.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

import com.example.locationmarker.surface.SurfaceManager;

public class InputDialog {
    private static final String LOG_TAG = InputDialog.class.getSimpleName();
    private static final InputDialog INSTANCE = new InputDialog();
    private static String alertDialogInputText;
    private static Context context;

    private static OnDialogTextInputListener onDialogTextInputListener;

    public interface OnDialogTextInputListener {
        void onDialogTextInput(int pos, String text);
    }

    public void setOnDialogTextInputListener(InputDialog.OnDialogTextInputListener listener) {
        onDialogTextInputListener = listener;
    }

    public static InputDialog getInstance() {
        return INSTANCE;
    }

    private InputDialog() {
    }

    public void setContext(Context c) {
        context = c;
    }

    private static void userInput(final Runnable func) {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);

        aBuilder.setTitle("Give a name for area:");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        aBuilder.setView(input);

        aBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogInputText = input.getText().toString();
                func.run();
            }
        });
        aBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                alertDialogInputText = "";
            }
        });
        aBuilder.show();
    }

    public static void startAlertDialog(final int itemPosition) {
        Runnable alertDialogRunnable = new Runnable() {
            @Override
            public void run() {
                if (!alertDialogInputText.isEmpty()) {
                    onDialogTextInputListener.onDialogTextInput(itemPosition, alertDialogInputText);
                }
            }
        };
        userInput(alertDialogRunnable);
    }
}
