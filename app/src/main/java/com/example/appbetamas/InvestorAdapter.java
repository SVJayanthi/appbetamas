package com.example.appbetamas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;


/**
 * Created by Sravan on 1/8/2018.
 */

//Builds all recycler views to display books and queries for the data from Firebase server
public class InvestorAdapter extends RecyclerView.Adapter<InvestorAdapter.InvestorViewHolder> {

    //Instantiate objects
    private Context mContext;
    private int mCount;
    public static String id = null;
    public static int viewHolderCount = 0;



    final private ListItemClickListener mOnClickListener;
    private final static String TAG = InvestorAdapter.class.getSimpleName();

    //Listen for when an item is clicked
    public interface ListItemClickListener {
        void onListItemClick(String clickedBook);
    }

    private List<Investor> mInvestors;

    //Sends query for list of books to the server and recieves book data
    public InvestorAdapter(Context context, ListItemClickListener listener, List<Investor> creatorsList) {
        this.mContext = context;
        this.mOnClickListener = listener;
        this.mInvestors = creatorsList;
        this.mCount = creatorsList.size();
    }

    //Sets up view holder inflater to display all the books
    @Override
    public InvestorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.creator, parent, false);
        Log.d(TAG, "ViewHolder Number: " + viewHolderCount);
        viewHolderCount++;
        return new InvestorViewHolder(view);
    }

    //Creates the view holder for each of the books queried
    @Override
    public void onBindViewHolder(InvestorViewHolder holder, int position) {
        if (mInvestors.get(position)==null) {
            return; // bail if returned null
        }
        Investor individual = mInvestors.get(position);

        Log.d(TAG, "#" + position);
        holder.name.setText(individual.getVideoName());
        holder.percent.setText((String) String.valueOf(individual.getPercent()));
        
        holder.location.setText(individual.getCountry());
    }


    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            Log.d(TAG, "Url is " + url);
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            Log.e(TAG, "Error message "+ e.getMessage());
            return null;
        }
    }

    @Override
    public int getItemCount() { return mCount; }



    //Inner class to hold the views needed to display a single item in the recycler view
    class InvestorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name;
        TextView percent;
        TextView value;
        TextView full;

        //Constructor for viewholder
        public InvestorViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.video_name);
            percent = (TextView) itemView.findViewById(R.id.video_percent);
            value = (TextView) itemView.findViewById(R.id.video_value);
            full = (TextView) itemView.findViewById(R.id.video_full);
            itemView.setOnClickListener(this);
        }


        //Recieves position of item clicked and sends the book id to a new book display page
        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            String clicked = mInvestors.get(clickedPosition).getKey();

            mOnClickListener.onListItemClick(clicked);
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
                InputStream in = new URL(urldisplay).openStream();
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
