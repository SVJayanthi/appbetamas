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

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;


/**
 * Created by Sravan on 1/8/2018.
 */

//Builds all recycler views to display books and queries for the data from Firebase server
public class CreatorAdapter extends RecyclerView.Adapter<CreatorAdapter.CreatorViewHolder> {

    //Instantiate objects
    private Context mContext;
    private int mCount;
    public static String id = null;
    public static int viewHolderCount = 0;



    final private ListItemClickListener mOnClickListener;
    private final static String TAG = CreatorAdapter.class.getSimpleName();

    //Listen for when an item is clicked
    public interface ListItemClickListener {
        void onListItemClick(String clickedBook);
    }

    private List<Creator> mCreators;

    //Sends query for list of books to the server and recieves book data
    public CreatorAdapter(Context context, ListItemClickListener listener, List<Creator> creatorsList) {
        this.mContext = context;
        this.mOnClickListener = listener;
        this.mCreators = creatorsList;
        this.mCount = creatorsList.size();
    }

    //Sets up view holder inflater to display all the books
    @Override
    public CreatorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.creator, parent, false);
        Log.d(TAG, "ViewHolder Number: " + viewHolderCount);
        viewHolderCount++;
        return new CreatorViewHolder(view);
    }

    //Creates the view holder for each of the books queried
    @Override
    public void onBindViewHolder(CreatorViewHolder holder, int position) {
        if (mCreators.get(position)==null) {
            return; // bail if returned null
        }
        Creator individual = mCreators.get(position);

        Log.d(TAG, "#" + position);
        new DownloadImageTask((ImageView) holder.image)
                .execute((String) individual.getThumbnails().get("default").get("url"));
        //holder.image.setImageDrawable(LoadImageFromWebOperations((String) individual.getThumbnails().get("default").get("url")));
        holder.name.setText(individual.getTitle());
        holder.subscriber.setText((String) individual.getStatistics().get("subscriberCount"));
        if (individual.getCountry() == null) {
            individual.setCountry("N/A");
        }
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
    class CreatorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        ImageView image;
        TextView name;
        TextView subscriber;
        TextView location;

        //Constructor for viewholder
        public CreatorViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.creator_image);
            name = (TextView) itemView.findViewById(R.id.creator_name);
            subscriber = (TextView) itemView.findViewById(R.id.creator_sub);
            location = (TextView) itemView.findViewById(R.id.creator_loc);
            itemView.setOnClickListener(this);
        }


        //Recieves position of item clicked and sends the book id to a new book display page
        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            String clicked = mCreators.get(clickedPosition).getKey();

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
