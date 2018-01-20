package ritik.nexcha;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static ritik.nexcha.Constants.SERVER_URL;

public class Story_cover extends AppCompatActivity {
    TextView textView_title, textView_views, textView_episodes, textView_description, textView_author;
    ImageView story_cover, eye_icon, arrow;

    DatabaseHandler db;
    String chat_uid;
    GridItem story;
    Boolean storyIndexed = false;
    RelativeLayout progressBar;


    AlertDialog.Builder alertbuilder;
    Intent toChat;
    String current_category;
    Intent i;
    OkHttpClient client;
    Gesture_Detector android_gesture_detector;
    private GestureDetector mGestureDetector;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_cover);
        db = new DatabaseHandler(this);
        i = new Intent(this, MainActivity.class);
        onNewIntent(getIntent());
        alertbuilder = new AlertDialog.Builder(this);
        storyIndexed = db.Ischatindexed(Integer.parseInt(chat_uid));
        progressBar = (RelativeLayout) findViewById(R.id.layoutProgressBar);
        android_gesture_detector = new Gesture_Detector();
        mGestureDetector = new GestureDetector(this, android_gesture_detector);
//        progressBar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        progressBar.setClickable(false);
        eye_icon = (ImageView) findViewById(R.id.storyReadCountImg);
        arrow = (ImageView) findViewById(R.id.imageButton);
        textView_title = (TextView) findViewById(R.id.textview_title);
        story_cover = (ImageView) findViewById(R.id.storyCover_imageview);
        textView_author = (TextView) findViewById(R.id.textview_author);
        textView_description = (TextView) findViewById(R.id.textview_description);
        textView_episodes = (TextView) findViewById(R.id.textview_episodes);
        textView_views = (TextView) findViewById(R.id.textview_views);
        alertbuilder.setMessage("Couldn't find the story you were looking for !!")
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        i.putExtra("current_category", current_category);
                        startActivity(i);
                        finish();
                    }
                });
        if (!storyIndexed) {
            if (!isNetworkAvailable(Story_cover.this)) {
                alertbuilder.create().show();
                progressBar.setVisibility(View.INVISIBLE);
            }
            eye_icon.setVisibility(View.INVISIBLE);
            arrow.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            client = new OkHttpClient();
            HttpUrl.Builder urlbuilder = HttpUrl.parse(Constants.SERVER_URL).newBuilder();
            urlbuilder.addQueryParameter("op", "refresh");
            urlbuilder.addQueryParameter("last", db.getLastStory());
            String url_built = urlbuilder.build().toString();
            Log.d("Ritik", "refresh: url_built is " + url_built);

//            alertbuilder.setMessage("Couldn't find the story you were looking for !!")
//                    .setCancelable(false)
//                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//
//                            i.putExtra("current_category", current_category);
//                            startActivity(i);
//                            finish();
//                        }
//                    });

            Request request = new Request.Builder()
                    .url(url_built)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertbuilder.create().show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });


                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    db.insertjsonviastring(response.body().string());
                    storyIndexed = db.Ischatindexed(Integer.parseInt(chat_uid));

                    if (storyIndexed) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displaycover();
                                progressBar.setVisibility(View.INVISIBLE);
                                eye_icon.setVisibility(View.VISIBLE);
                                arrow.setVisibility(View.VISIBLE);
                                Gesture_Detector android_gesture_detector = new Gesture_Detector();
                                mGestureDetector = new GestureDetector(Story_cover.this, android_gesture_detector);

                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.INVISIBLE);
                                alertbuilder.create().show();
                            }
                        });

                    }
                }
            });

        } else {
            displaycover();
        }

        toChat = new Intent(this, ChatScreen.class);
        toChat.putExtra("chat_uid", chat_uid);

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                toChat.putExtra("current_category", current_category);
                startActivity(toChat);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                finish();

            }

        });
        arrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
        // Return true if you have consumed the event, false if you haven't.
        // The default implementation always returns false.
    }

    @Override
    public void onBackPressed() {

        i.putExtra("current_category", current_category);
        startActivity(i);
        finish();

    }

    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            try {
                chat_uid = data.substring(data.lastIndexOf("?") + 1);
            } catch (Exception e) {
                chat_uid = "0";
            }
            current_category = "popular";
        } else {
            chat_uid = intent.getStringExtra("chat_uid");
            current_category = intent.getStringExtra("current_category");
        }
    }

    public void displaycover() {
        db.IncView(chat_uid);
        story = db.getStory(Integer.parseInt(chat_uid));
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();

            }
        });

        builder.build().load(story.getImage())
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .rotate(0)
                .into(story_cover);
        Log.d("Ritik", "onCreate: uview is " + story.getUview());
        textView_title.setText(story.getTitle());
        textView_views.setText(story.getViews());
        textView_episodes.setText("Ep." + chat_uid.substring(chat_uid.length() - 1) + " of " + db.getEpisodecount(chat_uid));
        textView_description.setText(story.getDescription());
        textView_author.setText("by " + story.getAuthor());


        Log.d("Ritik", "Checking if chatexist: " + chat_uid + " " + db.IsChatexist(Integer.parseInt(chat_uid)));

        fetch_task fetch = new fetch_task();
        fetch.execute();
    }

    private class fetch_task extends AsyncTask<String, String, String> {


//        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {

            try {
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS)
                        .writeTimeout(2, TimeUnit.SECONDS)
                        .readTimeout(2, TimeUnit.SECONDS)
                        .build();

                HttpUrl.Builder urlbuilder = HttpUrl.parse(SERVER_URL).newBuilder();
                urlbuilder.addQueryParameter("op", "update_view");
                urlbuilder.addQueryParameter("chat_uid", String.valueOf(chat_uid));
                urlbuilder.addQueryParameter("inc_view", story.getUview());
                String url_built = urlbuilder.build().toString();
                Log.d("Ritik", "view_inc: url_built is " + url_built);
                Request request = new Request.Builder()
                        .url(url_built)
                        .build();
                Response response;
                if (isNetworkAvailable(Story_cover.this)) {
                    response = client.newCall(request).execute();
                    String respons = response.body().string();
                    Log.d("Ritik", "doInBackground: response uview update is " + respons);

                    if (respons.split("v")[0].equals(story.getUview())) {
                        db.ResetView(story.getChat_uid(), Integer.parseInt(respons.split("v")[1]));
                        Log.d("Ritik", "uview reset: ");
                    }
                }
                Log.d("Ritik", "doInBackground:");


                if (!db.IsChatexist(Integer.parseInt(chat_uid))) {
                    if (!isNetworkAvailable(Story_cover.this)) {
                        alertbuilder.setMessage("You need an active internet connection to download this episode");
                        alertbuilder.create().show();
                    }

                    OkHttpClient client2 = new OkHttpClient();
                    urlbuilder = HttpUrl.parse(SERVER_URL).newBuilder();
                    urlbuilder.addQueryParameter("op", "fetch_chat");
                    urlbuilder.addQueryParameter("chat_uid", String.valueOf(chat_uid));
                    url_built = urlbuilder.build().toString();
                    Log.d("Ritik", "chat_fetch: url_built is " + url_built);
                    request = new Request.Builder()
                            .url(url_built)
                            .build();
                    response = client2.newCall(request).execute();

                    db.insertjsonviastringchats(response.body().string());
                }
                return "done";


            } catch (Exception e) {
                Log.d("Ritik", "Exception thrown in Asynctask of story_cover  " + e.getMessage());
            }
            return "error";
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
//            progressDialog.dismiss();
            progressBar.setVisibility(View.INVISIBLE);
            Log.d("Ritik", "onPostExecute: chat exist: " + db.IsChatexist(Integer.parseInt(chat_uid)));
            if (!db.IsChatexist(Integer.parseInt(chat_uid))) {
                AlertDialog alert = alertbuilder.create();
                alert.setTitle("Network error");
                alert.show();
            }

        }


        @Override
        protected void onPreExecute() {
            alertbuilder.setMessage("Please check your internet and try again !! ")
                    .setCancelable(false)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            i.putExtra("current_category", current_category);
                            startActivity(i);
                            finish();
                        }
                    });
