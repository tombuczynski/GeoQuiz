package com.bignerdranch.android.geoquiz.ui;

import androidx.lifecycle.ViewModel;

/**
 * Created by Tom Buczynski on 28.12.2021.
 */
public class HintViewModel extends ViewModel {
    private boolean mHintUsed;

    public boolean isHintUsed() {
        return mHintUsed;
    }

    public void setHintUsed(boolean hintUsed) {
        mHintUsed = hintUsed;
    }
}
