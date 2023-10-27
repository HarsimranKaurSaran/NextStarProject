package com.harsimranksaran.nextstarproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadImg extends AppCompatActivity {

    ImageView img;
    EditText desc;
    Button upload, select;
    CircleImageView imageView;

    private static int REQUEST_CODE = 0;
    ProgressDialog progressDialog;

    Uri ImageUri;

    FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference postImagesReference;
    DatabaseReference usersRef, postsRef, profilepicRef;

    String caption, saveCurrentDate, saveCurrentTime, postRandomName, downloadURL, current_user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickimage);

        getSupportActionBar().setTitle("Upload Image");

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        postImagesReference = storage.getReference();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("posts");
        profilepicRef = FirebaseDatabase.getInstance().getReference().child("users").child(current_user_id);

        progressDialog = new ProgressDialog(this);

        img = findViewById(R.id.imgshow);
        desc = findViewById(R.id.caption);
        upload = findViewById(R.id.upload);
        select = findViewById(R.id.select);
        imageView = findViewById(R.id.user_img_upload);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePicker();
            }
        });

        profilepicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String image = dataSnapshot.child("profileimage").getValue().toString();

                    Glide.with(UploadImg.this)
                            .load(image)
                            .placeholder(R.drawable.ic_userimg)
                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void imagePicker() {
        Intent in=new Intent(Intent.ACTION_PICK);
        in.setType("image/*");
        startActivityForResult(in,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(UploadImg.this, "No image selected", Toast.LENGTH_SHORT).show();
                return;
            } else {
                try {
                    ImageUri = data.getData();
                    setImage(ImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setImage(Uri path) throws IOException {
        ParcelFileDescriptor descriptor = getContentResolver().openFileDescriptor(path,"r");
        FileDescriptor fileDescriptor = descriptor.getFileDescriptor();
        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        descriptor.close();
        img.setImageBitmap(bitmap);
    }

    private void uploadImage(){

        caption = desc.getText().toString().trim();
        if (caption.isEmpty()){
            Toast.makeText(UploadImg.this, "Please say something about your post.", Toast.LENGTH_SHORT).show();
        } else {

            progressDialog.setMessage("Posting...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            Calendar callForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("EEE, MMM d, ''yy");
            saveCurrentDate = currentDate.format(callForDate.getTime());

            Calendar callForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("h:mm a");
            saveCurrentTime = currentTime.format(callForTime.getTime());

            postRandomName = saveCurrentDate + saveCurrentTime;

            StorageReference filePath = postImagesReference.child("postsImages").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");
            filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                        Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadURL = uri.toString();
                                saveToDatabase();
                            }
                        });


                    } else {
                        Toast.makeText(UploadImg.this, "Error while posting..." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadImg.this, "Error occurred..." + e, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saveToDatabase() {

        usersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String usernm = dataSnapshot.child("username").getValue().toString();
                    String profile = dataSnapshot.child("profileimage").getValue().toString();

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", current_user_id);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", caption);
                    postsMap.put("postimage", downloadURL);
                    postsMap.put("profilepic", profile);
                    postsMap.put("username", usernm);

                    postsRef.child(current_user_id+postRandomName).updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                Toast.makeText(UploadImg.this, "Posted Successfully", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(UploadImg.this, "Error while posting..."+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadImg.this, "Error occurred..."+e, Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
