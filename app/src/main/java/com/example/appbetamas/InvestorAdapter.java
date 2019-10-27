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
        View view = inflater.inflate(R.layout.investor, parent, false);
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
        Log.d(TAG, "Video name: " + individual.getVideoName());
        holder.nameV.setText((String) individual.getVideoName());
        holder.percentV.setText(("Percentage: " + individual.getPercent() + "%"));
        holder.valueV.setText(("Value: $" + individual.getValue()));
        holder.fullV.setText(("Market Capitalization: $" + (Double.valueOf(individual.getValue()) / (Double.valueOf(individual.getPercent())/100))));
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

        TextView nameV;
        TextView percentV;
        TextView valueV;
        TextView fullV;

        //Constructor for viewholder
        public InvestorViewHolder(View itemView) {
            super(itemView);

            nameV = (TextView) itemView.findViewById(R.id.video_name);
            percentV = (TextView) itemView.findViewById(R.id.video_percent);
            valueV = (TextView) itemView.findViewById(R.id.video_value);
            fullV = (TextView) itemView.findViewById(R.id.video_full);
            itemView.setOnClickListener(this);
        }


        //Recieves position of item clicked and sends the book id to a new book display page
        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            String clicked = mInvestors.get(clickedPosition).getUserId();

            mOnClickListener.onListItemClick(clicked);
        }

    }
}
