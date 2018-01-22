package ritik.nexcha;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by SuperUser on 09-06-2017.
 */

public class PrefsHelper {
    private String KEY_PREFS = "chat_history";
    private String KEY_PREFS_USER = "user";

    public int getchat_lastmsg(Context context, String chat_uid) {
        SharedPreferences sharedPreferences;
        int sequence;
        sharedPreferences = context.getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE);
        sequence = sharedPreferences.getInt(chat_uid, 0);
        return sequence;
    }

    public Map getsavedchats(Context context) {
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE);

        Map<String, ?> allEntries = sharedPreferences.getAll();
        return allEntries;

    }

    public void savechat_lastmsg(Context context, String chat_uid, int sequence) {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(chat_uid, sequence);
        editor.commit();
    }

    public void save_user(Context context, String[] user) {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(KEY_PREFS_USER, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("name", user[0]);
        editor.putString("email", user[1]);
        editor.putString("picture", user[2]);
        editor.commit();
    }

    public String[] get_user(Context context) {
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(KEY_PREFS_USER, Context.MODE_PRIVATE);
        String[] user = {sharedPreferences.getString("name", context.getString(R.string.guest)), sharedPreferences.getString("email", context.getString(R.string.tap_to_login)), sharedPreferences.getString("picture", "")};
        return user;
    }


}