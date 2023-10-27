package com.harsimranksaran.nextstarproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersProfile extends AppCompatActivity {

    CircleImageView profileImg;
    TextView username, usercountry, userdob, usergender, useremail, userfullname;
    Button addFriend;

    DatabaseReference profileUserRef, usersRef, friendRequestRef, friendsRef;
    FirebaseAuth mAuth;
    String senderUserID, receiverUserID, currentstate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_usersprofile);
        getSupportActionBar().setTitle("User Profile");

        mAuth = FirebaseAuth.getInstance();
        senderUserID = mAuth.getCurrentUser().getUid();
        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("friendrequests");
        friendsRef = FirebaseDatabase.getInstance().getReference().child("friends");

        profileImg = findViewById(R.id.profile_photo);
        username = findViewById(R.id.user_name_tv);
        usercountry = findViewById(R.id.user_profile_country);
        userdob = findViewById(R.id.user_profile_dob);
        usergender = findViewById(R.id.user_profile_gender);
        useremail = findViewById(R.id.user_profile_email);
        userfullname = findViewById(R.id.user_profile_fullname);
        addFriend = findViewById(R.id.add_friend);
        currentstate = "not friends";

        usersRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String usname = dataSnapshot.child("username").getValue().toString();
                String profile = dataSnapshot.child("profileimage").getValue().toString();
                String country = dataSnapshot.child("country").getValue().toString();
                String gender = dataSnapshot.child("gender").getValue().toString();
                String dob = dataSnapshot.child("dob").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String fullname = dataSnapshot.child("fullname").getValue().toString();

                username.setText(usname);
                Glide.with(UsersProfile.this)
                        .load(profile)
                        .placeholder(R.drawable.ic_userimg)
                        .into(profileImg);
                userfullname.setText("Full name  :  " + fullname);
                usercountry.setText("Country  :  " + country);
                userdob.setText("Date Of Birth  :  " + dob);
                usergender.setText("Gender  :  " + gender);
                useremail.setText("Email  :  " + email);

                maintaincancelbutton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (!senderUserID.equals(receiverUserID)){

            addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentstate.equals("not friends")){

                        sendFriendRequest();
                    }
                    if (currentstate.equals("request_sent")){
                        cancelFriendRequest();
                    }

                }
            });

        } else {
//            Intent intent = new Intent(UsersProfile.this, ProfileFragment.class);
//            startActivity(intent);

        }
    }

    private void cancelFriendRequest() {

        friendRequestRef.child(senderUserID).child(receiverUserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            friendRequestRef.child(receiverUserID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                addFriend.setEnabled(true);
                                                currentstate = "not friends";
                                                addFriend.setText("Add Friend");
                                            }
                                        }
                                    });

                        }
                    }
                });

    }

    private void maintaincancelbutton() {

        friendRequestRef.child(senderUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(receiverUserID)){
                    String request_type = dataSnapshot.child(receiverUserID).child("request_type").toString();
                    if (request_type.equals("sent")){
                        currentstate = "request_sent";
                        addFriend.setText("cancel request");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendFriendRequest() {

        friendRequestRef.child(senderUserID).child(receiverUserID).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            friendRequestRef.child(receiverUserID).child(senderUserID).child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                addFriend.setEnabled(true);
                                                currentstate = "request_sent";
                                                addFriend.setText("cancel request");
                                            }
                                        }
                                    });

                        }
                    }
                });

    }
}
