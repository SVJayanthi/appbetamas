package com.example.appbetamas;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class InvestorActivity extends AppCompatActivity implements InvestorAdapter.ListItemClickListener  {
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
        setContentView(R.layout.activity_investor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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

        recyclerViewTopCreators = (RecyclerView) findViewById(R.id.investments_list);


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
                        Intent intent = new Intent(InvestorActivity.this, LoginActivity.class);

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

        Intent intent = new Intent(InvestorActivity.this, CreatorActivity.class);
        Creator create = null;
        for (Creator person : creators)
            if (person.getKey().equals(clicked))
                create = person;
        Log.d(TAG, "Tag: " + clicked);
        intent.putExtra("url", (String) create.getThumbnails().get("high").get("url"));
        intent.putExtra("title", create.getTitle());
        intent.putExtra("country", create.getCountry());
        intent.putExtra("publish", create.getPublishedAt());
        intent.putExtra("subscribers", (String) create.getStatistics().get("subscriberCount"));
        intent.putExtra("video", (String) create.getStatistics().get("videoCount"));
        intent.putExtra("view", (String) create.getStatistics().get("viewCount"));
        intent.putExtra("v_url", (String) create.getStatistics().get("customUrl"));
        intent.putExtra("description", (String) create.getDescription());
        intent.putExtra("links", (String) create.getVideoLinks().get(0) + " \n" + create.getVideoLinks().get(1));

        startActivity(intent);


    }

}
