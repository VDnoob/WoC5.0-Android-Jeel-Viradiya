package com.example.unifier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;

import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class StartPageActivity extends AppCompatActivity {

    private TextView signup, txtNoAccnt;
    private EditText editEmail, editPassword;
    private Button login;
    private CircleImageView google;
    private LinearLayout ly;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private FirebaseAuth auth;
    private DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start_page);

        signup = findViewById(R.id.btnSignUp);
        txtNoAccnt = findViewById(R.id.txtNoAccount);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        login = findViewById(R.id.btnLogin);
        ly = findViewById(R.id.linear_layout);
        google = findViewById(R.id.iconGoogle);

        signup.setOnClickListener(v -> {
            startActivity(new Intent(StartPageActivity.this, SignUpActivity.class));
            finish();
        });

        login.setOnClickListener(v -> {
            String txtEmail = editEmail.getText().toString();
            String txtPassword = editPassword.getText().toString();

            if (TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)) {
                Toast.makeText(this, "Empty Credentials", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(txtEmail, txtPassword);
            }
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            startActivity(new Intent(StartPageActivity.this,MainActivity.class));
        }

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private void loginUser(String Email, String Password) {
        auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(StartPageActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(StartPageActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StartPageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && data != null) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            if(task.isSuccessful()) {
                Toast.makeText(this, "Sign In Successfully", Toast.LENGTH_SHORT).show();
                try {

                    GoogleSignInAccount gsa = task.getResult(ApiException.class);

                    if (gsa != null) {
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(gsa.getIdToken(), null);
                        auth.signInWithCredential(authCredential).addOnCompleteListener(this, task1 -> {
                            if (task1.isSuccessful()) {
                                FirebaseUser currentUser = auth.getCurrentUser();
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("name", gsa.getDisplayName().toString());
                                map.put("email", gsa.getEmail().toString());
                                map.put("password", "");
                                map.put("id", auth.getCurrentUser().getUid());
                                map.put("bio", "");
                                map.put("imageURL", "");

                                ref.child("UserG").child(auth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task1) {

                                        if (task1.isSuccessful()) {

                                            Toast.makeText(StartPageActivity.this, "Registered Successfully with google", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(StartPageActivity.this, "Update your profile for better experience", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(StartPageActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);

                                        } else {
                                            Toast.makeText(StartPageActivity.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(StartPageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }else{
                                Toast.makeText(StartPageActivity.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (ApiException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }
    }



}