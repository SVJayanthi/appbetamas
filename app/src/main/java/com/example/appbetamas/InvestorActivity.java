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
import com.google.firebase.auth.FirebaseUser;
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

public class InvestorActivity extends AppCompatActivity implements InvestorAdapter.ListItemClickListener {
    private AppBarConfiguration mAppBarConfiguration;

    //Google Firebase Database objects
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private GoogleSignInClient mGoogleSignInClient;

    private InvestorAdapter adapterTopCreators;
    private RecyclerView recyclerViewTopCreators;
    private TextView portfolioValue;
    private double portfolioDouble;
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

        databaseReference = mDatabase.getReference().child("users");

        recyclerViewTopCreators = (RecyclerView) findViewById(R.id.investments_list);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().requestScopes(new Scope("https://www.googleapis.com/auth/youtubepartner"), new Scope("https://www.googleapis.com/auth/yt-analytics-monetary.readonly"))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        portfolioValue = findViewById(R.id.portfolio);

        //Set up layout managers for recycler views
        RecyclerView.LayoutManager mymanag = new LinearLayoutManager(this);
        recyclerViewTopCreators.setLayoutManager(mymanag);
        recyclerViewTopCreators.setItemAnimator(new DefaultItemAnimator());
        //recyclerViewTopCreators.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerViewTopCreators.setAdapter(adapterTopCreators);
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

    private List<Investor> creators = new ArrayList<>();

    private void setAdapter() {
        final FirebaseUser user = mAuth.getCurrentUser();
        Log.d(TAG, "Current User: " + user.getUid());

        creators.clear();
        databaseReference.child(user.getUid()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "onChildAddedR:" + dataSnapshot.getKey());

                        Investor individual = dataSnapshot.getValue(Investor.class);
                        creators.add(individual);
                        portfolioDouble += (Double.parseDouble(individual.getValue()));
                        setTopAdapter();
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
        adapterTopCreators = new InvestorAdapter(this, this, creators);
        recyclerViewTopCreators.setAdapter(adapterTopCreators);
        portfolioValue.setText("The Portfolio Value is $" + String.valueOf(portfolioDouble));

    }

    @Override
    public void onListItemClick(String clicked) {


    }

}
