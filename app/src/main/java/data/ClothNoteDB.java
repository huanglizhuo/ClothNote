package data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by huanglizhuo on 14-5-6.
 */
public class ClothNoteDB extends SQLiteOpenHelper {

    public final static String TABLE_NAME = "Note";
    public final static String ID = "_id";
    public final static String CONTENT = "content";
    public final static String CREATEDTIME = "createdtime";
    public final static String STATUS = "status";//完成 未完成 正再进行
    public final static String FREQUENCY = "frequency";//频率 每天 一次 每周那几天 每月那几天。。。。
    public final static String ATTRIBUTE = "attribute";//提醒事件 0记录 1提醒
    public final static String REMINDTIME = "remindtime";
    public final static String UPDATEDTIME = "updatedtime";

    private final String CREATE_NOTE_TABLE =  "create table Note(`_id` integer primary key autoincrement,"
            + "`content` text,"
            + "`createdtime` text,"
            + "`status` text,"
            + "`frequency` text,"
            + "`attribute` int,"
            + "`remindtime` text,"
            + "`updatedtime` text)"
            ;
    private static final String Name = "ClothNote";
    private static final int VERSION = 1;

    public ClothNoteDB(Context context,String name, CursorFactory factory, int version){
        super(context,name,factory,version);
    }
    public ClothNoteDB(Context context){
        this(context,Name,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_NOTE_TABLE);
        Log.e("test","Create");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}

