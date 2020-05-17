package com.example.popularmovies1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.MyViewHolder>  {

    private List<TaskEntry> mTaskEntries;
    Context context;

    public FavoritesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public FavoritesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recycler_layout_favorites;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        FavoritesAdapter.MyViewHolder viewHolder = new FavoritesAdapter.MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesAdapter.MyViewHolder holder, int position) {
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
        if (mTaskEntries == null) {
            return 0;
        }
        return mTaskEntries.size();
    }

    public void setTasks(List<TaskEntry> taskEntries) {
        mTaskEntries = taskEntries;
        for(int i=0;i<taskEntries.size();i++)
        {
            Log.d("favorites_insert",mTaskEntries.get(i).getTitle());
        }
        notifyDataSetChanged();
    }

    public List<TaskEntry> getTasks() {
        return mTaskEntries;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView title_tv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //tv=itemView.findViewById(R.id.tv);
            title_tv=itemView.findViewById(R.id.title_tv);

        }

        public void bind(int position) {
            //tv.setText(imageIds.get(position));

            title_tv.setText(mTaskEntries.get(position).getTitle());


        }



    }
}
