package com.students.fyp.emotionrecognition.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.students.fyp.emotionrecognition.Activities.HomeActivity;
import com.students.fyp.emotionrecognition.Models.Playlist;
import com.students.fyp.emotionrecognition.R;
import com.students.fyp.emotionrecognition.databinding.PlaylistSampleBinding;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>{
    //this
    ArrayList<Playlist> list;
    Context context;



    public PlaylistAdapter(ArrayList<Playlist> list, Context context){
        this.list=list;
        this.context=context;
    }
    //this
    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.playlist_sample,parent,false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, final int position) {
        final Playlist playlist=list.get(position);
        holder.binding.name.setText(playlist.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)context).showPlaylistDetail(playlist.getKey());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class PlaylistViewHolder extends RecyclerView.ViewHolder{

        PlaylistSampleBinding binding;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            binding = PlaylistSampleBinding.bind(itemView);
        }
    }
}

