package ritik.nexcha.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ritik.nexcha.Constants;
import ritik.nexcha.GridItem;
import ritik.nexcha.R;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {
    DisplayMetrics displayMetrics;
    float dpWidth;
    private ArrayList<GridItem> story;
    private Context context;


    public GridAdapter(Context context, ArrayList<GridItem> story) {
        this.story = story;
        this.context = context;

        displayMetrics = context.getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;
    }

    @Override
    public GridAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GridAdapter.ViewHolder viewHolder, int i) {

        viewHolder.story_title.setText(story.get(i).getTitle());
        viewHolder.story_author.setText("by " + story.get(i).getAuthor());
        Log.d("Ritik", "onBindViewHolder: " + story.get(i).getTitle() + story.get(i).getAuthor());
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();

            }
        });
        Log.d("Ritik", "onBindViewHolder: " + viewHolder.story_cover.getWidth());
        builder.build().load(Constants.IMAGES_URL + story.get(i).getImage())
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .resize((int) dpWidth / 2, (int) dpWidth / 2)
                .rotate(0)
                .into(viewHolder.story_cover);


    }


    @Override
    public int getItemCount() {
        return story.size();
    }

    public void clear() {
        story.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(ArrayList<GridItem> arrayList) {
        story.addAll(arrayList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView story_title, story_author;
        private ImageView story_cover;

        // private LinearLayout story_item;
        public ViewHolder(View view) {
            super(view);
            //story_item=(LinearLayout)view.findViewById(R.id.grid_item);
            story_title = view.findViewById(R.id.grid_item_text);
            story_cover = view.findViewById(R.id.grid_item_image);
            story_author = view.findViewById(R.id.grid_item_author);
        }

    }

}