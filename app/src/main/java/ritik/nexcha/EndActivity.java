package ritik.nexcha;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

public class EndActivity extends Activity {
    ImageButton closebtn;
    Button restart, sharebtn;
    DatabaseHandler db;
    ArrayList<GridItem> suggestions;
    String chat_uid, current_category;
    RecyclerView recyclerview;
    GridAdapter adapter;
    Intent i;
    PrefsHelper prefshelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        db = new DatabaseHandler(this);
        prefshelper = new PrefsHelper();
        chat_uid = getIntent().getStringExtra("chat_uid");
        current_category = getIntent().getStringExtra("current_category");
        suggestions = db.getsuggestions(Integer.parseInt(chat_uid));
        closebtn = (ImageButton) findViewById(R.id.close_btn);
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                EndActivity.this.overridePendingTransition(R.anim.slide_down, R.anim.no_change);
            }
        });
        restart = (Button) findViewById(R.id.restart_btn);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startover = new Intent(EndActivity.this, ChatScreen.class);
                startover.putExtra("chat_uid", chat_uid);
                startover.putExtra("current_category", current_category);
                prefshelper.savechat_lastmsg(EndActivity.this, chat_uid, 0);
                startActivity(startover);
                finish();
            }
        });
        sharebtn = (Button) findViewById(R.id.share_btn);
        sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey !! Check out this story on the Nexcha www.nexcha.tk/app?" + chat_uid);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        i = new Intent(this, Story_cover.class);
        recyclerview = (RecyclerView) findViewById(R.id.suggestion_recyclerview);
        recyclerview.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerview.setLayoutManager(layoutManager);


        adapter = new GridAdapter(getApplicationContext(), suggestions);
        recyclerview.setAdapter(adapter);


        recyclerview.addOnItemTouchListener(
                new GridListner(this, new GridListner.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d("Ritik", "onItemClick: " + suggestions.get(position).getChat_uid());
                        i.putExtra("chat_uid", suggestions.get(position).getChat_uid());
                        i.putExtra("current_category", current_category);
                        startActivity(i);
                        finish();
                    }
                })
        );


    }

    @Override
    public void onBackPressed() {
        finish();
        EndActivity.this.overridePendingTransition(R.anim.slide_down, R.anim.no_change);
    }


}
