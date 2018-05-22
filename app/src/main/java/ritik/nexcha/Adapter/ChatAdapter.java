package ritik.nexcha.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;

import ritik.nexcha.DatabaseHandler;
import ritik.nexcha.Message;
import ritik.nexcha.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private final LayoutInflater inflater;
    //Creating an arraylist of POJO objects
    ArrayList<Message> messages = new ArrayList<>();
    DatabaseHandler db;
    View view;
    MyViewHolder holder;
    int chat_uid;
    public int sequence_last = 0;
    int lastPosition = 0;
    public Boolean endofchat = false;
    Boolean typing = false;
    private Context context;

    public ChatAdapter(Context context, int chat_uid) {
        this.context = context;
        db = new DatabaseHandler(context);
        this.chat_uid = chat_uid;
        inflater = LayoutInflater.from(context);
    }

    //This method inflates view present in the RecyclerView
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.chat_item, parent, false);
        holder = new MyViewHolder(view);
        return holder;
    }

    //Binding the data using get() method of POJO object
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.name.setText(message.getName());
        holder.name.setTextColor(Color.parseColor(message.getColor()));
        holder.content.setText(message.getMessage());
        if (position > lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context,
                    R.anim.fade_in);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }

    }

    //Setting the arraylist
    public void setListContent(ArrayList<Message> messages_new) {
        this.messages = messages_new;
        notifyItemRangeChanged(0, messages.size());

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void removelast() {
        messages.remove(messages.size() - 1);
        notifyItemRemoved(messages.size());

    }

    public void addmessage(Message newmessage) {
        messages.add(newmessage);
        notifyItemInserted(messages.size() - 1);
        sequence_last = newmessage.getSequence();
        if (newmessage.getTyping() < 0)
            endofchat = true;

    }

    public void nextmessage() {


        Message message = db.getnextmsg(chat_uid, sequence_last);
        addmessage(message);


    }

    //View holder class, where all view components are defined
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, content;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name = itemView.findViewById(R.id.user_name);
            content = itemView.findViewById(R.id.content);

        }

        @Override
        public void onClick(View v) {

        }
    }

}
