package ritik.nexcha;

/**
 * Created by SuperUser on 27-05-2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "chats";
    // Contacts table name
    private static final String TABLE_MAIN = "main";
    private static final String TABLE_CHATS = "chats";
    // Contacts Table Columns names
    private static final String KEY_ID = "_id";
    private static final String KEY_CHAT_UID = "chat_uid";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR_NAME = "author_name";
    private static final String KEY_COVER_IMAGE = "cover_image";
    private static final String KEY_VIEWS = "views";
    private static final String KEY_GENRE = "genre";
    private static final String KEY_DATETIME = "datetime";
    private static final String KEY_EPISODE = "episode";
    private static final String KEY_UPDATED = "updated";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_uVIEW = "uview";
    private static final String KEY_SEQUENCE = "sequence";
    private static final String KEY_TYPING = "typing";
    private static final String KEY_SENDER_NAME = "sender_name";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_COLOR = "sender_color";
    Context context;


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MAIN_TABLE = "CREATE TABLE " + TABLE_MAIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CHAT_UID + " TEXT,"
                + KEY_TITLE + " TEXT,"
                + KEY_AUTHOR_NAME + " TEXT,"
                + KEY_COVER_IMAGE + " TEXT,"
                + KEY_VIEWS + " INTEGER,"
                + KEY_GENRE + " TEXT,"
                + KEY_DATETIME + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_EPISODE + " INTEGER,"
                + KEY_uVIEW + " INTEGER DEFAULT 0,"
                + KEY_UPDATED + " INTEGER" + ")";
        db.execSQL(CREATE_MAIN_TABLE);
        String CREATE_CHATS_TABLE = "CREATE TABLE " + TABLE_CHATS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CHAT_UID + " INTEGER,"
                + KEY_SEQUENCE + " INTEGER,"
                + KEY_TYPING + " INTEGER,"
                + KEY_SENDER_NAME + " TEXT,"
                + KEY_MESSAGE + " TEXT,"
                + KEY_COLOR + " TEXT"
                + ")";
        db.execSQL(CREATE_CHATS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATS);

        // Create tables again
        onCreate(db);
    }


    public void IncView(String chat_uid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_MAIN +
                " SET " + KEY_uVIEW + " = " + KEY_uVIEW + " + 1" +
                " WHERE " + KEY_CHAT_UID + " = " + chat_uid);


    }

    public void ResetView(String chat_uid, int views) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_MAIN +
                " SET " + KEY_uVIEW + " = 0 WHERE " + KEY_CHAT_UID + " = " + chat_uid);
        db.execSQL("UPDATE " + TABLE_MAIN +
                " SET " + KEY_VIEWS + " = " + views + " WHERE " + KEY_CHAT_UID + " = " + chat_uid);


    }

    public GridItem getStory(int chat_uid) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MAIN, new String[]{KEY_CHAT_UID,
                        KEY_TITLE, KEY_COVER_IMAGE, KEY_GENRE, KEY_AUTHOR_NAME, KEY_VIEWS, KEY_DESCRIPTION, KEY_EPISODE, KEY_uVIEW}, KEY_CHAT_UID + "=?",
                new String[]{String.valueOf(chat_uid)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        GridItem story = new GridItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));
        return story;
    }

    public int getEpisodecount(String chat_uid) {
        chat_uid = chat_uid.substring(0, chat_uid.length() - 1);
        String Query = "SELECT  * FROM " + TABLE_MAIN + " WHERE " + KEY_CHAT_UID + " LIKE '" + chat_uid + "_'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(Query, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public String getLastStory() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MAIN, new String[]{"MAX(" + KEY_CHAT_UID + ")"}, null, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return cursor.getString(0);
    }

    public ArrayList<GridItem> getStoriesbygenre(String genre) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        ArrayList<GridItem> storyList = new ArrayList<GridItem>();
        if (!genre.equals("reading")) {
            if (genre.equals("popular"))
                cursor = db.query(TABLE_MAIN, new String[]{KEY_TITLE, KEY_COVER_IMAGE, KEY_CHAT_UID, KEY_AUTHOR_NAME}, null,
                        null, null, null, KEY_VIEWS + " DESC", null);
            else
                cursor = db.query(TABLE_MAIN, new String[]{KEY_TITLE, KEY_COVER_IMAGE, KEY_CHAT_UID, KEY_AUTHOR_NAME}, KEY_GENRE + "=?",
                        new String[]{String.valueOf(genre)}, null, null, KEY_CHAT_UID + " DESC", null);


            if ((cursor != null) && (cursor.getCount() > 0)) {

                cursor.moveToFirst();

                do {

                    GridItem story = new GridItem();

                    story.setTitle(cursor.getString(0));
                    story.setImage(cursor.getString(1));
                    story.setChat_uid(cursor.getString(2));
                    story.setAuthor(cursor.getString(3));
                    storyList.add(story);
                } while (cursor.moveToNext());
            }
        } else {
            PrefsHelper prefsHelper = new PrefsHelper();
            storyList.clear();
            Map<String, ?> allEntries = prefsHelper.getsavedchats(context);
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                storyList.add(getStory(Integer.parseInt(entry.getKey())));
            }
        }


        return storyList;
    }

    public ArrayList<GridItem> getsuggestions(int chat_uid) {
        ArrayList<GridItem> suggestions = new ArrayList<GridItem>();
        int next_episode = chat_uid + 1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MAIN, new String[]{KEY_TITLE, KEY_COVER_IMAGE, KEY_CHAT_UID, KEY_AUTHOR_NAME}, KEY_CHAT_UID + " = ?", new String[]{String.valueOf(next_episode)}, null, null, null);
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            GridItem story = new GridItem();
            story.setTitle(cursor.getString(0));
            story.setImage(cursor.getString(1));
            story.setChat_uid(cursor.getString(2));
            story.setAuthor(cursor.getString(3));
            suggestions.add(story);
        }
        int count = 3;
        if (!suggestions.isEmpty())
            count--;
        cursor = db.query(TABLE_MAIN, new String[]{KEY_TITLE, KEY_COVER_IMAGE, KEY_CHAT_UID, KEY_AUTHOR_NAME}, KEY_GENRE + " = ? AND " + KEY_CHAT_UID + " != ?", new String[]{getStory(chat_uid).getGenre(), String.valueOf(chat_uid)}, null, null, KEY_VIEWS + " DESC", String.valueOf(count));
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            do {
                GridItem story = new GridItem();
                story.setTitle(cursor.getString(0));
                story.setImage(cursor.getString(1));
                story.setChat_uid(cursor.getString(2));
                story.setAuthor(cursor.getString(3));
                suggestions.add(story);
            } while (cursor.moveToNext());
        }
        return suggestions;
    }


    public void insertjsonviastring(String jsonstring) {
        Log.d("Ritik", "insertjsonviastring: recieved jsonstring is " + jsonstring);
        try {
            JSONArray jsonArray = new JSONArray(jsonstring);
            SQLiteDatabase db = this.getWritableDatabase();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(KEY_TITLE, c.getString(KEY_TITLE));
                values.put(KEY_COVER_IMAGE, c.getString(KEY_COVER_IMAGE));
                values.put(KEY_GENRE, c.getString(KEY_GENRE));
                values.put(KEY_CHAT_UID, c.getString(KEY_CHAT_UID));
                values.put(KEY_VIEWS, c.getString(KEY_VIEWS));
                values.put(KEY_AUTHOR_NAME, c.getString(KEY_AUTHOR_NAME));
                values.put(KEY_EPISODE, c.getString(KEY_EPISODE));
                values.put(KEY_DESCRIPTION, c.getString(KEY_DESCRIPTION));

                db.insert(TABLE_MAIN, null, values);
            }

            db.close(); // Closing database connection


        } catch (Exception e) {
            Log.d("Ritik", "insertjsonviastring: databasehandler.class");
        }
    }

    public void updateviews(String jsonstring) {
        Log.d("Ritik", "updateview: recieved jsonstring is " + jsonstring);
        try {
            JSONArray jsonArray = new JSONArray(jsonstring);
            Log.d("Ritik", "updateviews: json array length " + jsonArray.length());
            SQLiteDatabase db = this.getWritableDatabase();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();

                Log.d("Ritik", "updateviews: " + c.getString(KEY_CHAT_UID) + " " + c.getString(KEY_VIEWS));

                values.put(KEY_CHAT_UID, c.getString(KEY_CHAT_UID));
                values.put(KEY_VIEWS, c.getString(KEY_VIEWS));

                db.update(TABLE_MAIN, values, KEY_CHAT_UID + " = ?", new String[]{c.getString(KEY_CHAT_UID)});

            }

            db.close(); // Closing database connection


        } catch (Exception e) {
            Log.d("Ritik", "insertjsonviastring: " + e.getMessage());
        }
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    public Message getnextmsg(int chat_uid, int last_sequence) {
        Message message = new Message();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHATS, new String[]{KEY_CHAT_UID, KEY_SEQUENCE, KEY_TYPING, KEY_SENDER_NAME, KEY_MESSAGE, KEY_COLOR}, KEY_CHAT_UID + "=? AND " +
                KEY_SEQUENCE + "=?", new String[]{String.valueOf(chat_uid), String.valueOf(last_sequence + 1)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            message.setMessage(cursor.getString(4));
            message.setName(cursor.getString(3));
            message.setTyping(cursor.getInt(2));
            message.setSequence(cursor.getInt(1));
            message.setColor(cursor.getString(5));
        }
        return message;
    }
//    void addchat() {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_CHAT_UID,"1443");
//        values.put(KEY_SENDER_NAME, "Ritik");
//        values.put(KEY_MESSAGE, "First ever msg");
//        values.put(KEY_SEQUENCE, "-1");
//        values.put(KEY_TYPING, "0");
//
//        // Inserting Row
//        db.insert(TABLE_CHATS, null, values);
//        db.close(); // Closing database connection
//    }

    // Getting single contact
    public Boolean IsChatexist(int chat_uid) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CHATS, new String[]{KEY_CHAT_UID}, KEY_CHAT_UID + "=? AND " +
                KEY_TYPING + "=?", new String[]{String.valueOf(chat_uid), String.valueOf(-1)}, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {

                return cursor.getString(0).equals(String.valueOf(chat_uid));
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public Boolean Ischatindexed(int chat_uid) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MAIN, new String[]{KEY_CHAT_UID}, KEY_CHAT_UID + "=?", new String[]{String.valueOf(chat_uid)}, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {

                return cursor.getString(0).equals(String.valueOf(chat_uid));
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    public void insertjsonviastringchats(String jsonstring) {
        Log.d("Ritik", "insertjsonviastring: recieved jsonstring is " + jsonstring);
        try {
            JSONArray jsonArray = new JSONArray(jsonstring);
            SQLiteDatabase db = this.getWritableDatabase();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                //values.put(KEY_ID, c.getString(KEY_ID));
                values.put(KEY_CHAT_UID, c.getString(KEY_CHAT_UID));
                values.put(KEY_SEQUENCE, c.getString(KEY_SEQUENCE));
                values.put(KEY_TYPING, c.getString(KEY_TYPING));
                values.put(KEY_SENDER_NAME, c.getString(KEY_SENDER_NAME));
                values.put(KEY_MESSAGE, c.getString(KEY_MESSAGE));
                values.put(KEY_COLOR, c.getString(KEY_COLOR));

                db.insert(TABLE_CHATS, null, values);
            }

            db.close(); // Closing database connection


        } catch (Exception e) {
            Log.d("Ritik", "Exception in jsonstringtodb  " + e.getMessage());
        }
    }


}