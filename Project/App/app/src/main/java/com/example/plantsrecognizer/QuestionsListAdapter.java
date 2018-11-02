package com.example.plantsrecognizer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class QuestionsListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final ArrayList<QuestionModel> questionsArrayList;
    private final LayoutInflater inflater;

    public QuestionsListAdapter(Context context, ArrayList<QuestionModel> questionsArrayList) {
        this.context = context;
        this.questionsArrayList = questionsArrayList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //CHILD METHODS
    @Override
    public Object getChild(int groupPos, int childPos) {
        return questionsArrayList.get(groupPos).getAnswers().get(childPos);
    }

    @Override
    public long getChildId(int arg0, int arg1) {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPos) {
        return questionsArrayList.get(groupPos).getAnswers().size();
    }

    @Override
    public View getChildView(int groupPos, int childPos, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_question_list_answers, null);
        }

        //GET ANSWER
        String child = (String) getChild(groupPos, childPos);

        //SET CHILD VALUE
        TextView answerTextView = convertView.findViewById(R.id.answer);
        answerTextView.setText(child);

        //GET QUESTION
        String question = getGroup(groupPos).toString();

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    //GROUP METHODS
    @Override
    public int getGroupCount() {
        return questionsArrayList.size();
    }

    @Override
    public Object getGroup(int groupPos) {
        return questionsArrayList.get(groupPos);
    }

    @Override
    public long getGroupId(int arg0) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_question_list_top, null);
        }

        //GET GROUP ITEM
        QuestionModel model = (QuestionModel) getGroup(groupPosition);

        //SET GROUP NAME(QUESTION)
        TextView questionTextView = convertView.findViewById(R.id.question);

        String currentQuestion = model.getQuestion();
        questionTextView.setText(currentQuestion);

        //SET GROUP ROW BACKGROUND COLOR
        convertView.setBackgroundColor(Color.LTGRAY);

        return convertView;
    }

    public void removeGroup(QuestionModel questionModel) {
        questionsArrayList.remove(questionModel);
        notifyDataSetChanged();
    }

    public void addGroup(QuestionModel questionModel) {
        questionsArrayList.add(questionModel);
        notifyDataSetChanged();
    }
}
