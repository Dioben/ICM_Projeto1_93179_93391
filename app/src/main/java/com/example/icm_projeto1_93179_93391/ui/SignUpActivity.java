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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser usr;
    boolean created;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
    }

    public void signbutton_onClick(View view) {
        TextInputLayout name_box = findViewById(R.id.user_name_box);
        TextInputLayout email_box = findViewById(R.id.user_email_box);
        TextInputLayout pass_box = findViewById(R.id.user_pw_box);
        TextInputLayout pass_confirm_box = findViewById(R.id.user_pwc_box);
        name_box.setErrorEnabled(false);
        email_box.setErrorEnabled(false);
        pass_box.setErrorEnabled(false);
        pass_confirm_box.setErrorEnabled(false);

        TextInputEditText namebox = findViewById(R.id.user_name_input);
        String name = namebox.getText().toString().trim();
        if (created) {
            if (name.isEmpty()) {
                name_box.setError("Username field cannot be empty");
                name_box.setErrorEnabled(true);
                return;
            }
            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
            usr.updateProfile(request).addOnSuccessListener(
                    new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseQueryClient.getInstance().setUser(auth.getCurrentUser());
                            Intent start = new Intent(getApplication(),main_menu.class);
                            startActivity(start);
                        }
                    }
            ).addOnFailureListener(
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplication(),"Setting username failed. Please retry.",Toast.LENGTH_LONG).show();
                        }
                    }
            );
            return;
        }

        TextInputEditText pass = findViewById(R.id.user_pw_input);
        TextInputEditText pass2 = findViewById(R.id.input_pw_confirm);
        String pw = pass.getText().toString().trim();
        String pw2 = pass2.getText().toString().trim();
        if(! pw.equals(pw2)){
            pass_box.setError("Passwords do not match");
            pass_box.setErrorEnabled(true);
            pass_confirm_box.setError("Passwords do not match");
            pass_confirm_box.setErrorEnabled(true);
            return;
        }
        TextInputEditText email = findViewById(R.id.user_email_input);

        String mail = email.getText().toString().trim();


        if (mail.isEmpty() || pw.isEmpty() || pw2.isEmpty() || name.isEmpty()){
            if (mail.isEmpty()) {
                email_box.setError("Email field cannot be empty");
                email_box.setErrorEnabled(true);
            }
            if (pw.isEmpty()) {
                pass_box.setError("Password field cannot be empty");
                pass_box.setErrorEnabled(true);
            }
            if (pw2.isEmpty()) {
                pass_confirm_box.setError("Password confirmation field cannot be empty");
                pass_confirm_box.setErrorEnabled(true);
            }
            if (name.isEmpty()) {
                name_box.setError("Username field cannot be empty");
                name_box.setErrorEnabled(true);
            }
            return;
        }
        auth.createUserWithEmailAndPassword(mail,pw).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Sign-up failed.",Toast.LENGTH_LONG).show();
                if (!(e instanceof FirebaseAuthException)) return;
                //Toast.makeText(getApplicationContext(),e+" "+((FirebaseAuthException)e).getErrorCode(),Toast.LENGTH_LONG).show();

                if (e instanceof FirebaseAuthWeakPasswordException) {
                    if (((FirebaseAuthException) e).getErrorCode().equals("ERROR_WEAK_PASSWORD")) {
                        pass_box.setError("Password needs to be at least 6 characters long");
                        pass_box.setErrorEnabled(true);
                    }
                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    if (((FirebaseAuthException) e).getErrorCode().equals("ERROR_INVALID_EMAIL")) {
                        email_box.setError("The email address is badly formatted");
                        email_box.setErrorEnabled(true);
                    }
                } else if (e instanceof FirebaseAuthUserCollisionException) {
                    if (((FirebaseAuthException) e).getErrorCode().equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                        email_box.setError("Email already exists");
                        email_box.setErrorEnabled(true);
                    }
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                created=true;
                usr = authResult.getUser();
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                email_box.setEnabled(false);
                pass_box.setEnabled(false);
                pass_confirm_box.setEnabled(false);
                usr.updateProfile(request).addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseQueryClient.getInstance().setUser(auth.getCurrentUser());
                                Intent start = new Intent(getApplication(),main_menu.class);
                                startActivity(start);
                            }
                        }
                ).addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplication(),"Setting username failed. Please retry.",Toast.LENGTH_LONG).show();
                            }
                        }
                );
            }
        })
        ;

    }
}