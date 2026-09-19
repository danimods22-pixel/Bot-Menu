package com.dani.modder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class BotDatabase extends SQLiteOpenHelper {
    public static class Message { public final String text, sender; Message(String text, String sender) { this.text = text; this.sender = sender; } }
    public BotDatabase(Context context) { super(context, "bot_menu.db", null, 2); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE commands (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE NOT NULL)");
        db.execSQL("CREATE TABLE messages (id INTEGER PRIMARY KEY AUTOINCREMENT, body TEXT NOT NULL, sender TEXT NOT NULL, created_at INTEGER NOT NULL)");
        db.execSQL("INSERT INTO commands(name) VALUES ('/start'),('/help'),('/info')");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) db.execSQL("CREATE TABLE messages (id INTEGER PRIMARY KEY AUTOINCREMENT, body TEXT NOT NULL, sender TEXT NOT NULL, created_at INTEGER NOT NULL)");
    }
    public void addCommand(String name) { getWritableDatabase().execSQL("INSERT OR IGNORE INTO commands(name) VALUES (?)", new Object[]{name}); }
    public int commandCount() { Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM commands", null); c.moveToFirst(); int result = c.getInt(0); c.close(); return result; }
    public void addMessage(String body, String sender) { getWritableDatabase().execSQL("INSERT INTO messages(body,sender,created_at) VALUES (?,?,?)", new Object[]{body, sender, System.currentTimeMillis()}); }
    public ArrayList<Message> messages() { ArrayList<Message> result = new ArrayList<>(); Cursor c = getReadableDatabase().rawQuery("SELECT body,sender FROM messages ORDER BY id ASC", null); while (c.moveToNext()) result.add(new Message(c.getString(0), c.getString(1))); c.close(); return result; }
}
