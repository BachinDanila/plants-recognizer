package com.example.plantsrecognizer;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestionModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String question;
    private ArrayList<String> answers;

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getAnswers() {
        return this.answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        String finalString = question + " ";
        for (String element : answers) {
            finalString += element + " ";
        }
        return finalString;
    }
}
