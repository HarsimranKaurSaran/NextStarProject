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
import com.google.firebase.database.ValueEventListener;
import com.harsimranksaran.nextstarproject.CommentsActivity;
import com.harsimranksaran.nextstarproject.EditActivity;
import com.harsimranksaran.nextstarproject.R;
import com.harsimranksaran.nextstarproject.data.Posts;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Posts, PostsViewHolder>  adapter;
    DatabaseReference reference, likesRef, shareRef;
    TextView loading;

    FirebaseAuth mAuth;
    String currentUserID;

    Boolean likechecker = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.activity_homepage, container,false);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reference = FirebaseDatabase.getInstance()
                .getReference()
                .child("posts");

        mAuth = FirebaseAuth.getInstance();
        currentUserID= mAuth.getCurrentUser().getUid();

        likesRef = FirebaseDatabase.getInstance().getReference().child("likes");

        loading = view.findViewById(R.id.loading);

        recyclerView = view.findViewById(R.id.home_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        FirebaseRecyclerOptions<Posts> options =
                new FirebaseRecyclerOptions.Builder<Posts>()
                        .setQuery(reference, Posts.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recyclerview, viewGroup, false);
                return new PostsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull Posts model) {

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

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), EditActivity.class);
                        intent.putExtra("postkey", PostKey);
                        startActivity(intent);
                    }
                });

                holder.commentbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent commentintent = new Intent(getActivity(), CommentsActivity.class);
                        commentintent.putExtra("postkey", PostKey);
                        startActivity(commentintent);
                    }
                });

                holder.sharebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //sharedPosts();
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

                                    if (dataSnapshot.child(PostKey).hasChild(currentUserID)){
                                        likesRef.child(PostKey).child(currentUserID).removeValue();
                                        likechecker = false;
                                    } else {
                                        likesRef.child(PostKey).child(currentUserID).setValue(true);
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
        };

        recyclerView.setAdapter(adapter);
        loading.setVisibility(View.INVISIBLE);

    }

//    public void sharedPosts(){
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                String username = dataSnapshot.child("username").getValue().toString();
//                String image = dataSnapshot.child("profileimage").getValue().toString();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

    @Override
    public void onStart() {
        super.onStart();

        adapter.startListening();

    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        TextView textViewname, textViewdate, textViewtime, textViewdesc;
        CircleImageView imageView;
        ImageView imageViewshow;
        VideoView videoView;

        ImageView likebtn, commentbtn, sharebtn;
        TextView no_likes, no_comments, no_shares;

        int countlikes;
        DatabaseReference likeRef;
        String currentUser;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            likebtn = mView.findViewById(R.id.like);
            commentbtn = mView.findViewById(R.id.comment);
            sharebtn = mView.findViewById(R.id.share);
            no_likes = mView.findViewById(R.id.no_likes);
            no_comments = mView.findViewById(R.id.no_comments);
            no_shares = mView.findViewById(R.id.no_shares);

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

        public void setUsername(String username){
            textViewname = mView.findViewById(R.id.user_name);
            textViewname.setText(username);
        }

        public void setProfilepic(Context context, String profilepic){
            imageView = mView.findViewById(R.id.user_image);
            Glide.with(context).load(profilepic).placeholder(R.drawable.ic_userimg).into(imageView);
        }

        public void setDate(String date){
            textViewdate = mView.findViewById(R.id.user_date);
            textViewdate.setText(date);
        }

        public void setTime(String time){
            textViewtime = mView.findViewById(R.id.user_time);
            textViewtime.setText("  "+time);
        }

        public void setDescription(String description){
            textViewdesc = mView.findViewById(R.id.description);
            textViewdesc.setText(description);
        }

        public void setPostimage(Context context, String postimage){
            imageViewshow = mView.findViewById(R.id.showimage);
            if (postimage != null) {
                Glide.with(context).load(postimage).into(imageViewshow);
            } else {
                imageViewshow.setVisibility(View.INVISIBLE);
            }
        }

        public void setPostvideo(String postvideo){
            videoView = mView.findViewById(R.id.showvideo);
            if (postvideo != null) {
                Uri imgUri = Uri.parse(postvideo);
                videoView.setVideoURI(imgUri);
                videoView.requestFocus();
                videoView.start();
            } else {
                videoView.setVisibility(View.INVISIBLE);
            }
        }

    }

}
