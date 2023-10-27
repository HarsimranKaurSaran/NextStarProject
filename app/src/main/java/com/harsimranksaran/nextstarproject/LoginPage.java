package com.harsimranksaran.nextstarproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity implements View.OnClickListener{

    EditText uemail, upass;
    Button login;
    TextView register;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        firebaseAuth=FirebaseAuth.getInstance();

        uemail = findViewById(R.id.et_username);
        upass = findViewById(R.id.et_password);
        login = findViewById(R.id.login);
        login.setOnClickListener(this);
        register = findViewById(R.id.tv_register);
        register.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login){

           dologin();

        } else if (view.getId() == R.id.tv_register){

            Intent intent = new Intent(LoginPage.this, RegisterPage.class);
            startActivity(intent);
        }
    }

    private void dologin()
    {
        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final String email = uemail.getText().toString();
        final String pass = upass.getText().toString();

        if (email.isEmpty() || pass.isEmpty()){
            Toast.makeText(LoginPage.this, "Username or password incorrect.", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }

        else {
            firebaseAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                               @Override
                                               public void onComplete(@NonNull Task<AuthResult> task) {
                                                   if (task.isSuccessful()) {
                                                       dialog.dismiss();
                                                       FirebaseUser user = task.getResult().getUser();

                                                       Toast.makeText(LoginPage.this, "Successfully Login.", Toast.LENGTH_SHORT).show();

                                                       Intent in = new Intent(LoginPage.this, MainActivity.class);
                                                       startActivity(in);
                                                       finish();

                                                   }

                                               }
                                           }
                    ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(LoginPage.this, "Something went wrong..." + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
