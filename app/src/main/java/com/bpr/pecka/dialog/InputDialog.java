package com.bpr.pecka.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.widget.EditText;

public class InputDialog {
    private static InputDialog instance;
    private String alertDialogInputText = "";
    private Context context;

    private OnDialogTextInputListener onDialogTextInputListener;

    public interface OnDialogTextInputListener {
        void onDialogTextInput(int pos, String text);
    }

    /**
     * Register listener for the OnDialogTextInputListener.
     *
     * @param listener listener to register.
     */
    public void setOnDialogTextInputListener(OnDialogTextInputListener listener) {
        this.onDialogTextInputListener = listener;
    }

    /**
     * Gets an instance of InputDialog.
     *
     * @return unique instance of InputDialog.
     */
    public static InputDialog getInstance() {
        if (instance == null) {
            instance = new InputDialog();
        }
        return instance;
    }

    private InputDialog() {
    }

    /**
     * Sets the context for the dialog.
     *
     * @param context specified context value.
     */
    public void setContext(Context context) {
        this.context = context;
    }

    private void userInput(final Runnable callback) {
        if (context == null) {
            throw new IllegalStateException("Context must be set before calling userInput()");
        }

        AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
        aBuilder.setTitle("Give a name for area:");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        aBuilder.setView(input);

        aBuilder.setPositiveButton("OK", (dialog, which) -> {
            alertDialogInputText = input.getText().toString().trim();
            if (!alertDialogInputText.isEmpty()) {
                callback.run();
            }
        });

        aBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        aBuilder.show();
    }

    /**
     * Starts an alert dialog to set the name of the area in the Locations view.
     *
     * @param itemPosition position of the item.
     */
    public void startAlertDialog(final int itemPosition) {
        Runnable alertDialogRunnable = () -> {
            if (onDialogTextInputListener != null) {
                onDialogTextInputListener.onDialogTextInput(itemPosition, alertDialogInputText);
            } else {
                throw new IllegalStateException("OnDialogTextInputListener is not set");
            }
        };
        userInput(alertDialogRunnable);
    }
}
