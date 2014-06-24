package com.test.clothnote.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import java.io.File;
import data.ClothNoteDBHelper;

public class MainActivity extends ActionBarActivity {

    private ClothNoteDBHelper dbHelper;
    private ParallaxListView parallaxListView;
    private NoteAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4A8CF6")));
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this,SettingActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void load() {
        dbHelper = new ClothNoteDBHelper(this);
        mAdapter = new NoteAdapter(this,dbHelper.query());
        parallaxListView = (ParallaxListView)findViewById(R.id.xListView);
        SharedPreferences settings = getSharedPreferences("settings", 0);

        if(!settings.getString("headimg","no").equals("no")){
            parallaxListView.setImageView(settings.getString("headimg","no"));
        }

        parallaxListView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        final Intent editview = new Intent(this,EditView.class);
        parallaxListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (parallaxListView.isMove == true){
                    return;
                }else {
                    NoteAdapter.MessageItem messageItem = (NoteAdapter.MessageItem)mAdapter.getItem(position-1);
                    editview.putExtra("Note",messageItem.getSigleNote());
                    startActivity(editview);
                }
            }
        });

        parallaxListView.setAdapter(mAdapter);
        parallaxListView.setDivider(null);

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            File sdcardDir = Environment.getExternalStorageDirectory();
            String path = sdcardDir.getPath()+"/ClothNote";
            File path1 = new File(path);
            if(!path1.exists()){
                path1.mkdirs();
            }
        }

    }

    @Override
    protected void onResume() {
        load();
        super.onResume();
    }

    @Override
    protected void onStart() {
        load();
        super.onStart();
    }

    @Override
    protected void onRestart() {
        load();
        super.onRestart();
    }

}
