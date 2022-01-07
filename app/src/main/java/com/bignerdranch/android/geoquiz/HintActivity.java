package com.bignerdranch.android.geoquiz;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.bignerdranch.android.geoquiz.ui.HintViewModel;

public class HintActivity extends AppCompatActivity {
    private static final String TAG = "HintActivity";

    public static final String EXTRA_ANSWER_IS_TRUE = "ANSWER_IS_TRUE";
    public static final String EXTRA_ALREADY_ANSWERED = "ALREADY_ANSWERED";
    public static final String EXTRA_HINT_USED = "HINT_USED";
    public static final String EXTRA_HINTS_LEFT_CNT = "HINTS_LEFT_CNT";

    private boolean mAnswerIsTrue, mAlreadyAnswered;
    private int mHintsLeftCnt;

    private TextView mTextViewAnswer, mTextViewHintsLeft;

    private HintViewModel mViewModel;
    public Button mButtonShowAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hint);

        mViewModel = new ViewModelProvider(this).get(HintViewModel.class);

        mTextViewAnswer = findViewById(R.id.textview_answer);
        mTextViewHintsLeft = findViewById(R.id.textview_hints_left);

        getArgs();
        if (mAlreadyAnswered || mViewModel.isHintUsed()) {
            showHint();
        }

        if (mAlreadyAnswered) {
            findViewById(R.id.textview_warning).setVisibility(View.INVISIBLE);
            mTextViewHintsLeft.setVisibility(View.INVISIBLE);
        }

        buttonsHandler();

        updateHintsLeftCnt();
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key. The {@link #getOnBackPressedDispatcher() OnBackPressedDispatcher} will be given a
     * chance to handle the back button before the default behavior of
     * {@link Activity#onBackPressed()} is invoked.
     *
     * @see #getOnBackPressedDispatcher()
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");

        setResult(mViewModel.isHintUsed() && ! mAlreadyAnswered);

        super.onBackPressed();
    }

    private void buttonsHandler() {
        mButtonShowAnswer = findViewById(R.id.button_show_answer);

        if (mAlreadyAnswered || mViewModel.isHintUsed()) {
            mButtonShowAnswer.setVisibility(View.INVISIBLE);
        }
        else
            mButtonShowAnswer.setOnClickListener(v -> {
                showHint();

                updateHintsLeftCnt();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    animatedHide(mButtonShowAnswer);
                else
                    mButtonShowAnswer.setVisibility(View.INVISIBLE);
            });
    }

    private void getArgs() {
        Intent intent = getIntent();

        mAnswerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mAlreadyAnswered = intent.getBooleanExtra(EXTRA_ALREADY_ANSWERED, false);

        mHintsLeftCnt = intent.getIntExtra(EXTRA_HINTS_LEFT_CNT, 0);
    }

    private void setResult(boolean hintUsed) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_HINT_USED, hintUsed);
        setResult(RESULT_OK, intent);
    }

    private void showHint() {
        String anwser = getString(R.string.hint_answer) +
                (mAnswerIsTrue ? getString(R.string.answer_true_button) : getString(R.string.answer_false_button));

        mViewModel.setHintUsed(true);

        mTextViewAnswer.setText(anwser);
    }

    private void updateHintsLeftCnt() {
        int hintsLeftCnt = mViewModel.isHintUsed() ? mHintsLeftCnt - 1: mHintsLeftCnt;
        if (hintsLeftCnt < 0)
            hintsLeftCnt = 0;

        mTextViewHintsLeft.setText(getResources().getQuantityString(R.plurals.hints_left, hintsLeftCnt, hintsLeftCnt));

        if (hintsLeftCnt == 0)
            mButtonShowAnswer.setEnabled(false);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void animatedHide(View v) {
        // get the center for the clipping circle
        int cx = v.getWidth() / 2;
        int cy = v.getHeight() / 2;

        // get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);

        // create the animation (the final radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, initialRadius, 0f);
        anim.setDuration(800);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                v.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }
}