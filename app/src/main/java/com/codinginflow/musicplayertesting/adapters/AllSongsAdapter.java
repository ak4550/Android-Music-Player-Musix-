package com.codinginflow.musicplayertesting.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codinginflow.musicplayertesting.MainPlayerActivity;
import com.codinginflow.musicplayertesting.R;
import com.codinginflow.musicplayertesting.utils.Song;

import java.util.ArrayList;
import java.util.List;

import es.claucookie.miniequalizerlibrary.EqualizerView;

public class AllSongsAdapter extends RecyclerView.Adapter<AllSongsAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "AllSongsAdapter";
    private Context mContext;
    private static List<Song> songList = new ArrayList<>();
    private List<Song> allSongList;
    private int currentSongPosition = -1;

    public static Song getSong(int position){
        if(songList.size() > -1 && !songList.isEmpty()){
            return songList.get(position);
        }
        return null;
    }

    public void setSongData(List<Song> songList) {
        this.songList = songList;
        allSongList = new ArrayList<>(songList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.song_item, parent, false);

        return new ViewHolder(view);
    }

    public static int getListSize(){
        if(!songList.isEmpty() && songList.size() > -1){
            return songList.size();
        }
        return -1;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.txt_song_name.setText(songList.get(position).getTrackName());
        holder.txt_song_artist.setText(songList.get(position).getArtistName());


        holder.song_item_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSongPosition = position;
                notifyDataSetChanged();
                if(position == currentSongPosition){
                    holder.equalizer_view.setVisibility(View.VISIBLE);
                    holder.equalizer_view.animateBars();
                }

                Intent intent = new Intent(mContext, MainPlayerActivity.class);
                intent.putExtra("songPosition", position);
                mContext.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        if (songList != null && !songList.isEmpty()) {
            return songList.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        return filteredSongList;
    }

    private Filter filteredSongList = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Song> filteredSongs = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredSongs.addAll(allSongList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Song song : allSongList) {
                    if (song.getTrackName().toLowerCase().contains(filterPattern)) {
                        filteredSongs.add(song);
                    }
                }
            }
            FilterResults result = new FilterResults();
            result.values = allSongList;
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            songList.clear();
            songList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgAlbum;
        CardView song_item_container;
        TextView txt_song_name, txt_song_artist;
        EqualizerView equalizer_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_song_name = itemView.findViewById(R.id.txt_song_name);
            txt_song_artist = itemView.findViewById(R.id.txt_song_artist);
            equalizer_view = itemView.findViewById(R.id.equalizer_view);
            song_item_container = itemView.findViewById(R.id.song_item_container);
            imgAlbum = itemView.findViewById(R.id.imgAlbum);


        }
    }
}
