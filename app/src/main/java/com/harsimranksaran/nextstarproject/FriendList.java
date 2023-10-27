package com.harsimranksaran.nextstarproject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harsimranksaran.nextstarproject.data.Friends;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendList extends AppCompatActivity {

    RecyclerView myfriendlist;

    DatabaseReference friendsRef, usersRef;
    FirebaseAuth mAuth;
    String onlineUserID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_showfriendlist);
        getSupportActionBar().setTitle("My Friends");

        mAuth = FirebaseAuth.getInstance();
        onlineUserID = mAuth.getCurrentUser().getUid();
        friendsRef = FirebaseDatabase.getInstance().getReference().child("friends").child(onlineUserID);
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        myfriendlist = findViewById(R.id.friends_recyclerview);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        myfriendlist.setLayoutManager(manager);

        DisplayAllFriends();

    }

    private void DisplayAllFriends() {

        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(friendsRef, Friends.class)
                        .build();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Friends model) {

                final String usersID = getRef(position).getKey();
                usersRef.child(usersID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            final String username = dataSnapshot.child("username").getValue().toString();
                            final String profileimg = dataSnapshot.child("profileimage").getValue().toString();

                            holder.setUsername(username);
                            holder.setProfileimage(getApplicationContext(), profileimg);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_friendlist, viewGroup, false);
                return new FriendsViewHolder(view);
            }
        };

        myfriendlist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUsername(String username){
            TextView textViewname = mView.findViewById(R.id.friend_name);
            textViewname.setText(username);
        }

        public void setProfileimage(Context context, String profilepic){
            CircleImageView imageView = mView.findViewById(R.id.friend_img);
            Glide.with(context).load(profilepic).into(imageView);
        }
    }
}
