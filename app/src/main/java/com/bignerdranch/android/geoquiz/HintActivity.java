package com.bignerdranch.android.geoquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bignerdranch.android.geoquiz.ui.HintViewModel;

public class HintActivity extends AppCompatActivity {
    private static final String TAG = "HintActivity";

    public static final String EXTRA_ANSWER_IS_TRUE = "ANSWER_IS_TRUE";
    public static final String EXTRA_ALREADY_ANSWERED = "ALREADY_ANSWERED";
    public static final String EXTRA_HINT_USED = "HINT_USED";

    private boolean mAnswerIsTrue, mAlreadyAnswered;
    private TextView mTextViewAnswer;

    private HintViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hint);

        mViewModel = new ViewModelProvider(this).get(HintViewModel.class);

        mTextViewAnswer = findViewById(R.id.textview_answer);

        getArgs();
        if (mAlreadyAnswered || mViewModel.isHintUsed()) {
            showHint();
        }

        buttonsHandler();
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
        Button button;
        button = findViewById(R.id.button_show_answer);

        if (mAlreadyAnswered) {
            button.setVisibility(View.INVISIBLE);
            findViewById(R.id.textview_warning).setVisibility(View.INVISIBLE);
        }
        else
            button.setOnClickListener(v -> showHint());
    }

    private void getArgs() {
        Intent intent = getIntent();

        mAnswerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mAlreadyAnswered = intent.getBooleanExtra(EXTRA_ALREADY_ANSWERED, false);
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
}