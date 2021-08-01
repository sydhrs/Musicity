package com.students.fyp.emotionrecognition.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.students.fyp.emotionrecognition.Activities.HomeActivity;
import com.students.fyp.emotionrecognition.Models.Playlist;
import com.students.fyp.emotionrecognition.Models.Track;
import com.students.fyp.emotionrecognition.R;
import com.students.fyp.emotionrecognition.RecyclerItemClickListener;
import com.students.fyp.emotionrecognition.databinding.AddPlaylistDialogBinding;
import com.students.fyp.emotionrecognition.databinding.MusicLayoutBinding;
import com.students.fyp.emotionrecognition.databinding.PlaylistDialogBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AllTracksAdapter extends RecyclerView.Adapter<AllTracksAdapter.TracksViewHolder>{
    //this
    public ArrayList<Track> list;
    Context context;

    public interface OnItemClickListener {
        void onItemClick(Track item);
    }

    private final OnItemClickListener listener;

    public AllTracksAdapter(ArrayList<Track> list, Context context, OnItemClickListener listener){
        this.list=list;
        this.context=context;
        this.listener = listener;
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

//        holder.binding.tvSongName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((HomeActivity)context).startSound(track);
//            }
//        });

        holder.binding.llTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(track);
            }
        });

        holder.binding.imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        View view = LayoutInflater.from(context).inflate(R.layout.playlist_dialog,null);
                        final PlaylistDialogBinding dialogBinding = PlaylistDialogBinding.bind(view);

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                        dialogBuilder.setCancelable(true);
                        dialogBuilder.setView(dialogBinding.getRoot());

                        final AlertDialog dialog = dialogBuilder.create();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        final FirebaseFirestore database = FirebaseFirestore.getInstance();

                        final ArrayList<Playlist> playlists = new ArrayList<>();
                        final PlaylistSelectionAdapter adapter = new PlaylistSelectionAdapter(playlists,context);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                        dialogBinding.recyclerView.setLayoutManager(layoutManager);
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                                layoutManager.getOrientation());
                        dialogBinding.recyclerView.addItemDecoration(dividerItemDecoration);
                        dialogBinding.recyclerView.setAdapter(adapter);

                        dialogBinding.recyclerView.addOnItemTouchListener(
                                new RecyclerItemClickListener(context, dialogBinding.recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                                    @Override public void onItemClick(View view, int position) {
                                        final Playlist playlist = playlists.get(position);
                                        database.collection("Users")
                                                .document(FirebaseAuth.getInstance().getUid())
                                                .collection("Playlists")
                                                .document(playlist.getKey())
                                                .collection("Tracks")
                                                .document(track.getKey())
                                                .set(track).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss();
                                                ((HomeActivity)context).showSnack(String.format("New track added in %s",playlist.getName()));
                                            }
                                        });
                                    }

                                    @Override public void onLongItemClick(View view, int position) {
                                        // do whatever
                                    }
                                })
                        );

                        database.collection("Users")
                                .document(FirebaseAuth.getInstance().getUid())
                                .collection("Playlists")
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                playlists.clear();
                                for(DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                    Playlist playlist = snapshot.toObject(Playlist.class);
                                    playlist.setKey(snapshot.getId());
                                    playlists.add(playlist);
                                }
                                if(playlists.size() > 0)
                                    adapter.notifyDataSetChanged();
                                else
                                    dialogBinding.emptyState.setVisibility(View.VISIBLE);
                            }
                        });


                        dialog.show();
                        return false;
                    }
                });
                popup.inflate(R.menu.track);
                popup.show();
            }
        });

    }

    public void updateList(ArrayList<Track> list){
        this.list = list;
        notifyDataSetChanged();
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

