package com.bignerdranch.android.geoquiz;

/**
 * Created by Tom Buczynski on 25.08.2020.
 */
public class Question {

    private String mQuestionText;
    private boolean mAnswerTrue;

    public String getQuestionText() {
        return mQuestionText;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public boolean checkAnswer(boolean answer) {
        return answer == mAnswerTrue;
    }

    public Question(String questionText, boolean answer) {
        mQuestionText = questionText;
        mAnswerTrue = answer;
    }

    public Question(String questionText, int answer) {
        mQuestionText = questionText;
        mAnswerTrue = answer != 0;
    }
}
