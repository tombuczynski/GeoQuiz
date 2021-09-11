package com.bignerdranch.android.geoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

public class HintActivity extends AppCompatActivity {
    public static final String EXTRA_ANSWER_IS_TRUE = "ANSWER_IS_TRUE";
    public static final String EXTRA_ALREADY_ANSWERED = "ALREADY_ANSWERED";

    private boolean mAnswerIsTrue, mAlreadyAnswered;
    private TextView mTextViewAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);

        mTextViewAnswer = findViewById(R.id.textview_answer);

        readParams();
        buttonsHandler();
    }

    private void buttonsHandler() {
        Button button;

        button = findViewById(R.id.button_show_answer);
        button.setOnClickListener(v -> showHint());
    }

    private void readParams() {
        Intent intent = getIntent();

        mAnswerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mAlreadyAnswered = intent.getBooleanExtra(EXTRA_ALREADY_ANSWERED, false);
    }

    private void showHint() {
        String anwser = getString(R.string.hint_answer) +
                (mAnswerIsTrue ? getString(R.string.answer_true_button) : getString(R.string.answer_false_button));

        mTextViewAnswer.setText(anwser);
    }
}