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
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

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

public class CreatorActivity extends AppCompatActivity {

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

    private EditText percentC;
    private Button sendC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creator);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();

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


        new DownloadImageTask((ImageView) imageC)
                .execute(intent.getStringExtra("url"));
        titleC.setText(intent.getStringExtra("title"));
        countryC.setText(("Country: " + intent.getStringExtra("country")));
        publishC.setText(("Created: " + intent.getStringExtra("publish")));
        subscribersC.setText(("Subscribers: " + intent.getStringExtra("subscribers")));
        videoC.setText(("Video Count: " + intent.getStringExtra("video")));
        viewsC.setText(intent.getStringExtra("views"));
        urlC.setText(intent.getStringExtra("v_url"));
        descriptionC.setText(intent.getStringExtra("description"));
        linksC.setText(intent.getStringExtra("links"));
        final double valueD = Double.parseDouble(intent.getStringExtra("subscribers")) / 12;
        valueC.setText(("Value " + String.valueOf(valueD / 12)));

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


                    int amount =  (int) ((valueD / 12)*(percent/100));
                    Log.d("amount", ""+amount);
                    String description = "12 percent equity";
                    String result = "asdf";
                    try {
                        result = purchaseStock(url, merchant_id, amount, description);
                    } catch (Exception e) {
                        Log.e(TAG, "Exception: " + e.getMessage());
                    }
                    Log.d(TAG, "Results: " + result);

                    Toast.makeText(CreatorActivity.this, "Offer Submitted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(CreatorActivity.this, "Invalid Submission", Toast.LENGTH_SHORT).show();
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

            Log.d(TAG, "Con: " + con.getInputStream().toString());

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
