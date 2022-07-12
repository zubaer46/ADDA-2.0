package net.lebdevelopers.firebaseapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.lebdevelopers.firebaseapp.MessageActivity;
import net.lebdevelopers.firebaseapp.Model.Chat;
import net.lebdevelopers.firebaseapp.Model.Users;
import net.lebdevelopers.firebaseapp.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<Users> mUsers;
    private boolean isChat;
    String thelastMessage;


    // Constructor
    public UserAdapter(Context context, List<Users> mUsers, boolean isChat) {
        this.context = context;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,
                parent,
                false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Users users = mUsers.get(position);
        holder.username.setText(users.getUsername());

        if (users.getImageURL().equals("default")){
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }else{
            // Adding Glide Library
            Glide.with(context)
                    .load(users.getImageURL())
                    .into(holder.imageView);
        }
        if(isChat){
            lastMessage(users.getId(), holder.last_msg);
        }else{
            holder.last_msg.setVisibility(View.GONE);
        }

        // Status check
        if (isChat){

            if(users.getStatus().equals("online")){
                holder.imageViewON.setVisibility(View.VISIBLE);
                holder.imageViewOFF.setVisibility(View.GONE);
            }else{

                holder.imageViewON.setVisibility(View.GONE);
                holder.imageViewOFF.setVisibility(View.VISIBLE);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MessageActivity.class);
                i.putExtra("userid", users.getId());
                context.startActivity(i);

            }
        });



    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView imageView;
        public ImageView imageViewON;
        public ImageView imageViewOFF;
        public TextView last_msg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.textView30);
            imageView = itemView.findViewById(R.id.imageView);
            imageViewON = itemView.findViewById(R.id.statusimageON);
            imageViewOFF = itemView.findViewById(R.id.statusimageOFF);
            last_msg = itemView.findViewById(R.id.last_msg);

        }
    }


    //Check for last msg
    private void lastMessage(String userid, TextView last_msg){
        thelastMessage = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");


        reference.addValueEventListener(new ValueEventListener(){
            @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot){
              for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                  Chat chat = snapshot.getValue(Chat.class);
                  if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                          chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){
                      thelastMessage = chat.getMessage();


                  }
              }
              switch (thelastMessage){
                  case "default":
                      last_msg.setText("No Message");
                      break;
                  default:
                      last_msg.setText(thelastMessage);
                      break;
              }
              thelastMessage = "default";
           }
           @Override
            public void onCancelled(@NonNull DatabaseError databseError){

           }
        });

    }


}
