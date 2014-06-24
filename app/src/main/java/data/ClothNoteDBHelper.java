package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by huanglizhuo on 14-5-6.
 */
public class ClothNoteDBHelper {

    private static String table = ClothNoteDB.TABLE_NAME ;
    private SQLiteDatabase noteDB;

    public ClothNoteDBHelper(Context context){
        noteDB = new ClothNoteDB(context).getWritableDatabase();
    }
    public long add(Note note){
        ContentValues values = new ContentValues();
        values.put("content",note.getContent());
        values.put("createdtime",note.getCreatetime());
        values.put("status",note.getStatus());
        values.put("frequency",note.getFrequency());
        values.put("attribute",note.getAttributes());
        values.put("remindtime",note.getRemindtime());
        values.put("updatedtime",note.getUpdatedtime());
        long l = noteDB.insert(table,null,values);
        noteDB.close();
        return l;
    }

    public void delete(Note note){
        noteDB.delete(table,"_id=" + note.get_id(),null );
        noteDB.close();
    }

//    public Cursor query(SQLiteDatabase noteDB){
//        Cursor mCursor = noteDB.query(table,new String[]{
//                "_id","content","createdtime","status","frequency","attribute","remindtime","updatedtime"},null,null,null,null,null);
////        noteDB.close();
//        return mCursor;
//    }

    public ArrayList<Note> query(){
        ArrayList<Note> allNote = new java.util.ArrayList<Note>();
        Cursor mCursor = noteDB.query(table,new String[]{
                "_id","content","createdtime","status","frequency","attribute","remindtime","updatedtime"},null,null,null,null,null);

        while (true){
            if(!mCursor.moveToNext()){
                noteDB.close();
                return allNote;
            }
            Note singleNote = new Note();
            singleNote.set_id(mCursor.getInt(mCursor.getColumnIndex("_id")));
            singleNote.setContent(mCursor.getString(mCursor.getColumnIndex("content")));
            singleNote.setCreatetime(mCursor.getString(mCursor.getColumnIndex("createdtime")));
            singleNote.setStatus(mCursor.getString(mCursor.getColumnIndex("status")));
            singleNote.setFrequency(mCursor.getString(mCursor.getColumnIndex("frequency")));
            singleNote.setAttributes(mCursor.getString(mCursor.getColumnIndex("attribute")));
            singleNote.setRemindtime(mCursor.getString(mCursor.getColumnIndex("remindtime")));
            singleNote.setUpdatedtime(mCursor.getString(mCursor.getColumnIndex("updatedtime")));
            allNote.add(singleNote);
        }
    }
    public void update(Note note){
        ContentValues contentValues = new ContentValues();
        contentValues.put("content",note.getContent());
        contentValues.put("createdtime",note.getCreatetime());
        contentValues.put("status",note.getStatus());
        contentValues.put("frequency",note.getFrequency());
        contentValues.put("attribute",note.getAttributes());
        contentValues.put("remindtime",note.getRemindtime());
        contentValues.put("updatedtime",note.getUpdatedtime());
        noteDB.update(table,contentValues,"_id="+note.get_id(),null);
        noteDB.close();
    }
}
