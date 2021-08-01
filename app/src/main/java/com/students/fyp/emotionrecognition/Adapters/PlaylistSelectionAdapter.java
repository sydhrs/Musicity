package com.students.fyp.emotionrecognition.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.students.fyp.emotionrecognition.Models.Playlist;
import com.students.fyp.emotionrecognition.R;
import com.students.fyp.emotionrecognition.databinding.PlaylistSampleBinding;
import com.students.fyp.emotionrecognition.databinding.PlaylistSelectionSampleBinding;

import java.util.ArrayList;

public class PlaylistSelectionAdapter extends RecyclerView.Adapter<PlaylistSelectionAdapter.PlaylistViewHolder>{
    //this
    ArrayList<Playlist> list;
    Context context;

    public PlaylistSelectionAdapter(ArrayList<Playlist> list, Context context){
        this.list=list;
        this.context=context;

    }
    //this
    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.playlist_selection_sample,parent,false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, final int position) {
        final Playlist playlist=list.get(position);
        holder.binding.playlistname.setText(playlist.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class PlaylistViewHolder extends RecyclerView.ViewHolder{

        PlaylistSelectionSampleBinding binding;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            binding = PlaylistSelectionSampleBinding.bind(itemView);
        }
    }
}

