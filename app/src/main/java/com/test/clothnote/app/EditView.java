package com.test.clothnote.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;;
import data.ClothNoteDB;
import data.ClothNoteDBHelper;
import data.Note;
import mirko.android.datetimepicker.time.RadialPickerLayout;
import mirko.android.datetimepicker.time.TimePickerDialog;


public class EditView extends ActionBarActivity {

    public ClothNoteDBHelper dbHelper;
    private TextView remind;
    private EditText content;
    private final Calendar mCalendar = Calendar.getInstance();
    private int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
    private int minute = mCalendar.get(Calendar.MINUTE);
    private Note mNote;
    private boolean isnew;
    private Switch remindswitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4A8CF6")));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_edit_view);

        isnew = getIntent().getBooleanExtra("isnew",false);

        content = (EditText)findViewById(R.id.content);
        remind = (TextView)findViewById(R.id.tvTime);
        remindswitch = (Switch)findViewById(R.id.remindswitch);
        if (isnew){
            mNote = new Note();
            mNote.setContent("");
            mNote.setRemindtime(remind.getText().toString());
            resetTime(remind);
        }else {
            mNote = (Note)getIntent().getSerializableExtra("Note");
            content.setText(mNote.getContent());
            remind.setText(mNote.getRemindtime());
            if (mNote.getAttributes().equals("on")){
                remindswitch.setChecked(true);
            }
        }

        final TimePickerDialog timePickerDialog24h = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay,
                                  int minute) {

                remind.setText(
                        new StringBuilder().append(pad(hourOfDay))
                                .append(":").append(pad(minute)));

                remind.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);

        remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog24h.show(getFragmentManager(),"timePicker");
                String a[] = remind.getText().toString().split(":");
                Log.e("test",mCalendar.getTimeInMillis()+"");
            }
        });

//        remindswitch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (remindswitch.isChecked()){
//
//                }
//            }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_cancel) {
            Note old = (Note)getIntent().getSerializableExtra("Note");
            Log.e("test",old.getContent());
            content.setText(old.getContent());
            return true;
        }

        if (id == R.id.action_save){
            save();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        save();
        return super.onKeyDown(keyCode, event);
    }

    public void resetTime(TextView showtime) {
        showtime.setText(new StringBuilder().append(pad(hourOfDay))
                .append(":").append(pad(minute)));
        showtime.setTextColor(getResources().getColor(android.R.color.darker_gray));
    }

    public static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public static String pad2(int c) {
        if (c == 12)
            return String.valueOf(c) ;
        if (c == 00)
            return String.valueOf(c+12) ;
        if (c > 12)
            return String.valueOf(c-12) ;
        else
            return String.valueOf(c);
    }

    public static String pad3(int c) {
        if (c == 12)
            return " PM";
        if (c == 00)
            return " AM";
        if (c > 12)
            return " PM";
        else
            return " AM";
    }

    private void save(){
        dbHelper = new ClothNoteDBHelper(this);
        mNote.setContent(content.getText().toString());
        mNote.setUpdatedtime(new StringBuilder().append(pad(hourOfDay))
                .append(":").append(pad(minute)).toString());
        mNote.setRemindtime(remind.getText().toString());
//
//        if(remindswitch.isChecked()){
//            mNote.setAttributes("on");
//        }

        if (isnew && (!mNote.getContent().equals(""))){
            SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd.hh:mm");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String str =  formatter.format(curDate);
            mNote.setCreatetime(str);
            Log.e("test","保存new");
            dbHelper.add(mNote);
            return;
        }else if(mNote.getContent().equals("")){
            Toast.makeText(this,"不能创建空记事偶",Toast.LENGTH_LONG);
            return;
        }else {
            Log.e("test","update");
            dbHelper.update(mNote);
        }
    }

}
