package com.harsimranksaran.nextstarproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.harsimranksaran.nextstarproject.data.Comment;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    EditText commentsInput;
    ImageButton sendbtn;
    RecyclerView recyclerView;
    String Post_Key;

    DatabaseReference userRef, postRef;
    FirebaseAuth mAuth;

    String currentUserID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        getSupportActionBar().setTitle("Comments");

        Post_Key = getIntent().getExtras().get("postkey").toString();

        commentsInput = findViewById(R.id.et_comment);
        sendbtn = findViewById(R.id.send);
        recyclerView = findViewById(R.id.comments_recyclerview);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        postRef = FirebaseDatabase.getInstance().getReference().child("posts").child(Post_Key).child("comments");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String username = dataSnapshot.child("username").getValue().toString();
                            String image = dataSnapshot.child("profileimage").getValue().toString();
                            validateComment(username, image);
                            commentsInput.setText("");
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Comment> options =
                new FirebaseRecyclerOptions.Builder<Comment>()
                        .setQuery(postRef, Comment.class)
                        .build();

        FirebaseRecyclerAdapter<Comment, CommentsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Comment, CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull Comment model) {

                holder.myUsername.setText(model.getUsername());
                holder.mycomment.setText(model.getComment());
                Glide.with(CommentsActivity.this).load(model.getUserimage()).into(holder.usershowimg);

            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_comments, viewGroup, false);
                return new CommentsViewHolder(view);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView myUsername, mycomment;
        CircleImageView usershowimg;


        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            myUsername = mView.findViewById(R.id.comment_username);
            mycomment = mView.findViewById(R.id.user_comment);
            usershowimg = mView.findViewById(R.id.comment_userimg);

        }

    }

    private void validateComment(String username, String image) {

        String comment = commentsInput.getText().toString();
        if (comment.isEmpty()){
            Toast.makeText(this, "Please write a comment...", Toast.LENGTH_SHORT).show();
        } else {
            final String randomKey = currentUserID + "usercomment";

            HashMap commentMap = new HashMap();
            commentMap.put("uid", currentUserID);
            commentMap.put("username", username);
            commentMap.put("comment", comment);
            commentMap.put("userimage", image);

            postRef.child(randomKey).updateChildren(commentMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(CommentsActivity.this, "Comment posted.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CommentsActivity.this, "Error while posting comment ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
