package com.github.sshaddicts.skeptikos.utilities.grid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.github.sshaddicts.skeptikos.R;

import java.util.List;
import java.util.Objects;


public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private int resource;
    private List<Bitmap> objects;

    public GridViewAdapter( Context context, int resource, List<Bitmap> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Objects.requireNonNull(parent);

        View row = convertView;
        ViewHolder holder = null;

        if(row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.gridRowImageView);

            row.setTag(holder);
        }else{
            holder = (ViewHolder) row.getTag();
        }

        Bitmap bmp = objects.get(position);
        holder.image.setImageBitmap(bmp);
        return row;
    }

    private class ViewHolder{
        ImageView image;
    }
}
