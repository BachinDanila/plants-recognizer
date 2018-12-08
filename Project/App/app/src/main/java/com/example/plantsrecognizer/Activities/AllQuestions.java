package com.example.plantsrecognizer.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.plantsrecognizer.Adapters.QuestionsAdapter;
import com.example.plantsrecognizer.Models.QuestionModel;
import com.example.plantsrecognizer.R;
import com.example.plantsrecognizer.Utils.PreferenceHandler;
import com.example.plantsrecognizer.Utils.XlsParser;
import com.example.plantsrecognizer.Views.AnimatedExpandableListView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class AllQuestions extends AppCompatActivity implements Serializable {

    private static final long serialVersionUID = 1L;
    transient QuestionsAdapter questionsAdapter;

    transient XlsParser xls;
    transient QuestionModel questionModel;

    private transient ArrayList<QuestionModel> questionModelList = null;
    private transient String[] allQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        PreferenceHandler handler = new PreferenceHandler(this);
        handler.Handle();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_question);

        handleIntent(getIntent());

        xls = new XlsParser(this);
        allQuestions = xls.getXlsQuestions();
        questionModelList = new ArrayList<>();

        try {
            for (String filename : allQuestions) {
                questionModelList.add(readQuestionModel(filename));
            }
        } catch (FileNotFoundException exc) {
            try {
                for (String question : allQuestions) {
                    questionModel = new QuestionModel();
                    questionModel.setQuestion(question);
                    questionModel.setAnswers(xls.getXlsAnswers(question));
                    questionModelList.add(questionModel);
                    write_file(questionModel);
                }
            } catch (Exception e) {
                e.printStackTrace();
                e.getCause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final AnimatedExpandableListView listView = findViewById(R.id.expandableListView);
        questionsAdapter = new QuestionsAdapter(this);
        questionsAdapter.setData(questionModelList);
        listView.setAdapter(questionsAdapter);

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPos, int childPos, long id) {
                QuestionModel current = questionModelList.get(groupPos);
                current.setSelectedAnswer(current.getAnswer(childPos));
                Toast.makeText(getApplicationContext(), current.getQuestion() + ": " +
                        current.getSelectedAnswer(), Toast.LENGTH_SHORT).show();
                listView.collapseGroupWithAnimation(groupPos);
                return false;
            }
        });

        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }

        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            search(query);
            Log.d("SEARCH", query);
        }
    }

    private void search(String keyword) {
        int len = questionsAdapter.getGroupCount();
        for (int j = 0; j < len; j++) {
            for (int i = 0; i < questionsAdapter.getGroupCount(); i++) {
                QuestionModel current = questionsAdapter.getGroup(i);
                String text = current.getQuestion().toLowerCase();
                Log.d("SEARCH", text + " " + keyword);
                if (!text.contains(keyword.toLowerCase())) {
                    questionsAdapter.removeGroup(current);
                }
            }
        }
        if (questionsAdapter.getGroupCount() == 0) {
            Toast toast = Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT);
            toast.show();
            try {
                for (String allQuestion : allQuestions) {
                    questionsAdapter.addGroup(readQuestionModel(allQuestion));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void write_file(QuestionModel object) {
        String filename = object.getQuestion();
        try {
            FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(object);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private QuestionModel readQuestionModel(String filename) throws FileNotFoundException {
        FileInputStream fis = openFileInput(filename);
        try {
            ObjectInputStream is = new ObjectInputStream(fis);
            QuestionModel model = (QuestionModel) is.readObject();
            is.close();
            fis.close();
            return model;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            assert searchManager != null;
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        return super.onCreateOptionsMenu(menu);
    }
}
