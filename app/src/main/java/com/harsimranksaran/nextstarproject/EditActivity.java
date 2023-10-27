package com.harsimranksaran.nextstarproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditActivity extends AppCompatActivity {

    ImageView editImage;
    VideoView videoView;
    CircleImageView profileimg;
    EditText editcaption;
    Button editsave, editcancel;

    private String PostKey, currentUserID, databaseUserID;

    DatabaseReference clickPostRef;
    FirebaseAuth mAuth;

    String description, image, profile, video;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editimage);

        getSupportActionBar().setTitle("Post");

        PostKey = getIntent().getExtras().get("postkey").toString();

        editImage = findViewById(R.id.edit_img_show);
        videoView = findViewById(R.id.edit_vid_show);
        editcaption = findViewById(R.id.edit_caption);
        editsave = findViewById(R.id.edit_save);
        editcancel = findViewById(R.id.edit_cancel);
        profileimg = findViewById(R.id._user_img);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        clickPostRef = FirebaseDatabase.getInstance().getReference().child("posts").child(PostKey);

        editsave.setVisibility(View.INVISIBLE);
        editcancel.setVisibility(View.INVISIBLE);
        editImage.setVisibility(View.INVISIBLE);
        videoView.setVisibility(View.INVISIBLE);
        editcaption.setEnabled(false);

        clickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    description = dataSnapshot.child("description").getValue().toString();
                    if (dataSnapshot.child("postimage").exists()){
                        image = dataSnapshot.child("postimage").getValue().toString();
                        Glide.with(EditActivity.this).load(image).into(editImage);
                        editImage.setVisibility(View.VISIBLE);
                    }
                    if (dataSnapshot.child("postvideo").exists()){
                        video = dataSnapshot.child("postvideo").getValue().toString();
                        videoView.setVideoURI(Uri.parse(video));
                        //videoView.setVideoPath(video);
                        videoView.requestFocus();
                        videoView.start();
                        videoView.setVisibility(View.VISIBLE);
                    }
                   // image = dataSnapshot.child("postimage").getValue().toString();
                    profile = dataSnapshot.child("profilepic").getValue().toString();
                    databaseUserID = dataSnapshot.child("uid").getValue().toString();

                    editcaption.setText(description);
                    Glide.with(EditActivity.this).load(profile).placeholder(R.drawable.ic_userimg).into(profileimg);

                    if (currentUserID.equals(databaseUserID)) {
                        editsave.setVisibility(View.VISIBLE);
                        editcancel.setVisibility(View.VISIBLE);
                        editcaption.setEnabled(true);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        editsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCurrentPost();
            }
        });

        editcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void editCurrentPost() {

        clickPostRef.child("description").setValue(editcaption.getText().toString());
        Toast.makeText(this, "Post Updated successfully.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.option_delete: {
                deleteCurrentPost();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteCurrentPost() {

        clickPostRef.removeValue();
        Intent intent = new Intent(EditActivity.this, MainActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Post deleted successfully", Toast.LENGTH_SHORT).show();

    }


}
