package com.harsimranksaran.nextstarproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.harsimranksaran.nextstarproject.FriendList;
import com.harsimranksaran.nextstarproject.R;
import com.harsimranksaran.nextstarproject.ShowRequests;
import com.harsimranksaran.nextstarproject.UsersProfile;
import com.harsimranksaran.nextstarproject.data.FindFriends;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchFragment extends Fragment{

    EditText search;
    ImageView searchbtn;
    RecyclerView searchlist;
    TextView requests, myfriends;

    DatabaseReference reference;
    FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder> firebaseRecyclerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.activity_search, container,false);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        search = view.findViewById(R.id.search_et);
        searchbtn = view.findViewById(R.id.search_imgbtn);
        searchlist = view.findViewById(R.id.search_recycler_view);
        requests = view.findViewById(R.id.text_see_requests);
        myfriends = view.findViewById(R.id.text_myfriends);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        searchlist.setLayoutManager(manager);

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = search.getText().toString();

                reference = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("users");

                Query query = reference.orderByChild("username").startAt(input).endAt(input + "\uf8ff");

                FirebaseRecyclerOptions<FindFriends> options =
                        new FirebaseRecyclerOptions.Builder<FindFriends>()
                                .setQuery(query, FindFriends.class)
                                .build();


                firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull FindFriends model) {

                        holder.textViewname.setText(model.getUsername());
                        Glide.with(getActivity()).load(model.getProfileimage()).into(holder.imageView);
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String visit_user_id = getRef(position).getKey();
                                Intent intent = new Intent(getActivity(), UsersProfile.class);
                                intent.putExtra("visit_user_id", visit_user_id);
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_findfriends, viewGroup, false);
                        return new FindFriendsViewHolder(view);
                    }
                };


                searchlist.setAdapter(firebaseRecyclerAdapter);
                firebaseRecyclerAdapter.startListening();
            }
        });

        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShowRequests.class);
                startActivity(intent);
            }
        });

        myfriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FriendList.class);
                startActivity(intent);
            }
        });

    }


    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;
        CircleImageView imageView;
        TextView textViewname;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            imageView = mView.findViewById(R.id.image_user);
            textViewname = mView.findViewById(R.id.name_user);

        }

    }

}
