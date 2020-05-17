package com.example.popularmovies1;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.squareup.picasso.Picasso;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

    public List<String> imageIds;
    public List<JSONObject> jsonObjects;
    public static final String BASE_IMAGE_URL="https://image.tmdb.org/t/p/w185/";


    private MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(String weatherForDay);
    }

    public MainAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public MainAdapter(List<JSONObject> jsonObjects) {
        this.jsonObjects = jsonObjects;
    }

    @NonNull
    @Override
    public MainAdapter.MyViewHolder onCreateViewHolder (@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recycler_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.MyViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return jsonObjects.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements OnClickListener
    {
        ImageView imageView;

        TextView tv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //tv=itemView.findViewById(R.id.tv);
            imageView=itemView.findViewById(R.id.image_view);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            //tv.setText(imageIds.get(position));
            List<String> imageIds=new ArrayList<>();
            for(int i=0;i<jsonObjects.size();i++)
            {
                try {
                    imageIds.add(jsonObjects.get(i).getString("poster_path"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }




            Picasso.get().load(BASE_IMAGE_URL+imageIds.get(position)).into(imageView);


        }


        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();


            Log.d("adapter","clicked");
            Intent i = new Intent(v.getContext(), DetailActivity.class);
            String title=null;
            String imageUrl = null;
            String release_date=null;
            String rating=null;
            String overview=null;
            String id=null;
            try {
                title=jsonObjects.get(adapterPosition).getString("title");
                imageUrl=jsonObjects.get(adapterPosition).getString("poster_path");
                release_date=jsonObjects.get(adapterPosition).getString("release_date");
                rating=jsonObjects.get(adapterPosition).getString("vote_average");
                overview=jsonObjects.get(adapterPosition).getString("overview");
                id=jsonObjects.get(adapterPosition).getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            i.putExtra("imageUrl",imageUrl);
            //Toast.makeText(v.getContext(),String.valueOf(getAdapterPosition())+title,Toast.LENGTH_LONG).show();
            i.putExtra("title",title);
            i.putExtra("rating",rating);
            i.putExtra("release_date",release_date);
            i.putExtra("overview",overview);
            i.putExtra("id",id);

            v.getContext().startActivity(i);
        }
    }

}

