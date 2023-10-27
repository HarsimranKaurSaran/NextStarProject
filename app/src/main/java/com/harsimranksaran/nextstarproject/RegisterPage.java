package com.harsimranksaran.nextstarproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterPage extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener{

    RadioButton male, female;
    String mgender = "";
    EditText uname, ucontact, upassword, uemail;
    Button bsignup;
    TextView tvcancel;

    FirebaseAuth firebaseAuth;
    DatabaseReference usersRef;

    ProgressDialog progressDialog;
    String currentUserID;
    String username, password, contactnb, email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("wait a sec...");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth= FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        male.setOnCheckedChangeListener(this);
        female.setOnCheckedChangeListener(this);

        uname = findViewById(R.id.name);
        ucontact = findViewById(R.id.contact);
        upassword = findViewById(R.id.pass);
        uemail = findViewById(R.id.email);
       // utalent = findViewById(R.id.talent);
        bsignup = findViewById(R.id.register);
        tvcancel = findViewById(R.id.cancel);
        bsignup.setOnClickListener(this);
        tvcancel.setOnClickListener(this);

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.getId() == R.id.male){
            if (b){
                mgender = male.getText().toString();
            }
        } else if (compoundButton.getId() == R.id.female){
            if (b){
                mgender = female.getText().toString();
            }
        }
        //Toast.makeText(RegisterPage.this,"genderrrrrrrrrr"+mgender, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.register){
            signUp();

        } else if (view.getId() == R.id.cancel){

            Intent intent = new Intent(RegisterPage.this, LoginPage.class);
            startActivity(intent);
            finish();
        }
    }

    public void signUp(){

        username = uname.getText().toString();
        password = upassword.getText().toString();
        contactnb = ucontact.getText().toString();
        email = uemail.getText().toString();

        if (username.isEmpty() || password.isEmpty() || contactnb.isEmpty() || email.isEmpty() || mgender.isEmpty()){
            Toast.makeText(RegisterPage.this, "Please fill all the details.", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else {
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                currentUserID = task.getResult().getUser().getUid();
                                saveToDatabase();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterPage.this, "Something went wrong..." + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveToDatabase() {
        HashMap usermap = new HashMap();
        usermap.put("fullname", " ");
        usermap.put("username", username);
        usermap.put("password", password);
        usermap.put("contact", contactnb);
        usermap.put("gender", mgender);
        usermap.put("email", email);
        usermap.put("dob", " ");
        usermap.put("uid", currentUserID);
        usermap.put("country", " ");
        usermap.put("profileimage", " ");
        usersRef.child(currentUserID).updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterPage.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Intent intent = new Intent(RegisterPage.this, LoginPage.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterPage.this, "Something went wrong..."+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterPage.this, "Something went wrong..."+e, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

    }

}
