package br.com.jera.hackathonford.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.jera.hackathonford.R;
import br.com.jera.hackathonford.model.DrivingEvent;

/**
 * Created by rodrigo on 2/6/15.
 */
public class DrivingEventAdapter extends ArrayAdapter<DrivingEvent> {
    Activity context;
    List<DrivingEvent> drivingEvents;
    private SparseBooleanArray mSelectedItemsIds;

    public DrivingEventListAdapter(Activity context, int resId, List<DrivingEvent> drivingEvents) {
        super(context, resId, drivingEvents);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.drivingEvents = drivingEvents;
    }

    private class ViewHolder {
        TextView locationName;
        TextView occurence;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_location, null);
            holder = new ViewHolder();
            holder.locationName = (TextView) convertView
                    .findViewById(R.id.locationName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DrivingEvent drivingEvent = getItem(position);
        holder.locationName.setText(drivingEvent.eventType);
        holder.occurence.setText(drivingEvent.datetime.toString());
        convertView
                .setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4
                        : Color.TRANSPARENT);

        return convertView;
    }

    @Override
    public void add(DrivingEvent categoria) {
        categorias.add(categoria);
        notifyDataSetChanged();
        Toast.makeText(context, categorias.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void remove(DrivingEvent object) {
        // super.remove(object);
        categorias.remove(object);
        notifyDataSetChanged();
    }

    public List<DrivingEvent> getDrivingEvents() {
        return categorias;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}

