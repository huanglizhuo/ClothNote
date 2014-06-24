package com.test.clothnote.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import data.ClothNoteDB;
import data.ClothNoteDBHelper;
import data.Note;

/**
 * Created by huanglizhuo on 14-5-22.
 */
public class NoteAdapter extends BaseAdapter implements SlideView.OnSlideListener {


    private LayoutInflater mInflater;
    private List<MessageItem> mMessageItems ;
    private Context context;
    public SlideView mLastSlideViewWithStatusOn;
    public int cposition;
    public NoteAdapter(final Context context,ArrayList<Note> mNote){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        mMessageItems = new ArrayList<MessageItem>();

        for (int i = 0; i<mNote.size() ; i++){
            MessageItem item = new MessageItem();
            item.sigleNote = mNote.get(i);
            mMessageItems.add(item);
        }

    }

    @Override
    public int getCount() {
        return mMessageItems.size();
    }

    @Override
    public Object getItem(int position) {
        cposition = position;
        return mMessageItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        cposition = position;
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        SlideView slideView = (SlideView) convertView;
        if (slideView == null) {
            View itemView = mInflater.inflate(R.layout.list_item, null);
            slideView = new SlideView(this.context);
            slideView.setContentView(itemView);
            holder = new ViewHolder(slideView);
            slideView.setOnSlideListener(this);
            slideView.setTag(holder);
        } else {
            holder = (ViewHolder) slideView.getTag();
        }
        MessageItem item = mMessageItems.get(position);
        item.slideView = slideView;
        item.slideView.shrink();
        holder.title.setText(item.sigleNote.getContent());
        holder.time.setText(item.sigleNote.getRemindtime());
        holder.deleteHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(cposition);
                notifyDataSetChanged();
            }
        });

        return slideView;
    }

    @Override
    public void onSlide(View view, int status) {
        if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
            mLastSlideViewWithStatusOn.shrink();
        }

        if (status == SLIDE_STATUS_ON) {
            mLastSlideViewWithStatusOn = (SlideView) view;
        }
    }



    public class MessageItem {
        public Note sigleNote;
        public SlideView slideView;
        public Note getSigleNote(){
            return sigleNote;
        }
    }

    private static class ViewHolder {
        public TextView title;
        public TextView time;
        public ViewGroup deleteHolder;

        ViewHolder(View view) {
            title = (TextView) view.findViewById(R.id.title);
            time = (TextView) view.findViewById(R.id.time);
            deleteHolder = (ViewGroup)view.findViewById(R.id.holder);
        }
    }

    public void remove(int position){
        ClothNoteDBHelper  dbHelper = new ClothNoteDBHelper(context);
        dbHelper.delete(mMessageItems.get(position).sigleNote);
        mMessageItems.remove(position);
    }

}
