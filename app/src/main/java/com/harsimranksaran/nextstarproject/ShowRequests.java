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
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.harsimranksaran.nextstarproject.data.Requests;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowRequests extends AppCompatActivity {

    RecyclerView showRequestslist;

    DatabaseReference profileUserRef, usersRef, friendRequestRef;
    FirebaseAuth mAuth;
    String currentUserID, receiverUserID, currentstate;

    String userid="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showrequests);

        getSupportActionBar().setTitle("Requests");

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("friendrequests").child(currentUserID);

        showRequestslist = findViewById(R.id.requests_recyclerview);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        showRequestslist.setLayoutManager(manager);

        showrequests();

    }

    private void showrequests(){

        FirebaseRecyclerOptions<Requests> options =
                new FirebaseRecyclerOptions.Builder<Requests>()
                        .setQuery(friendRequestRef, Requests.class)
                        .build();

        FirebaseRecyclerAdapter<Requests, RequestsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Requests, RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull Requests model) {

                final String usersID = getRef(position).getKey();
                usersRef.child(usersID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){

                            final String username = dataSnapshot.child("username").getValue().toString();
                            final String profileimg = dataSnapshot.child("profileimage").getValue().toString();
                            userid = dataSnapshot.child("uid").getValue().toString();

                            holder.setUsername(username);
                            holder.setProfileimage(getApplicationContext(), profileimg);

                            holder.accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    holder.acceptFriendRequest(userid);
                                }
                            });

                            holder.decline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    holder.declinerequest(userid);
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_friendrequests, viewGroup, false);
                return new RequestsViewHolder(view);
            }
        };

        showRequestslist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Button accept, decline;
        DatabaseReference friendsRef, friendRequestRef;
        FirebaseAuth mAuth;
        String currentUserID, currentstate;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            accept = mView.findViewById(R.id.accept_request);
            decline = mView.findViewById(R.id.decline_request);
        }

        public void setUsername(String username){
            TextView textViewname = mView.findViewById(R.id.crusername);
            textViewname.setText(username);
        }

        public void setProfileimage(Context context, String profilepic){
            CircleImageView imageView = mView.findViewById(R.id.cruserimg);
            Glide.with(context).load(profilepic).into(imageView);
        }

        public void acceptFriendRequest(final String userid) {

            friendsRef = FirebaseDatabase.getInstance().getReference().child("friends");
            friendRequestRef = FirebaseDatabase.getInstance().getReference().child("friendrequests");
            mAuth = FirebaseAuth.getInstance();
            currentUserID = mAuth.getCurrentUser().getUid();

            friendsRef.child(currentUserID).child(userid).child("date").setValue("jan")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                friendsRef.child(userid).child(currentUserID).child("date").setValue("jan")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){

                                                    friendRequestRef.child(currentUserID).child(userid).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task){
                                                                friendRequestRef.child(userid).child(currentUserID).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    currentstate = "friends";
                                                                }
                                                            });
                                                                }
                                                            });

                                                }

                                            }
                                        });
                            }
                        }
                    });

        }

        public void declinerequest(final String userid){

            friendsRef = FirebaseDatabase.getInstance().getReference().child("friends");
            friendRequestRef = FirebaseDatabase.getInstance().getReference().child("friendrequests");
            mAuth = FirebaseAuth.getInstance();
            currentUserID = mAuth.getCurrentUser().getUid();

            friendRequestRef.child(currentUserID).child(userid).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                friendRequestRef.child(userid).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    currentstate = "not friends";
                                                }
                                            }
                                        });

                            }
                        }
                    });

        }

    }
}