//            progressDialog = ProgressDialog.show(Story_cover.this,
//                    "Please Wait..",
//
//          "Fetching the Awesome");
            if (!db.IsChatexist(Integer.parseInt(chat_uid)))
                progressBar.setVisibility(View.VISIBLE);
        }


    }

    class Gesture_Detector implements GestureDetector.OnGestureListener,
            GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d("Gesture ", " onDown");
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d("Gesture ", " onSingleTapConfirmed");
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d("Gesture ", " onSingleTapUp");
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.d("Gesture ", " onShowPress");
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d("Gesture ", " onDoubleTap");
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.d("Gesture ", " onDoubleTapEvent");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d("Gesture ", " onLongPress");
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            Log.d("Gesture ", " onScroll");
            if (e1.getY() < e2.getY()) {
                Log.d("Gesture ", " Scroll Down");
            }
            if (e1.getY() > e2.getY()) {
                Log.d("Gesture ", " Scroll Up");
                toChat.putExtra("current_category", current_category);
                startActivity(toChat);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                finish();
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() < e2.getX()) {
                Log.d("Gesture ", "Left to Right swipe: " + e1.getX() + " - " + e2.getX());
                Log.d("Speed ", String.valueOf(velocityX) + " pixels/second");
            }
            if (e1.getX() > e2.getX()) {
                Log.d("Gesture ", "Right to Left swipe: " + e1.getX() + " - " + e2.getX());
                Log.d("Speed ", String.valueOf(velocityX) + " pixels/second");
            }
            if (e1.getY() < e2.getY()) {
                Log.d("Gesture ", "Up to Down swipe: " + e1.getX() + " - " + e2.getX());
                Log.d("Speed ", String.valueOf(velocityY) + " pixels/second");
            }
            if (e1.getY() > e2.getY()) {
                Log.d("Gesture ", "Down to Up swipe: " + e1.getX() + " - " + e2.getX());
                Log.d("Speed ", String.valueOf(velocityY) + " pixels/second");
                toChat.putExtra("current_category", current_category);
                startActivity(toChat);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                finish();
            }
            return true;

        }

    }
}

