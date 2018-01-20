package ritik.nexcha;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.charbgr.BlurNavigationDrawer.v7.BlurActionBarDrawerToggle;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static ritik.nexcha.Constants.SERVER_URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DatabaseHandler db;
    GridAdapter adapter;
    RecyclerView recyclerView;
    FloatingActionButton fab;
    String current_category;
    Intent i;
    private ArrayList<GridItem> story;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHandler(this);


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    refresh(SERVER_URL);
                } catch (Exception e) {
                    Log.d("Ritik", e.getStackTrace().toString());
                }
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (!drawer.isDrawerOpen(GravityCompat.START))
                    drawer.openDrawer(GravityCompat.START);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new BlurActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        i = new Intent(this, Story_cover.class);
        story = new ArrayList<GridItem>();
        initViews();

    }


    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        //recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);


        if (getIntent().hasExtra("current_category"))
            current_category = getIntent().getStringExtra("current_category");
        else
            current_category = "popular";
        Log.d("Ritik", "current category recieved : " + current_category);
//        story=db.getStoriesbygenre(current_category);

        adapter = new GridAdapter(getApplicationContext(), story);
        recyclerView.setAdapter(adapter);
        load_category(current_category);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == recyclerView.SCROLL_STATE_IDLE)
                    fab.show();
                else
                    fab.hide();
            }
        });
        recyclerView.addOnItemTouchListener(
                new GridListner(this, new GridListner.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d("Ritik", "onItemClick: " + story.get(position).getChat_uid());
                        i.putExtra("chat_uid", story.get(position).getChat_uid());
                        i.putExtra("title", story.get(position).getTitle());
                        i.putExtra("image_cover", story.get(position).getImage());
                        i.putExtra("author", story.get(position).getAuthor());
                        i.putExtra("views", story.get(position).getViews());
                        i.putExtra("episode", story.get(position).getEpisode());
                        i.putExtra("description", story.get(position).getViews());
                        i.putExtra("current_category", current_category);
                        startActivity(i);
                        finish();
                    }
                })
        );

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("");
            alertDialog.setMessage("Are you sure you want to exit ?");
//    alertDialog.setIcon(R.drawable.delete);
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();

                }
            });

            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            // Showing Alert Message
            alertDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_genre_mystery) {
            load_category("mystery");
        } else if (id == R.id.nav_genre_scifi) {
            load_category("sci-fi");
        } else if (id == R.id.nav_genre_comedy) {
            load_category("comedy");
        } else if (id == R.id.nav_genre_love) {
            load_category("love");
        } else if (id == R.id.nav_genre_drama) {
            load_category("drama");
        } else if (id == R.id.nav_genre_thriller) {
            load_category("thriller");
        } else if (id == R.id.nav_genre_horror) {
            load_category("horror");
        } else if (id == R.id.nav_genre_paranormal) {
            load_category("paranormal");
        } else if (id == R.id.nav_genre_fantasy) {
            load_category("fantasy");
        } else if (id == R.id.nav_popular) {
            load_category("popular");
        } else if (id == R.id.nav_reading) {
            load_category("reading");
        }

        return true;
    }

    void load_category(String category) {

        if (category.equals("sci-fi")) {
            story = db.getStoriesbygenre("sci-fi");
            fab.setImageResource(R.drawable.icon_sci_fi);
            current_category = "sci-fi";
        } else if (category.equals("comedy")) {
            story = db.getStoriesbygenre("comedy");
            fab.setImageResource(R.drawable.icon_comedy);
            current_category = "comedy";
        } else if (category.equals("love")) {
            story = db.getStoriesbygenre("love");
            fab.setImageResource(R.drawable.icon_love);
            current_category = "love";

        } else if (category.equals("drama")) {
            story = db.getStoriesbygenre("drama");
            fab.setImageResource(R.drawable.icon_drama);
            current_category = "drama";

        } else if (category.equals("fantasy")) {
            story = db.getStoriesbygenre("fantasy");
            fab.setImageResource(R.drawable.icon_fantasy);
            current_category = "fantasy";

        } else if (category.equals("mystery")) {
            story = db.getStoriesbygenre("mystery");
            fab.setImageResource(R.drawable.icon_mystery);
            current_category = "mystery";

        } else if (category.equals("paranormal")) {
            story = db.getStoriesbygenre("paranormal");
            fab.setImageResource(R.drawable.icon_paranormal);
            current_category = "paranormal";

        } else if (category.equals("thriller")) {
            story = db.getStoriesbygenre("thriller");
            fab.setImageResource(R.drawable.icon_thriller);
            current_category = "thriller";

        } else if (category.equals("horror")) {
            story = db.getStoriesbygenre("horror");
            fab.setImageResource(R.drawable.icon_horror);
            current_category = "horror";
        } else if (category.equals("popular")) {
            story = db.getStoriesbygenre("popular");
            fab.setImageResource(R.drawable.icon_popular);
            current_category = "popular";

        } else if (category.equals("reading")) {
            story = db.getStoriesbygenre("reading");
            fab.setImageResource(R.drawable.icon_reading);
            current_category = "reading";

        }
        //TODO Add categories like populor
        else {
            //TODO default genre here
            story = db.getStoriesbygenre("popular");
            fab.setImageResource(R.drawable.icon_popular);
            current_category = "popular";
        }
        adapter.clear();
        adapter.addAll(story);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    void refresh(String url) throws IOException {

        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlbuilder = HttpUrl.parse(url).newBuilder();
        urlbuilder.addQueryParameter("op", "refresh");
        urlbuilder.addQueryParameter("last", db.getLastStory());
        String url_built = urlbuilder.build().toString();
        Log.d("Ritik", "refresh: url_built is " + url_built);
        Request request = new Request.Builder()
                .url(url_built)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(swipeContainer, "Coudn't connect to server", Snackbar.LENGTH_LONG).show();
                        swipeContainer.setRefreshing(false);

                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();
                Log.d("Ritik", "onResponse: " + myResponse);
                db.insertjsonviastring(myResponse);
                final ArrayList<GridItem> temp = db.getStoriesbygenre(current_category);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.clear();
                        adapter.addAll(temp);
                        swipeContainer.setRefreshing(false);

                    }
                });

            }
        });
    }
}
