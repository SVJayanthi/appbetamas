package com.example.appbetamas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class SearchActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth auth;

    private ImageView imageC;
    private TextView titleC;
    private TextView countryC;
    private TextView publishC;
    private TextView subscribersC;
    private TextView videoC;
    private TextView viewsC;
    private TextView urlC;
    private TextView descriptionC;
    private TextView linksC;
    private TextView valueC;

    private String query;
    private String key;
    private double valueD;

    private EditText percentC;
    private Button sendC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creator);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();

        query = intent.getStringExtra("query");

        imageC = findViewById(R.id.c_image);
        titleC = findViewById(R.id.c_title);
        countryC = findViewById(R.id.c_country);
        publishC = findViewById(R.id.c_published);
        subscribersC = findViewById(R.id.c_subscribers);
        videoC = findViewById(R.id.c_video);
        viewsC = findViewById(R.id.c_views);
        urlC = findViewById(R.id.c_url);
        descriptionC = findViewById(R.id.c_description);
        linksC = findViewById(R.id.c_links);
        valueC = findViewById(R.id.c_value);


        percentC = findViewById(R.id.c_percent);
        sendC = findViewById(R.id.c_send);


        sendC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = percentC.getText().toString();
                double percent = Double.parseDouble(percentC.getText().toString());
                if (percent >0 && percent < 100) {
                    FirebaseUser user = auth.getCurrentUser();

                    Investor investor = new Investor(user.getUid(), urlC.getText().toString(), titleC.getText().toString(), String.valueOf(percent),
                            String.valueOf((valueD / 12)*(percent/100)));
                    mDatabase.getReference().child("users").child(user.getUid()).child(investor.getVideoName()).setValue(investor);

                    String myUrl = "http://api.reimaginebanking.com/accounts/5db520d43c8c2216c9fcb68c/purchases?key=9e004d793d35bca2c05c345e19217c9a";
                    String merchant_id = "5db5236d3c8c2216c9fcb68f";
                    URL url = null;
                    try {
                        url = new URL(myUrl);
                    } catch (Exception e) {
                        Log.e(TAG, "Exception: " + e.getMessage());
                    }
                    int amount = 1234;
                    String description = "12 percent equity";
                    String result = "";
                    try {
                        result = purchaseStock(url, merchant_id, amount, description);
                    } catch (Exception e) {
                        Log.e(TAG, "Exception: " + e.getMessage());
                    }
                    Log.d(TAG, "Results: " + result);

                    Toast.makeText(SearchActivity.this, "Offer Submitted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(SearchActivity.this, "Invalid Submission", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "Open Search");

        new RetrieveFeedTask().execute();
    }


    private static String purchaseStock(URL url, String merchant_id, int amount, String description ) throws IOException {
        try {

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String dateString = (formatter.format(date));

            JSONObject purchase = new JSONObject();
            purchase.put("merchant_id", merchant_id);
            purchase.put("medium", "balance");
            purchase.put("purchase_date", dateString);
            purchase.put("amount", amount);
            purchase.put("status", "pending");
            purchase.put("description", description);

            OutputStreamWriter outputStream = new OutputStreamWriter(con.getOutputStream());
            outputStream.write(purchase.toString());
            outputStream.flush();


            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));


            String line = "";
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            return stringBuilder.toString();
        }
        catch(Exception e){
            return e.getMessage();
        }

    }
    private void setAdapter() {
        Log.d(TAG, "Accessing Database");
        mDatabase.getReference().child("channels").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "onChildAddedR:" + dataSnapshot.getKey());

                        if (key.equals(dataSnapshot.getKey())) {
                            Creator individual = dataSnapshot.getValue(Creator.class);
                            new DownloadImageTask((ImageView) imageC)
                                    .execute((String) individual.getThumbnails().get("high").get("url"));
                            titleC.setText(individual.getTitle());
                            countryC.setText(("Country: " + individual.getCountry()));
                            publishC.setText(("Created: " + individual.getPublishedAt()));
                            subscribersC.setText(("Subscribers: " + individual.getStatistics().get("subscriberCount")));
                            videoC.setText(("Video Count: " + individual.getStatistics().get("videoCount")));
                            //viewsC.setText(("View Count: " +individual.getStatistics().get("viewCount")));
                            //urlC.setText(individual.getCustomUrl());
                            descriptionC.setText(individual.getDescription());
                            linksC.setText(individual.getVideoLinks().get(0));
                            valueD = Double.parseDouble((String) individual.getStatistics().get("subscriberCount")) / 12;
                            valueC.setText(("Value " + String.valueOf(valueD / 12)));
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }



    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            Log.d(TAG, "Pre Execute Search");
        }

        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL("https://core-result-256922.appspot.com/search/" + query);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }


            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            Log.i("INFO", response);
            try {
                JSONArray object = (JSONArray) new JSONTokener(response).nextValue();
                String first_line = object.getString(0);
                key = first_line.substring(7, 31);
                Log.i("Key", key);
                new RetrieveAddTask().execute();
                setAdapter();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class RetrieveAddTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL("https://core-result-256922.appspot.com/id/" + key);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }


            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
        }
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
