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
    private transient QuestionsAdapter questionsAdapter;

    private transient ArrayList<QuestionModel> questionModelList = null;
    private transient String[] allQuestions;
    private XlsParser xls;
    private AnimatedExpandableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        PreferenceHandler preferences = new PreferenceHandler(this);     //Used for read current preference data
        preferences.setTheme();                                                 //Handles changes to the settings

        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_question);      //Initialize Activity

        handleIntent(getIntent());                  //Check intent to handle the search key click

        xls = new XlsParser(this);
        allQuestions = xls.getXlsQuestions();       //Get all questions
        questionModelList = new ArrayList<>();      //Create List which contains Question Models. Used in custom adapter

        //Set up Animated Expandable List View with custom adapter
        listView = findViewById(R.id.expandableListView);
        questionsAdapter = new QuestionsAdapter(this);
        questionsAdapter.setData(questionModelList);
        listView.setAdapter(questionsAdapter);

        setQuestions();

        setExpandableListViewOnChildClickListener();
        setExpandableListViewOnGroupClickListener();
    }

    private void setExpandableListViewOnChildClickListener() {
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
    }

    private void setExpandableListViewOnGroupClickListener() {
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

    private void setQuestions() {
        try {
            for (String filename : allQuestions) {
                questionModelList.add(readQuestionModel(filename)); //Read serialized object and add it to list
            }
        } catch (FileNotFoundException exc) {       //Activated if no serialized object was found
            try {
                for (String question : allQuestions) {
                    QuestionModel questionModel = new QuestionModel();
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
    }

    //New intent used to handle search button click activity
    //Used for starting handler
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    //Intent which activates when user press search button
    //Write search request to Log
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            search(query);
            Log.d("SEARCH", query);
        }
    }

    //Search in ExpandableListView
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
        checkIfNotFound();
    }

    private void checkIfNotFound() {
        //If the element was not found
        //Used only in @search
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

    //Write Question model data to file
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

    //Read Question model from serialized object
    //Filename equals to plant name
    //If throws FileNotFoundException create new file with serialized object
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

    //Create options menu for search activity
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
