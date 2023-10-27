package com.harsimranksaran.nextstarproject.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.harsimranksaran.nextstarproject.CommentsActivity;
import com.harsimranksaran.nextstarproject.R;
import com.harsimranksaran.nextstarproject.UserSettings;
import com.harsimranksaran.nextstarproject.data.Posts;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment{

    TextView userName;
    CircleImageView image;
    Button settings;

    RecyclerView myPostsList;

    DatabaseReference usersRef, postRef, likesRef;
    FirebaseAuth mAuth;
    String user;

    Boolean likechecker = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.activity_profile, container,false);
        return fragment;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userName = view.findViewById(R.id.username_tv);
        image = view.findViewById(R.id.profile_img);
        settings = view.findViewById(R.id.settings);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        postRef = FirebaseDatabase.getInstance().getReference().child("posts");
        likesRef = FirebaseDatabase.getInstance().getReference().child("likes");

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSetting();
            }
        });

        myPostsList = view.findViewById(R.id.profile_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        myPostsList.setLayoutManager(layoutManager);

        displayMyPosts();

    }

    private void displayMyPosts() {

        Query query = postRef.orderByChild("uid").startAt(user).endAt(user + "\uf8ff");

        FirebaseRecyclerOptions<Posts> options =
                new FirebaseRecyclerOptions.Builder<Posts>()
                        .setQuery(query, Posts.class)
                        .build();

        FirebaseRecyclerAdapter<Posts, MyPostsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Posts, MyPostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyPostsViewHolder holder, int position, @NonNull Posts model) {

                final String PostKey = getRef(position).getKey();

                holder.setUsername(model.getUsername());
                holder.setDate(model.getDate());
                holder.setTime(model.getTime());
                holder.setDescription(model.getDescription());
                holder.setProfilepic(getContext().getApplicationContext(), model.getProfilepic());

                String image = model.getPostimage();
                holder.setPostimage(getContext().getApplicationContext(), image);

                String video = model.getPostvideo();
                holder.setPostvideo(video);

                holder.setlikebuttonstatus(PostKey);

                holder.commentbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent commentintent = new Intent(getActivity(), CommentsActivity.class);
                        commentintent.putExtra("postkey", PostKey);
                        startActivity(commentintent);
                    }
                });

                holder.likebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likechecker = true;

                        likesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (likechecker.equals(true)){

                                    if (dataSnapshot.child(PostKey).hasChild(user)){
                                        likesRef.child(PostKey).child(user).removeValue();
                                        likechecker = false;
                                    } else {
                                        likesRef.child(PostKey).child(user).setValue(true);
                                        likechecker = false;
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

            }

            @NonNull
            @Override
            public MyPostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_myposts, viewGroup, false);
                return new MyPostsViewHolder(view);
            }
        };

        myPostsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static class MyPostsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        ImageView likebtn, commentbtn, sharebtn;
        TextView no_likes, no_shares;

        int countlikes;
        DatabaseReference likeRef;
        String currentUser;

        public MyPostsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            likebtn = mView.findViewById(R.id.like_post);
            commentbtn = mView.findViewById(R.id.comment_post);
            sharebtn = mView.findViewById(R.id.share_post);
            no_likes = mView.findViewById(R.id.no_likes_post);
            no_shares = mView.findViewById(R.id.no_shares_post);

            likeRef = FirebaseDatabase.getInstance().getReference().child("likes");
            currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        }

        public void setlikebuttonstatus(final String PostKey){

            likeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(PostKey).hasChild(currentUser)){
                        countlikes = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        likebtn.setImageResource(R.drawable.ic_likedo_empty);
                        no_likes.setText(Integer.toString(countlikes)+" likes");
                    } else {
                        countlikes = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        likebtn.setImageResource(R.drawable.ic_likedo);
                        no_likes.setText(Integer.toString(countlikes)+ " likes");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public void setPostimage(Context context, String postimage){
            ImageView imggrid = mView.findViewById(R.id.showimage_post);
            if (postimage != null) {
                Glide.with(context).load(postimage).into(imggrid);
            } else {
                imggrid.setVisibility(View.INVISIBLE);
            }

        }

        public void setPostvideo(String postvideo){
            VideoView videoView = mView.findViewById(R.id.showvideo_post);
            if (postvideo != null) {
                Uri imgUri = Uri.parse(postvideo);
                videoView.setVideoURI(imgUri);
                videoView.requestFocus();
                videoView.start();
            } else {
                videoView.setVisibility(View.INVISIBLE);
            }
        }

        public void setUsername(String username){
            TextView textViewname = mView.findViewById(R.id.user_name_post);
            textViewname.setText(username);
        }

        public void setProfilepic(Context context, String profilepic){
            CircleImageView imageView = mView.findViewById(R.id.user_image_post);
            Glide.with(context).load(profilepic).into(imageView);
        }

        public void setDate(String date){
            TextView textViewdate = mView.findViewById(R.id.user_date_post);
            textViewdate.setText(date);
        }

        public void setTime(String time){
            TextView textViewtime = mView.findViewById(R.id.user_time_post);
            textViewtime.setText("  "+time);
        }

        public void setDescription(String description){
            TextView textViewdesc = mView.findViewById(R.id.description_post);
            textViewdesc.setText(description);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        usersRef.child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue().toString();
                String profile = dataSnapshot.child("profileimage").getValue().toString();

                userName.setText(username);
                Glide.with(getContext())
                        .load(profile)
                        .placeholder(R.drawable.ic_userimg)
                        .into(image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void userSetting(){
        Intent intent = new Intent(getActivity(), UserSettings.class);
        startActivity(intent);
    }


}
