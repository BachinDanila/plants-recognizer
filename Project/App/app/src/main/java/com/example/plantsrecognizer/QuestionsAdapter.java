package com.example.plantsrecognizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class QuestionsAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private LayoutInflater inflater;

    private List<QuestionModel> items;


    QuestionsAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<QuestionModel> items) {
        this.items = items;
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).getAnswer(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder holder;
        String item = getChild(groupPosition, childPosition);
        if (convertView == null) {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.questions_list_item, parent, false);
            holder.answer = convertView.findViewById(R.id.answer);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        holder.answer.setText(item);

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return items.get(groupPosition).countAnswers();
    }

    @Override
    public QuestionModel getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder holder;
        QuestionModel item = getGroup(groupPosition);
        if (convertView == null) {
            holder = new GroupHolder();
            convertView = inflater.inflate(R.layout.questions_list_group, parent, false);
            holder.question = convertView.findViewById(R.id.question);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }
        holder.question.setText(item.getQuestion());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    void removeGroup(QuestionModel model) {
        items.remove(model);
        notifyDataSetChanged();
    }

    void addGroup(QuestionModel model) {
        items.add(model);
        notifyDataSetChanged();
    }

    private class ChildHolder {
        TextView answer;
    }

    private class GroupHolder {
        TextView question;
    }

}
