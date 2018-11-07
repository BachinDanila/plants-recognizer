package com.example.plantsrecognizer;

import java.io.Serializable;
import java.util.ArrayList;

public class QuestionModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String question = "";
    private ArrayList<String> answers = new ArrayList<>();
    private int numberOfSelectedAnswers = 0;
    private String selectedAnswer;

    String getQuestion() {
        return this.question;
    }

    void setQuestion(String question) {
        this.question = question;
    }

    ArrayList<String> getAnswers() {
        return this.answers;
    }

    void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    int countAnswers() {
        return this.answers.size();
    }

    String getAnswer(int index) {
        return this.answers.get(index);
    }

    void addAnswer(int index, String element) {
        this.answers.add(index, element);
    }

    int getSelectedCount() {
        return numberOfSelectedAnswers;
    }

    void setSelectedCount(int number) {
        this.numberOfSelectedAnswers = number;
    }

    String getSelectedAnswer() {
        return this.selectedAnswer;
    }

    void setSelectedAnswer(String answer) {
        this.selectedAnswer = answer;
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
