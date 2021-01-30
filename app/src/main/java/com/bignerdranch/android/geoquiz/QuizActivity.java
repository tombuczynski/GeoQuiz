package com.bignerdranch.android.geoquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "INDEX";
    private static final String KEY_ANSWERS = "ANSWERS";

    private static final int NO_ANSWER = 0;
    private static final int GOOD_ANSWER = 1;
    private static final int WRONG_ANSWER = 2;

    private Question[] mQuestions = null;
    private byte[] mAnswers = null; // 0=no answer(default), 1=good answer, 2 = wrong answer
    private int mCurrentIndex = 0;
    public Button mButtonFalse;
    public Button mButtonTrue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called, " + (savedInstanceState != null ? "savedInstanceState != null" : "savedInstanceState == null"));

        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mAnswers = savedInstanceState.getByteArray(KEY_ANSWERS);
        }

        if (! fillQuestionsTable()) {
            finish();
        }

        buttonsHandler();

        questionUpdate();
        checkResult();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: called");

        outState.putInt(KEY_INDEX, mCurrentIndex);
        outState.putByteArray(KEY_ANSWERS, mAnswers);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: called");
    }

    private void questionUpdate() {
        if (mCurrentIndex >= mQuestions.length)
            return;

        TextView questionText = findViewById(R.id.textview_question);
        questionText.setText(mQuestions[mCurrentIndex].getQuestionText());
    }

    private void clearResult() {
        Arrays.fill(mAnswers, (byte) NO_ANSWER);

        mButtonFalse.setEnabled(true);
        mButtonTrue.setEnabled(true);
    }

    /**
     *
     * @return -1 - not all questions answered; >= 0 - [%} of good answers
     */
    private int checkResult() {
        boolean noAnswer = mAnswers[mCurrentIndex] == NO_ANSWER;
        mButtonFalse.setEnabled(noAnswer);
        mButtonTrue.setEnabled(noAnswer);

        int goodAnswers = 0;
        for (byte answer : mAnswers) {
            if (answer == NO_ANSWER)
                return -1;

            if (answer == GOOD_ANSWER) {
                goodAnswers++;
            }
        }

        return (goodAnswers * 100) / mAnswers.length;
    }

    private void recordAnswer(boolean goodAnswer) {
        mAnswers[mCurrentIndex] = (byte)(goodAnswer ? GOOD_ANSWER : WRONG_ANSWER);

        int result = checkResult();
        if (result >=0)
            showResult(result);
    }

    private void buttonsHandler() {
        mButtonFalse = findViewById(R.id.button_false);
        mButtonFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswer(false);
            }
        });

        mButtonTrue = findViewById(R.id.button_true);
        mButtonTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswer(true);
            }
        });

        View buttonPrev = findViewById(R.id.button_prev);
        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevQuestion();
            }
        });

        View buttonNext = findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });

        TextView tvQuestion = findViewById(R.id.textview_question);
        tvQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });

    }

    private void nextQuestion() {
        if (++mCurrentIndex >= mQuestions.length)
            mCurrentIndex = 0;

        questionUpdate();
        checkResult();
    }

    private void prevQuestion() {
        if (--mCurrentIndex < 0)
            mCurrentIndex = mQuestions.length - 1;

        questionUpdate();
        checkResult();
    }

    private boolean fillQuestionsTable() {
        String[] q_texts = getResources().getStringArray(R.array.question_texts);
        int[] q_answers = getResources().getIntArray(R.array.question_answers);

        if (q_texts.length == q_answers.length) {
            mQuestions = new Question[q_texts.length];
            if (mAnswers == null) {
                mAnswers = new byte[q_texts.length];
            }

            for (int i = 0; i < q_texts.length; i++) {
                mQuestions[i] = new Question(q_texts[i], q_answers[i]);
            }
        } else {
            Log.d(TAG, "fillQuestionsTable: q_texts.length != q_answers.length");
            return false;
        }

        return true;
    }

    private void showAnswer(boolean answer) {
        if (mQuestions[mCurrentIndex].checkAnswer(answer)) {
            showToast(R.string.good_answer_text);
            recordAnswer(true);
        } else {
            showToast(R.string.wrong_answer_text);
            recordAnswer(false);
        }
    }

    private void showResult(int result) {
        String msg = getResources().getString(R.string.result_percent, result);

        AlertDialog.Builder dlgB = new AlertDialog.Builder(this);
        dlgB.setTitle(R.string.quiz_finish)
            .setMessage(msg)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            })
            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    clearResult();
                }
            });

        dlgB.create().show();
    }

    private void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }
}