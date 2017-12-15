package com.example.justovanderwerf.sportquiz;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justo van der Werf on 12/14/2017.
 *
 * Class to store JSON data.
 */

public class Question {
    private String correct, incorrect1, incorrect2, incorrect3, quest;

    public Question(String correct, String incorrect1, String incorrect2, String incorrect3, String quest) {
        this.correct = correct;
        this.incorrect1 = incorrect1;
        this.incorrect2 = incorrect2;
        this.incorrect3 = incorrect3;
        this.quest = quest;
    }

    public ArrayList<String> getAnswers(){
        ArrayList<String> answers = new ArrayList<>();
        answers.add(correct);
        answers.add(incorrect1);
        answers.add(incorrect2);
        answers.add(incorrect3);

        return answers;
    }

    public String getQuest(){
        return quest;
    }
}
