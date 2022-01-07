package com.bignerdranch.android.geoquiz;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String EXTRA_INDEX = "INDEX";
    private static final String EXTRA_ANSWERS = "ANSWERS";

    private static final Integer HINTS_AVAILABLE = 5;

    private static final int NO_ANSWER = 0;
    private static final int GOOD_ANSWER = 1;
    private static final int WRONG_ANSWER = 2;
    private static final int HINT_USED_FOR_ANSWER = 3;

    private Question[] mQuestions = null;
    private byte[] mAnswers = null; // 0=no answer(default), 1=good answer, 2 = wrong answer, 3 = hint used
    private int mCurrentIndex = 0;
    private int mHintsLeftCnt = HINTS_AVAILABLE;

    private Button mButtonFalse;
    private Button mButtonTrue;
    private TextView mQuestionText;

    private final ActivityResultLauncher<Intent> mStartHintActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                int code = result.getResultCode();
                Intent data = result.getData();

                if (code == RESULT_OK && data != null) {
                    boolean hintUsed = data.getBooleanExtra(HintActivity.EXTRA_HINT_USED, false);

                    if (hintUsed && isNoCurrentAnswer()) {
                        mAnswers[mCurrentIndex] = HINT_USED_FOR_ANSWER;

                        if (mHintsLeftCnt > 0)
                            mHintsLeftCnt--;
                    }
                }

                Log.d(TAG, "HintActivity result code = " + code);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called, " + (savedInstanceState != null ? "savedInstanceState != null" : "savedInstanceState == null"));

        Log.d(TAG,  String.format("Android version: %s, API level: %d", Build.VERSION.RELEASE, Build.VERSION.SDK_INT));

        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(EXTRA_INDEX, 0);
            mAnswers = savedInstanceState.getByteArray(EXTRA_ANSWERS);

            mHintsLeftCnt = savedInstanceState.getInt(HintActivity.EXTRA_HINTS_LEFT_CNT, HINTS_AVAILABLE);
        }

        if (! fillQuestionsTable()) {
            finish();
        }

        mQuestionText = findViewById(R.id.textview_question);

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

        outState.putInt(EXTRA_INDEX, mCurrentIndex);
        outState.putByteArray(EXTRA_ANSWERS, mAnswers);

        outState.putInt(HintActivity.EXTRA_HINTS_LEFT_CNT, mHintsLeftCnt);
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

        //Log.d(TAG, "questionUpdate", new RuntimeException("Break"));
        mQuestionText.setText(mQuestions[mCurrentIndex].getQuestionText());
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
        boolean noAnswer = isNoCurrentAnswer();
        mButtonFalse.setEnabled(noAnswer);
        mButtonTrue.setEnabled(noAnswer);

        int goodAnswers = 0;
        for (byte answer : mAnswers) {
            if (isNoAnswer(answer))
                return -1;

            if (answer == GOOD_ANSWER) {
                goodAnswers++;
            }
        }

        return (goodAnswers * 100) / mAnswers.length;
    }

    private boolean isNoCurrentAnswer() {
        return isNoAnswer(mAnswers[mCurrentIndex]);
    }

    private boolean isNoAnswer(byte answer) {
        return (answer == NO_ANSWER) || (answer == HINT_USED_FOR_ANSWER);
    }

    private boolean isCurrentAnswerTrue() {
        return mQuestions[mCurrentIndex].isAnswerTrue();
    }

    private void recordAnswer(boolean goodAnswer) {
        mAnswers[mCurrentIndex] = (byte)(goodAnswer ? GOOD_ANSWER : WRONG_ANSWER);

        int result = checkResult();
        if (result >=0)
            showResult(result);
    }

    private void buttonsHandler() {
        mButtonFalse = findViewById(R.id.button_false);
        mButtonFalse.setOnClickListener(v -> showAnswer(false));

        mButtonTrue = findViewById(R.id.button_true);
        mButtonTrue.setOnClickListener(v -> showAnswer(true));

        View buttonPrev = findViewById(R.id.button_prev);
        buttonPrev.setOnClickListener(v -> prevQuestion());

        View buttonNext = findViewById(R.id.button_next);
        buttonNext.setOnClickListener(v -> nextQuestion());

        TextView tvQuestion = findViewById(R.id.textview_question);
        tvQuestion.setOnClickListener(v -> nextQuestion());

        View buttonShowHint = findViewById(R.id.button_show_hint);
        buttonShowHint.setOnClickListener(v -> startHintActivity());
    }

    private void startHintActivity() {
        Intent intent = new Intent(this, HintActivity.class);
        intent.putExtra(HintActivity.EXTRA_ANSWER_IS_TRUE, isCurrentAnswerTrue());
        intent.putExtra(HintActivity.EXTRA_ALREADY_ANSWERED, ! isNoCurrentAnswer());
        intent.putExtra(HintActivity.EXTRA_HINTS_LEFT_CNT, mHintsLeftCnt);

        mStartHintActivity.launch(intent);
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
            String answer_str = getString(R.string.good_answer_text) + " ";
            if (mAnswers[mCurrentIndex] == HINT_USED_FOR_ANSWER)
                answer_str += getString(R.string.hint_used);

            showToast(answer_str);
            recordAnswer(true);
        } else {
            showToast(R.string.wrong_answer_text);
            recordAnswer(false);
        }
    }

    private void showResult(int result) {
        String msg = getString(R.string.result_percent, result);

        AlertDialog.Builder dlgB = new AlertDialog.Builder(this);
        dlgB.setTitle(R.string.quiz_finish)
            .setMessage(msg)
            .setPositiveButton("OK", (dialog, which) -> { });

        dlgB.create().show();
        clearResult();
    }

    private void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }
    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}