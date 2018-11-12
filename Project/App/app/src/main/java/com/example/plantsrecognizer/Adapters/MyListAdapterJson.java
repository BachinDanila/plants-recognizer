package com.example.plantsrecognizer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plantsrecognizer.Models.JsonModel;
import com.example.plantsrecognizer.R;
import com.example.plantsrecognizer.Utils.ImageLoadTask;

import java.util.ArrayList;

public class MyListAdapterJson extends BaseAdapter {

    private final Context context;
    private final ArrayList<JsonModel> JsonModelArrayList;

    public MyListAdapterJson(Context context, ArrayList<JsonModel> JsonModelArrayList) {

        this.context = context;
        this.JsonModelArrayList = JsonModelArrayList;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return JsonModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return JsonModelArrayList.get(position);
    }

    public void removeItem(int position) {
        JsonModelArrayList.remove(position);
    }

    public void addItem(JsonModel jsonModel) {
        JsonModelArrayList.add(jsonModel);
        this.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            assert inflater != null;
            convertView = inflater.inflate(R.layout.row_custom_list, null, true);

            holder.ImageView = convertView.findViewById(R.id.imageView);
            holder.TextViewTitle = convertView.findViewById(R.id.textViewTitle);
            holder.TextViewDescription = convertView.findViewById(R.id.textViewDescription);
            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }
        try {
            JsonModel thisJsonModel = JsonModelArrayList.get(position);
            String source = thisJsonModel.getSource();
            new ImageLoadTask(source, holder.ImageView, context).execute();

            holder.TextViewTitle.setText(thisJsonModel.getTitle());
            holder.TextViewDescription.setText(thisJsonModel.getDescription());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder {
        protected ImageView ImageView;
        TextView TextViewTitle;
        TextView TextViewDescription;
    }
}
