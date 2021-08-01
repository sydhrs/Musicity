package com.students.fyp.emotionrecognition.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.students.fyp.emotionrecognition.Activities.TagActivity;
import com.students.fyp.emotionrecognition.Activities.HomeActivity;
import com.students.fyp.emotionrecognition.Models.Track;
import com.students.fyp.emotionrecognition.R;
import com.students.fyp.emotionrecognition.databinding.MusicLayoutBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PlaylistTracksAdapter extends RecyclerView.Adapter<PlaylistTracksAdapter.TracksViewHolder>{
    //this
    ArrayList<Track> list;
    Context context;
    FirebaseFirestore database;
    String playlist;

    public PlaylistTracksAdapter(ArrayList<Track> list, String playlist, Context context){
        this.list=list;
        this.context=context;
        this.playlist = playlist;
        database = FirebaseFirestore.getInstance();
    }
    //this
    @NonNull
    @Override
    public TracksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.music_layout,parent,false);
        return new TracksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TracksViewHolder holder, final int position) {
        final Track track=list.get(position);
        holder.binding.tvSongName.setText(track.getName());
        holder.binding.tvSingerName.setText(track.getArtist());
        holder.binding.tvSongDuration.setText(track.getTime());

        holder.binding.tvSongName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)context).setPlaylistTracks(list);
                ((HomeActivity)context).startSound(track, true);
            }
        });

        holder.binding.imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                database.collection("Users")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .collection("Playlists")
                                        .document(playlist)
                                        .collection("Tracks")
                                        .document(track.getKey()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        ((HomeActivity)context).showSnack("Tracked deleted from playlist.");
                                    }
                                });
                                break;
                            case R.id.changeTag:
                                Intent intent = new Intent(context, TagActivity.class);
                                intent.putExtra("name", track.getName());
                                intent.putExtra("artist", track.getArtist());
                                intent.putExtra("mood", track.getMood());
                                intent.putExtra("playlist", playlist);
                                intent.putExtra("trackId", track.getKey());
                                context.startActivity(intent);
                                break;
                        }
                        return false;
                    }
                });
                popup.inflate(R.menu.tags);
                popup.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class TracksViewHolder extends RecyclerView.ViewHolder{

        MusicLayoutBinding binding;

        public TracksViewHolder(View itemView) {
            super(itemView);
            binding = MusicLayoutBinding.bind(itemView);
        }
    }
}

