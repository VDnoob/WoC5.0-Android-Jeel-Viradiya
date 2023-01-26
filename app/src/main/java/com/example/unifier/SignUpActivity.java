package com.example.unifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private EditText editUserName,editEmail,editPassword;
    private Button register;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);



        editUserName = findViewById(R.id.editUserName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        register = findViewById(R.id.btnRegister);

        reference = FirebaseDatabase.getInstance().getReference();
        auth= FirebaseAuth.getInstance();

        pd = new ProgressDialog(this);


        register.setOnClickListener(v -> {
            String userName = editUserName.getText().toString();
            String eMail = editEmail.getText().toString();
            String password = editPassword.getText().toString();

            if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(eMail) || TextUtils.isEmpty(password)){
                Toast.makeText(SignUpActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
            }
            else if(password.length() < 4){
                Toast.makeText(SignUpActivity.this, "Too short password", Toast.LENGTH_SHORT).show();
            }
            else{
                registerUser(userName,eMail,password);
            }

        });


    }

    private void registerUser(String username, String email, String Password) {

        pd.setMessage("Please wait");
        pd.show();

       auth.createUserWithEmailAndPassword(email,Password).addOnSuccessListener(authResult -> {
           HashMap<String,Object> map = new HashMap<>();
           map.put("name",username);
           map.put("email",email);
           map.put("password",Password);
           map.put("id",auth.getCurrentUser().getUid());
           map.put("bio","");
           map.put("imageURL","");

           reference.child("Users").child(auth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(task -> {
               if(task.isSuccessful()){
                   pd.dismiss();
                   Toast.makeText(SignUpActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                   Toast.makeText(SignUpActivity.this, "Update your profile for better experience", Toast.LENGTH_SHORT).show();
                   Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                   startActivity(intent);
                   finish();
               }
               else{
                   Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
               }
           });
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               pd.dismiss();
               Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
           }
       });
    }
}