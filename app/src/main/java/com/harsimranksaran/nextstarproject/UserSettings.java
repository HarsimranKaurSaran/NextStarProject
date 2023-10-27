package com.harsimranksaran.nextstarproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSettings extends AppCompatActivity{

    EditText enterPass, reenterPass;
    Button confirmBtn, cancelBtn;

    EditText uname, user_fullname, uemail, ucontact, ucountry, udob, changepass, user_gender;

    FirebaseAuth mAuth;

    String currentUserID;
    StorageReference userProfilePic;
    DatabaseReference usersRef;

    ProgressDialog progressDialog;

    private static int REQUEST_CODE = 1;

    CircleImageView profileImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        enterPass = findViewById(R.id.enter_pass);
        reenterPass = findViewById(R.id.reenter_pass);
        confirmBtn = findViewById(R.id.confirm_password);
        cancelBtn = findViewById(R.id.cancel_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);
        userProfilePic = FirebaseStorage.getInstance().getReference().child("profileimages");

        uname = findViewById(R.id.username);
        user_fullname = findViewById(R.id.fill_name);
        uemail = findViewById(R.id.fill_email);
        ucontact = findViewById(R.id.fill_contact);
        ucountry = findViewById(R.id.fill_country);
        udob = findViewById(R.id.fill_dob);
        changepass = findViewById(R.id.fill_password);
        profileImg = findViewById(R.id.profile_pic);
        user_gender = findViewById(R.id.fill_gender);

        progressDialog = new ProgressDialog(this);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent();
                in.setAction(Intent.ACTION_GET_CONTENT);
                in.setType("image/*");
                startActivityForResult(in, REQUEST_CODE);
            }
        });

        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //changePassword();
            }
        });

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String image = dataSnapshot.child("profileimage").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    String fullname = dataSnapshot.child("fullname").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String contact = dataSnapshot.child("contact").getValue().toString();
                    String country = dataSnapshot.child("country").getValue().toString();
                    String dob = dataSnapshot.child("dob").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();

                    Glide.with(UserSettings.this)
                            .load(image)
                            .placeholder(R.drawable.ic_userimg)
                            .into(profileImg);


                    uname.setText(username);
                    user_fullname.setText(fullname);
                    uemail.setText(email);
                    ucontact.setText(contact);
                    ucountry.setText(country);
                    udob.setText(dob);
                    user_gender.setText(gender);

                }
            }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                });


    }

    private void changePassword() {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Change Password");
        View v=getLayoutInflater().inflate(R.layout.custom_changepass,null);

        builder.setView(v);
        final AlertDialog dialog=  builder.create();
        dialog.show();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UserSettings.this,"Password changed successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();

                StorageReference filePath = userProfilePic.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {

                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();

                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    usersRef.child("profileimage").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        Toast.makeText(UserSettings.this, "Profile image changed", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                    } else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(UserSettings.this, "Error..." + message, Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    }
                });
            }
            else {
                Toast.makeText(UserSettings.this, "Error...", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.option_save: {
                updateSettings();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSettings() {

        String u_name = uname.getText().toString();
        String full_name = user_fullname.getText().toString();
        String u_email = uemail.getText().toString();
        String u_contact = ucontact.getText().toString();
        String u_country = ucountry.getText().toString();
        String u_dob = udob.getText().toString();

        if (u_name.isEmpty() || full_name.isEmpty() || u_email.isEmpty() || u_contact.isEmpty()
                || u_country.isEmpty() || u_dob.isEmpty()){
            Toast.makeText(this, "Please enter all the fields.", Toast.LENGTH_SHORT).show();
        } else {
            HashMap usermap = new HashMap();
            usermap.put("fullname", full_name);
            usermap.put("username", u_name);
            usermap.put("contact", u_contact);
            usermap.put("email", u_email);
            usermap.put("dob", u_dob);
            usermap.put("country", u_country);

            usersRef.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(UserSettings.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserSettings.this, "Error occurred..."+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserSettings.this, "Error occurred..."+e, Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

}
