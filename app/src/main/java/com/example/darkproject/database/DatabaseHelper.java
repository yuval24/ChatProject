package com.example.darkproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sharedmodule.ChatMessage;

import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper databaseHelperInstance = null;
    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;

    // Define your table and column names
    public static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_ID = "username";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_SENDER = "sender";
    public static final String COLUMN_RECEIVER = "receiver";

    // SQL statement to create the messages table
    private static final String SQL_CREATE_MESSAGES_TABLE =
            "CREATE TABLE " + TABLE_MESSAGES + " (" +
                    COLUMN_ID + " TEXT PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CONTENT + " TEXT, " +
                    COLUMN_RECEIVER + " TEXT, "+
                    COLUMN_SENDER + " TEXT )"
                    ;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (databaseHelperInstance == null) {
            databaseHelperInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return databaseHelperInstance;
    }
    public void insertMessage(ChatMessage message, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, username);
        values.put(COLUMN_CONTENT, message.getContent());
        values.put(COLUMN_RECEIVER, message.getRecipient());
        values.put(COLUMN_SENDER, message.getSender());

        db.insert(DATABASE_NAME, null, values);
        db.close();
    }

    public List<ChatMessage> getAllMessages() {
        // Retrieve all messages from the database
        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the messages table
        db.execSQL(SQL_CREATE_MESSAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades here
        // This method is called when the version number of the database is increased
    }
}
