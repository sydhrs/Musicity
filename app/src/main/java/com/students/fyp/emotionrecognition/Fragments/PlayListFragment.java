package com.students.fyp.emotionrecognition.Fragments;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.students.fyp.emotionrecognition.Adapters.PlaylistAdapter;
import com.students.fyp.emotionrecognition.Models.Playlist;
import com.students.fyp.emotionrecognition.R;
import com.students.fyp.emotionrecognition.databinding.AddPlaylistDialogBinding;
import com.students.fyp.emotionrecognition.databinding.FragmentPlayListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PlayListFragment extends Fragment {

    FragmentPlayListBinding binding;
    FirebaseFirestore database;
    ArrayList<Playlist> playlists;
    PlaylistAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPlayListBinding.inflate(inflater, container, false);

        database = FirebaseFirestore.getInstance();
        playlists = new ArrayList<>();
        adapter = new PlaylistAdapter(playlists, getContext());

        GridLayoutManager manager = new GridLayoutManager(getContext(),2);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(adapter);

        database.collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("Playlists")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                        if(value.getDocuments().size() > 0) {
                            playlists.clear();
                            for (DocumentSnapshot document : value.getDocuments()) {
                                Playlist playlist = document.toObject(Playlist.class);
                                playlist.setKey(document.getId());
                                playlists.add(playlist);
                            }
                            adapter.notifyDataSetChanged();
                            binding.noPlaylists.setVisibility(View.GONE);
                        } else {
                            binding.noPlaylists.setVisibility(View.VISIBLE);
                        }
                    }
                });




        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = LayoutInflater.from(getContext()).inflate(R.layout.add_playlist_dialog,null);
                final AddPlaylistDialogBinding dialogBinding = AddPlaylistDialogBinding.bind(view);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                dialogBuilder.setCancelable(true);
                dialogBuilder.setView(dialogBinding.getRoot());

                final AlertDialog dialog = dialogBuilder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                dialogBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialogBinding.createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Playlist playlist = new Playlist(dialogBinding.nameBox.getText().toString());
                        database.collection("Users")
                                .document(FirebaseAuth.getInstance().getUid())
                                .collection("Playlists")
                                .document()
                                .set(playlist);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        return binding.getRoot();
    }
}