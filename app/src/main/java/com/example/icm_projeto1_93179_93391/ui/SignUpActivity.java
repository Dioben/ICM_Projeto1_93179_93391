package com.example.icm_projeto1_93179_93391.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.icm_projeto1_93179_93391.R;
import com.example.icm_projeto1_93179_93391.network.FirebaseQueryClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
    }

    public void signbutton_onClick(View view) {
        TextInputEditText pass = findViewById(R.id.user_pw_input);
        TextInputEditText pass2 = findViewById(R.id.input_pw_confirm);
        String pw = pass.getText().toString().trim();
        String pw2 = pass2.getText().toString().trim();
        if(! pw.equals(pw2)){
            Toast.makeText(this,"Passwords do not match",Toast.LENGTH_LONG).show();
            return;
        }
        TextInputEditText email = findViewById(R.id.user_email_input);
        TextInputEditText namebox = findViewById(R.id.user_name_input);

        String mail = email.getText().toString().trim();
        String name = namebox.getText().toString().trim();

        if (mail.isEmpty() || pw.isEmpty()){
            Toast.makeText(this,"email and password must not be empty",Toast.LENGTH_LONG).show();
            return;
        }
        auth.createUserWithEmailAndPassword(mail,pw).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Account creation failed",Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser usr = authResult.getUser();
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                usr.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseQueryClient.getInstance().setUser(auth.getCurrentUser());
                        Intent start = new Intent(getApplication(),main_menu.class);
                        startActivity(start);
                    }
                });
            }
        })
        ;

    }
}