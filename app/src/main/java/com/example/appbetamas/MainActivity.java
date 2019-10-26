package com.example.appbetamas;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements CreatorAdapter.ListItemClickListener {

    private AppBarConfiguration mAppBarConfiguration;

    //Google Firebase Database objects
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private GoogleSignInClient mGoogleSignInClient;

    private CreatorAdapter adapterTopCreators;
    private RecyclerView recyclerViewTopCreators;

    private ImageView imageUser;
    private TextView nameUser;
    private TextView emailUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().requestScopes(new Scope("https://www.googleapis.com/auth/youtubepartner"), new Scope("https://www.googleapis.com/auth/yt-analytics-monetary.readonly"))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                signOut();
            }
        });


        //Get server databse references
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        databaseReference = mDatabase.getReference().child("channels");

        recyclerViewTopCreators = (RecyclerView) findViewById(R.id.creators_list);


        //Set up layout managers for recycler views
        RecyclerView.LayoutManager mymanag = new LinearLayoutManager(this);
        recyclerViewTopCreators.setLayoutManager(mymanag);
        recyclerViewTopCreators.setItemAnimator(new DefaultItemAnimator());
        //recyclerViewTopCreators.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerViewTopCreators.setAdapter(adapterTopCreators);



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);

                        startActivity(intent);
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();

        setAdapter();
        Log.d(TAG, "Start Complete");

    }

    private List<Creator> creators = new ArrayList<>();

    private void setAdapter() {
        Log.d(TAG, "Top Creator");

        creators.clear();
        databaseReference.orderByChild("statistics/subscriberCount").limitToFirst(10).
                addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "onChildAddedR:" + dataSnapshot.getKey());

                        Creator individual = dataSnapshot.getValue(Creator.class);
                        individual.setKey(dataSnapshot.getKey());

                        creators.add(individual);

                        if (creators.size() > 8) {
                            Log.d(TAG, "callingAdapter:" + dataSnapshot.getKey());
                            setTopAdapter();
                            return;
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    //Set adapters for each of the book/calendar displays
    private void setTopAdapter() {
        Log.d(TAG, "Setting Adapter");
        adapterTopCreators = new CreatorAdapter(this, this, creators);
        recyclerViewTopCreators.setAdapter(adapterTopCreators);

    }

    @Override
    public void onListItemClick(String clicked) {
/*
        Intent intent = new Intent(MainActivity.this, CreatorDetail.class);
        intent.putExtra("Identification", clicked);

        startActivity(intent);
        */

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
