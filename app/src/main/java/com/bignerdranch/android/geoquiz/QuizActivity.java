package com.bignerdranch.android.geoquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    public static final String KEY_INDEX = "INDEX";

    private Question[] mQuestions = null;
    private byte[] mAnswers = null; // 0=no answer(default), 1=good answer, 2 = wrong answer
    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called, " + (savedInstanceState != null ? "savedInstanceState != null" : "savedInstanceState == null"));

        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        if (! fillQuestionsTable()) {
            finish();
        }

        buttonsHandler();


        questionUpdate();
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

        outState.putInt(KEY_INDEX, mCurrentIndex);;
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

    private void buttonsHandler() {
        Button buttonFalse = findViewById(R.id.button_false);
        buttonFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCorrectAnswer(false);
            }
        });

        Button buttonTrue = findViewById(R.id.button_true);
        buttonTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCorrectAnswer(true);
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

    private void showCorrectAnswer(boolean answer) {
        if (mQuestions[mCurrentIndex].checkAnswer(answer))
            showToast(R.string.good_answer_text);
        else
            showToast(R.string.wrong_answer_text);
    }

    private void nextQuestion() {
        if (++mCurrentIndex >= mQuestions.length)
            mCurrentIndex = 0;

        questionUpdate();
    }

    private void prevQuestion() {
        if (--mCurrentIndex < 0)
            mCurrentIndex = mQuestions.length - 1;

        questionUpdate();
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

    private void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }
}