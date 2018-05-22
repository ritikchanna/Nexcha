package ritik.nexcha.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import ritik.nexcha.Adapter.ChatAdapter;
import ritik.nexcha.DatabaseHandler;
import ritik.nexcha.Message;
import ritik.nexcha.PrefsHelper;
import ritik.nexcha.R;

public class ChatScreen extends AppCompatActivity {
    RecyclerView recyclerView;
    Button nextbutton;
    ChatAdapter adapter;
    String current_category;
    Message temp_message;
    DatabaseHandler db;
    int chat_uid;
    PrefsHelper prefsHelper;
    Boolean endofchat = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        recyclerView = findViewById(R.id.chat_recycler_view);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        VerticalSpaceItemDecoration dividerItemDecoration = new VerticalSpaceItemDecoration(50);
        prefsHelper = new PrefsHelper();
        Log.d("Ritik", "onCreate: calling all saved chats");
        prefsHelper.getsavedchats(this);
        recyclerView.addItemDecoration(dividerItemDecoration);
        chat_uid = Integer.parseInt(getIntent().getStringExtra("chat_uid"));
        current_category = getIntent().getStringExtra("current_category");
        adapter = new ChatAdapter(this, chat_uid);
        loadprevmsges();
        nextbutton = findViewById(R.id.chat_next_button);
        recyclerView.setAdapter(adapter);
        db = new DatabaseHandler(this);
        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextbutton.setAlpha(.5f);
                nextbutton.setEnabled(false);
                if (!endofchat) {
                    temp_message = db.getnextmsg(chat_uid, adapter.sequence_last);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (temp_message.getTyping() > 0) {
                                final Message message = new Message();
                                message.setMessage(temp_message.getName() + " is typing ...");
                                message.setColor("#000000");
                                ChatScreen.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.addmessage(message);
                                        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                                    }
                                });
                                try {
                                    Thread.sleep(2000);
                                } catch (Exception e) {

                                }

                            }

                            ChatScreen.this.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    if (temp_message.getTyping() > 0)
                                        adapter.removelast();

                                    adapter.addmessage(temp_message);
                                    recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                                    if (temp_message.getTyping() >= 0) {
                                        nextbutton.setAlpha(1.0f);
                                        nextbutton.setEnabled(true);
                                    } else {
                                        if (!nextbutton.isEnabled()) {
                                            //nextbutton.setText("^");
                                            nextbutton.setAlpha(1.0f);
                                            nextbutton.setEnabled(true);
                                            endofchat = true;
                                        }

                                    }

                                }
                            });

                        }

                    }).start();
                } else {
                    Intent intent = new Intent(ChatScreen.this, EndActivity.class);
                    Log.d("Ritik", "on end chatscreen" + chat_uid + current_category);
                    intent.putExtra("chat_uid", String.valueOf(chat_uid));
                    intent.putExtra("current_category", current_category);
                    nextbutton.setAlpha(1.0f);
                    nextbutton.setEnabled(true);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (!adapter.endofchat)
            prefsHelper.savechat_lastmsg(this, String.valueOf(chat_uid), adapter.sequence_last);
        else
            prefsHelper.savechat_lastmsg(this, String.valueOf(chat_uid), 0);
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("current_category", current_category);


        startActivity(i);
        finish();
        finishAffinity();

    }


    public void loadprevmsges() {
        if (prefsHelper.getchat_lastmsg(ChatScreen.this, String.valueOf(chat_uid)) > 1) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChatScreen.this);
            alertDialog.setTitle("Resume Progress");
            alertDialog.setMessage("Do you want to continue from where you left ?");
//    alertDialog.setIcon(R.drawable.delete);
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    nextbutton.setAlpha(.5f);
                    nextbutton.setEnabled(false);
                    for (int i = 0; i < prefsHelper.getchat_lastmsg(ChatScreen.this, String.valueOf(chat_uid)); i++)
                        adapter.nextmessage();
                    nextbutton.setAlpha(1.0f);
                    nextbutton.setEnabled(true);

                }
            });

            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    adapter.nextmessage();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        } else {
            adapter.nextmessage();

        }


    }


    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;
        private Drawable mDivider;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
            mDivider = ContextCompat.getDrawable(ChatScreen.this, R.drawable.line_divider);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }
}



