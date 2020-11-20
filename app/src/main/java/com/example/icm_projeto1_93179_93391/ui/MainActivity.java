package com.example.icm_projeto1_93179_93391.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.icm_projeto1_93179_93391.R;
import com.example.icm_projeto1_93179_93391.network.FirebaseQueryClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN =1 ;
    private FirebaseAuth auth;
    boolean googlecreated;
    FirebaseUser usr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        findViewById(R.id.googlesign).setOnClickListener(this::googlesign_onClick);
    }

    @Override
    protected void onStart() {
        //auth.signOut();//DEBUG PURPOSES PLEASE REMOVE
        FirebaseUser usr = auth.getCurrentUser();

        if (usr!=null){
            FirebaseQueryClient.getInstance().setUser(usr);
            Intent skip = new Intent(this,main_menu.class);
            startActivity(skip);
        }
        super.onStart();
    }

    public void signbutton_onClick(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void logbutton_onClick(View view) {
        TextInputLayout email_box = findViewById(R.id.user_email_box);
        TextInputLayout pass_box = findViewById(R.id.user_pw_box);
        email_box.setErrorEnabled(false);
        pass_box.setErrorEnabled(false);
        TextInputEditText email = findViewById(R.id.user_email_input);
        TextInputEditText pass = findViewById(R.id.user_pw_input);
        String mail = email.getText().toString().trim();
        String pw = pass.getText().toString().trim();
        if (mail.isEmpty() || pw.isEmpty()){
            if (mail.isEmpty()) {
                email_box.setError("Email field cannot be empty");
                email_box.setErrorEnabled(true);
            }
            if (pw.isEmpty()) {
                pass_box.setError("Password field cannot be empty");
                pass_box.setErrorEnabled(true);
            }
            return;
        }
        auth.signInWithEmailAndPassword(mail,pw).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //Toast.makeText(getApplication(),"Logged in",Toast.LENGTH_SHORT).show();
                FirebaseQueryClient.getInstance().setUser(authResult.getUser());
                Intent start = new Intent(getApplication(),main_menu.class);
                startActivity(start);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Login failed.",Toast.LENGTH_LONG).show();
                if (!(e instanceof  FirebaseAuthException)) return;
                //Toast.makeText(getApplicationContext(),e+" "+((FirebaseAuthException)e).getErrorCode(),Toast.LENGTH_LONG).show();

                if (e instanceof FirebaseAuthInvalidUserException) {
                    if (((FirebaseAuthException) e).getErrorCode().equals("ERROR_USER_NOT_FOUND")) {
                        email_box.setError("No account with this email exists");
                        email_box.setErrorEnabled(true);
                    }
                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    if (((FirebaseAuthException) e).getErrorCode().equals("ERROR_INVALID_EMAIL")) {
                        email_box.setError("The email address is badly formatted");
                        email_box.setErrorEnabled(true);
                    }
                    if (((FirebaseAuthException) e).getErrorCode().equals("ERROR_WRONG_PASSWORD")) {
                        pass_box.setError("Incorrect password");
                        pass_box.setErrorEnabled(true);
                    }
                }

            }
        });
    }

    public void googlesign_onClick(View view) {
        if (googlecreated){expandUI(); return;}
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                auth.signInWithCredential(credential).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Google and our Servers failed to cooperate.", Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if(!authResult.getAdditionalUserInfo().isNewUser()){
                            //Toast.makeText(getApplication(),"Signed in with Google",Toast.LENGTH_SHORT).show();
                            FirebaseQueryClient.getInstance().setUser(authResult.getUser());
                            Intent main = new Intent(getApplication(),main_menu.class);
                            startActivity(main);
                            return;
                        }
                        usr = auth.getCurrentUser();
                        expandUI();
                    }
                });
            } catch (ApiException e) {
                Toast.makeText(this,"Google sign in failed."+e,Toast.LENGTH_LONG).show();
            }
        }
    }

    public void expandUI(){
        googlecreated=true;
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_username_popup, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );
        TextInputEditText namebox = popupView.findViewById(R.id.user_name_input);
        TextInputLayout name_box = popupView.findViewById(R.id.user_name_box);

        Button confirm_upload_button = (Button) popupView.findViewById(R.id.confirm_username_button);
        confirm_upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = namebox.getText().toString().trim();
                if (name.isEmpty()) {
                    name_box.setError("Username field cannot be empty");
                    name_box.setErrorEnabled(true);
                    return;
                }
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                usr.updateProfile(request).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplication(),"Username setting failed. Please retry.",Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseQueryClient.getInstance().setUser(auth.getCurrentUser());
                        Intent start = new Intent(getApplication(),main_menu.class);
                        startActivity(start);
                    }
                });
            }
        });

        popupWindow.setElevation(20);
        popupWindow.showAtLocation(findViewById(R.id.login_layout), Gravity.CENTER, 0, 0);
    }
}