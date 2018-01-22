package ritik.nexcha;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static ritik.nexcha.Constants.APP_VERSION;
import static ritik.nexcha.Constants.SERVER_URL;

public class SplashActivity extends AppCompatActivity {
    ImageView logo;

    Intent i;
    DatabaseHandler db;
    AlertDialog.Builder alertDialogbuilder;
    AlertDialog alertDialog;

    ConstraintLayout constraintLayout;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logo = findViewById(R.id.splash_imageView);

        constraintLayout = findViewById(R.id.splash_constraint);
        db = new DatabaseHandler(this);
        i = new Intent(SplashActivity.this, MainActivity.class);
        alertDialogbuilder = new AlertDialog.Builder(SplashActivity.this);
        alertDialogbuilder.setTitle("Update Available");
        alertDialogbuilder.setCancelable(false);
        alertDialog = alertDialogbuilder.create();

        new refresh_chats().execute("");

    }

    @Override
    public void onBackPressed() {

    }

    private class refresh_chats extends AsyncTask<String, String, String> {

        long time;
        // Snackbar snackbar=Snackbar.make(constraintLayout,"Offline ! Go Online to get new stories !!",Snackbar.LENGTH_INDEFINITE);


        @Override
        protected String doInBackground(String... params) {

            try {
                if (!isNetworkAvailable(SplashActivity.this)) {
                    Snackbar snackbar = Snackbar.make(constraintLayout, "Offline ! Go Online to get new stories !!", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                    Thread.sleep(500);
                } else {
                    OkHttpClient client = new OkHttpClient();

                    HttpUrl.Builder urlbuilder = HttpUrl.parse(SERVER_URL).newBuilder();
                    urlbuilder.addQueryParameter("op", "refresh");
                    urlbuilder.addQueryParameter("last", db.getLastStory());
                    String url_built = urlbuilder.build().toString();
                    Log.d("Ritik", "SplashScreen: url_built is " + url_built);
                    Request request = new Request.Builder()
                            .url(url_built)
                            .build();
                    Response response = client.newCall(request).execute();

                    db.insertjsonviastring(response.body().string());


                    //Update views of existing chats
                    urlbuilder = HttpUrl.parse(SERVER_URL).newBuilder();
                    urlbuilder.addQueryParameter("op", "getviews");
                    url_built = urlbuilder.build().toString();
                    Log.d("Ritik", "SplashScreen: url_built for getviews  is " + url_built);
                    request = new Request.Builder()
                            .url(url_built)
                            .build();
                    response = client.newCall(request).execute();
                    db.updateviews(response.body().string());


                    new Helper().refreshuser(SplashActivity.this);
                    urlbuilder = HttpUrl.parse(SERVER_URL).newBuilder();
                    urlbuilder.addQueryParameter("op", "getversion");
                    url_built = urlbuilder.build().toString();
                    Log.d("Ritik", "SplashScreen: url_built for getversion  is " + url_built);
                    request = new Request.Builder()
                            .url(url_built)
                            .build();
                    response = client.newCall(request).execute();
                    String latest_version = response.body().string();
                    Log.d("Ritik", "APP_VERSION " + APP_VERSION + "    fetched version   " + latest_version);
                    if (!latest_version.equals(APP_VERSION)) {
                        Log.d("Ritik", "doInBackground: " + APP_VERSION);
                        if (latest_version.charAt(1) == 'M') {


                            alertDialogbuilder.setMessage("Update the application in order to continue using the app !!");

                            alertDialogbuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                        finish();
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                        finish();
                                    }

                                }
                            });
                        } else if (latest_version.charAt(0) == 'O') {
                            alertDialogbuilder.setMessage("Do you want to update the app ?");

                            alertDialogbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                        finish();
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                        finish();
                                    }

                                }
                            });
                            alertDialogbuilder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    finish();
                                }
                            });

                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog = alertDialogbuilder.create();
                                alertDialog.show();
                            }
                        });

                    }


                }

                return "done";


            } catch (Exception e) {
                Log.d("Ritik", "Exception thrown in Asynctask of story_cover  " + e.getMessage());
            }
            return "error";
        }


        @Override
        protected void onPostExecute(String result) {
            Log.d("Ritik", "onPostExecute: +" + result);

            while (System.currentTimeMillis() < time + 3000) {
                try {
                    Log.d("Ritik", "Sleeping in postexecute");
                    Thread.sleep(100);
                } catch (Exception e) {
                }
            }
            if (!alertDialog.isShowing()) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }

        }


        @Override
        protected void onPreExecute() {
            time = System.currentTimeMillis();

        }


    }
}
