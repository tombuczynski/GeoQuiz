package com.bignerdranch.android.geoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private Question[] mQuestions = new Question[0];
    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        fillQuestionsTable();
        buttonsHandler();

        questionUpdate();
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

        Button buttonPrev = findViewById(R.id.button_prev);
        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevQuestion();
            }
        });

        Button buttonNext = findViewById(R.id.button_next);
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

    private void fillQuestionsTable() {
        String[] q_texts = getResources().getStringArray(R.array.question_texts);
        int[] q_answers = getResources().getIntArray(R.array.question_answers);

        if (q_texts.length == q_answers.length) {
            mQuestions = new Question[q_texts.length];
            for (int i = 0; i < q_texts.length; i++) {
                mQuestions[i] = new Question(q_texts[i], q_answers[i]);
            }
        }
    }

    private void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }
}