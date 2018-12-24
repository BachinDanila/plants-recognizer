package com.example.plantsrecognizer.Models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestionModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String question = "";
    private ArrayList<String> answers = new ArrayList<>();
    private String selectedAnswer;

    //TODO: Remove unused field
    //private int numberOfSelectedAnswers = 0;

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    public int countAnswers() {
        return this.answers.size();
    }

    public String getAnswer(int index) {
        return this.answers.get(index);
    }

    public String getSelectedAnswer() {
        return this.selectedAnswer;
    }

    public void setSelectedAnswer(String answer) {
        this.selectedAnswer = answer;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder finalString = new StringBuilder(question + " ");
        for (String element : answers) {
            finalString.append(element).append(" ");
        }
        return finalString.toString();
    }

    //TODO: Remove unused blocks of code when finish
    /*
    public void addAnswer(int index, String element) {
        this.answers.add(index, element);
    }

    public ArrayList<String> getAnswers() {
        return this.answers;
    }

    public int getSelectedCount() {
        return numberOfSelectedAnswers;
    }

    public void setSelectedCount(int number) {
        this.numberOfSelectedAnswers = number;
    }
    */
}
