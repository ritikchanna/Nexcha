package ritik.nexcha;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by SuperUser on 09-06-2017.
 */

public class PrefsHelper {
    private String KEY_PREFS = "chat_history";

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


}