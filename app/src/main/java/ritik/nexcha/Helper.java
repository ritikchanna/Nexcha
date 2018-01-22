package ritik.nexcha;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONObject;

/**
 * Created by root on 20/1/18.
 */

public class Helper {
    public void refreshuser(final Context context) {

        final String user[] = {context.getString(R.string.guest), context.getString(R.string.tap_to_login), ""};

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(context);

        if (!(AccessToken.getCurrentAccessToken() == null)) {
            Log.d("Ritik", "facebook: ");
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            Profile profile = Profile.getCurrentProfile();


            Bundle params = new Bundle();
            params.putString("fields", "first_name,last_name,email,picture.type(large)");
            new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {

                            if (response != null) {
                                try {
                                    JSONObject data = response.getJSONObject();
                                    String user_fb[] = user;
                                    if (data.has("first_name"))
                                        user_fb[0] = data.getString("first_name");

                                    if (data.has("last_name"))
                                        user_fb[0] = user_fb[0] + " " + data.getString("last_name");
                                    if (data.has("email"))
                                        user_fb[1] = data.getString("email");
                                    if (data.has("picture"))
                                        user_fb[2] = data.getJSONObject("picture").getJSONObject("data").getString("url");

                                    new PrefsHelper().save_user(context, user_fb);

                                    //Bitmap profilePic= BitmapFactory.decodeStream(profilePicUrl .openConnection().getInputStream());
                                } catch (Exception e) {
                                    Log.d("Ritik", "Exception in facebook ");
                                    new PrefsHelper().save_user(context, user);
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).executeAsync();
        } else if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            Uri personPhoto = acct.getPhotoUrl();
            user[0] = personName;
            user[1] = personEmail;
            user[2] = personPhoto.toString();
            new PrefsHelper().save_user(context, user);
        }

    }
}
