package com.bpr.pecka.controls;

public interface IPrecisionIconVisible {

    /**
     * Called when a precision icon visibility is changed.
     * This callback will be run on the main thread.
     *
     * @param visibility determines whether visibility was switched on/off.
     */
    void onPrecisionIconVisibleChange(boolean visibility);
}
