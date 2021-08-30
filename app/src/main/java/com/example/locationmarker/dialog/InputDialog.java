package com.example.locationmarker.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.widget.EditText;

public class InputDialog {
    private static final String LOG_TAG = InputDialog.class.getSimpleName();
    private static final InputDialog INSTANCE = new InputDialog();
    private static String alertDialogInputText;
    private static Context context;

    private static OnDialogTextInputListener onDialogTextInputListener;

    public interface OnDialogTextInputListener {
        void onDialogTextInput(int pos, String text);
    }

    /**
     * Register listener for the OnDialogTextInputListener.
     *
     * @param listener listeners to register.
     */
    public void setOnDialogTextInputListener(InputDialog.OnDialogTextInputListener listener) {
        onDialogTextInputListener = listener;
    }

    /**
     * Gets a InputDialog using the defaults.
     *
     * @return unique instance of InputDialog.
     */
    public static InputDialog getInstance() {
        return INSTANCE;
    }

    private InputDialog() {
    }

    /**
     * Sets the value of the private context field to the specified.
     *
     * @param context specified context value.
     */
    public void setContext(Context context) {
        this.context = context;
    }

    private static void userInput(final Runnable func) {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);

        aBuilder.setTitle("Give a name for area:");
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        aBuilder.setView(input);

        aBuilder.setPositiveButton("Ok", (dialog, which) -> {
            alertDialogInputText = input.getText().toString();
            func.run();
        });
        aBuilder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            alertDialogInputText = "";
        });
        aBuilder.show();
    }

    /**
     * Starts alert dialog to set name of the area in Locations view.
     *
     * @param itemPosition position of the item.
     */
    public static void startAlertDialog(final int itemPosition) {
        Runnable alertDialogRunnable = () -> {
            if (!alertDialogInputText.isEmpty()) {
                onDialogTextInputListener.onDialogTextInput(itemPosition, alertDialogInputText);
            }
        };
        userInput(alertDialogRunnable);
    }
}
