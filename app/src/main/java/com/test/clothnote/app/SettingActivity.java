package com.test.clothnote.app;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import data.ClothNoteDBHelper;
import data.Note;

public class SettingActivity extends Activity implements View.OnClickListener {

    private Switch floatSwitch;
    private TextView out2sd;
    private Button chooseim;
    private SharedPreferences settings;
    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4A8CF6")));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_setting);

        settings = getSharedPreferences("settings", 0);

        chooseim = (Button)findViewById(R.id.imageChoose);
        chooseim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);

            }
        });

        out2sd = (TextView)findViewById(R.id.out2txt);
        out2sd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                out2Text();
            }
        });
        floatSwitch = (Switch)findViewById(R.id.float_switch);
        floatSwitch.setOnClickListener(this);

        if (settings.getBoolean("floatSwitch",true)){
            floatSwitch.setChecked(true);
        }else {
            floatSwitch.setChecked(false);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Log.e("test",picturePath);
            SharedPreferences settings = getSharedPreferences("settings", 0);
            settings.edit().putString("headimg", picturePath).commit();
//            imghh.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(SettingActivity.this, ServiceFloating.class);
        if(floatSwitch.isChecked()){
            settings.edit().putBoolean("floatSwitch", true).commit();
            startService(i);
        }else {
            settings.edit().putBoolean("floatSwitch", false).commit();
            stopService(i);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void out2Text(){
        File sdcardDir = Environment.getExternalStorageDirectory();
        String path = sdcardDir.getPath()+"/ClothNote/";
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd.hh:mm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str =  formatter.format(curDate);

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            File path1 = new File(path+str);
            if(!path1.exists()){
                path1.mkdirs();
            }
        }else {
            Toast.makeText(this, "SD卡不可用 无法导出", Toast.LENGTH_SHORT).show();
            return ;
        }
        FileOutputStream outputStream = null;
        ArrayList<Note> mNote = (new ClothNoteDBHelper(this)).query();
        for(Note single:mNote){
            try {
                outputStream = new FileOutputStream(new File(path+str+"/"+single.getCreatetime()));
                outputStream.write(single.getContent().getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(this, "已导出至"+path+str, Toast.LENGTH_LONG).show();
    }

}
