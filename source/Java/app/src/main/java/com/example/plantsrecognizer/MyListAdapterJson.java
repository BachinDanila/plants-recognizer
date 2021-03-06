package com.example.plantsrecognizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyListAdapterJson extends BaseAdapter {
    private Context context;
    private ArrayList<JsonModel> JsonModelArrayList;

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

            convertView = inflater.inflate(R.layout.row_custom_list, null, true);

            holder.ImageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.TextViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
            holder.TextViewDescription = (TextView) convertView.findViewById(R.id.textViewDescription);
            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }
        JsonModel thisJsonModel = JsonModelArrayList.get(position);
        String source = thisJsonModel.getSource();
        new ImageLoadTask(source, holder.ImageView).execute();

        holder.TextViewTitle.setText(thisJsonModel.getTitle());
        holder.TextViewDescription.setText(thisJsonModel.getDescription());

        return convertView;
    }

    private class ViewHolder {
        protected ImageView ImageView;
        protected TextView TextViewTitle;
        protected TextView TextViewDescription;
    }
}
