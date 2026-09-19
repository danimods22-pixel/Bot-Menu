package com.dani.modder;

import android.content.*;
import android.database.sqlite.*;
import java.util.ArrayList;

public class BotDatabase extends SQLiteOpenHelper {
    BotDatabase(Context c){super(c,"bot_menu.db",null,1);}
    public void onCreate(SQLiteDatabase db){db.execSQL("CREATE TABLE commands (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE NOT NULL)"); db.execSQL("INSERT INTO commands(name) VALUES ('/start'),('/help')");}
    public void onUpgrade(SQLiteDatabase db,int oldV,int newV){db.execSQL("DROP TABLE IF EXISTS commands");onCreate(db);}
    void add(String name){getWritableDatabase().execSQL("INSERT OR IGNORE INTO commands(name) VALUES (?)",new Object[]{name});}
    void remove(String name){getWritableDatabase().delete("commands","name=?",new String[]{name});}
    ArrayList<String> all(){ArrayList<String> out=new ArrayList<>();Cursor c=getReadableDatabase().rawQuery("SELECT name FROM commands ORDER BY id DESC",null);while(c.moveToNext())out.add(c.getString(0));c.close();return out;}
}
