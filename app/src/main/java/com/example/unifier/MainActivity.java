package com.example.unifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.unifier.Fragements.HomeFragment;
import com.example.unifier.Fragements.NotificationFragment;
import com.example.unifier.Fragements.ProfileFragment;
import com.example.unifier.Fragements.SearchFragment;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private Button btnLogOut;
    private BottomNavigationView bnv;
    private Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogOut = findViewById(R.id.btnlogOut);
        bnv = findViewById(R.id.bottom_navigation);
         bnv.setOnItemReselectedListener(item -> {
             switch(item.getItemId()){
                 case R.id.iconHome:
                     selectorFragment = new HomeFragment();
                     break;
                 case R.id.iconSearch:
                     selectorFragment = new SearchFragment();
                     break;
                 case R.id.iconAdd:
                     selectorFragment = null;
                     startActivity(new Intent(MainActivity.this,PostActivity.class));
                     finish();
                     break;
                 case R.id.iconFavorite:
                     selectorFragment = new NotificationFragment();
                     break;
                 case R.id.iconPerson:
                     selectorFragment = new ProfileFragment();
                     break;
             }

             if(selectorFragment != null){
                 getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectorFragment).commit();
             }

         });

         getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                (object, response) -> {
                    // Application code
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();



        btnLogOut.setOnClickListener(v -> signOut());

    }

    private void signOut() {
        gsc.signOut().addOnCompleteListener(task -> {
            finish();
            startActivity(new Intent(MainActivity.this,StartPageActivity.class));
        });
    }
}