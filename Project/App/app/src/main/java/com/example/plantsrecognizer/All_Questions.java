package com.example.plantsrecognizer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class All_Questions extends AppCompatActivity implements Serializable {

    private ThemeHandler handler;
    private static final long serialVersionUID = 1L;
    transient QuestionsListAdapter questionsAdapter;

    XlsParser xls;
    QuestionModel questionModel;

    private transient ListView listView;
    private transient ArrayList<QuestionModel> questionModelList = null;
    private transient String[] allQuestions;
    private transient ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        handler = new ThemeHandler(this);
        handler.Handle();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_question);

        xls = new XlsParser(this);
        allQuestions = xls.getXlsQuestions();
        questionModelList = new ArrayList<>();

        try {
            for (String question : allQuestions) {
                questionModel = new QuestionModel();
                questionModel.setQuestion(question);
                questionModel.setAnswers(xls.getXlsAnswers(question));
                questionModelList.add(questionModel);
                Log.d("QUESTIONMODEL", question + " " + xls.getXlsAnswers(question).size());
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }

        expandableListView = findViewById(R.id.expandableListView);
        questionsAdapter = new QuestionsListAdapter(this, questionModelList);
        expandableListView.setAdapter(questionsAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPos, int childPos, long id) {
                Toast.makeText(getApplicationContext(), questionModelList.get(groupPos).getAnswers().get(childPos),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPos, long id) {
                Toast.makeText(getApplicationContext(), questionModelList.get(groupPos).getQuestion(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
}
