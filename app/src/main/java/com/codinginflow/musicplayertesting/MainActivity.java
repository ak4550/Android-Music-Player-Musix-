package com.codinginflow.musicplayertesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.codinginflow.musicplayertesting.adapters.AllSongsAdapter;
import com.codinginflow.musicplayertesting.utils.Song;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";

    private Toolbar toolBar;
    private DrawerLayout nav_drawer;
    private static final int PERMISSION_CODE = 20;
    private RecyclerView all_songs_recycler_view;
    private SearchView search_option;
   // public static MyPlayer mMediaPlayer;
    private NotificationManager notificationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUserPermission();

        toolBar = findViewById(R.id.toolBar);
        nav_drawer = findViewById(R.id.nav_drawer);
        setSupportActionBar(toolBar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, nav_drawer, toolBar, R.string.open_drawer_navigation, R.string.close_drawer_navigation);
        nav_drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // Setting recyclerView and it's adapter
        all_songs_recycler_view = findViewById(R.id.all_songs_recycler_view);
        final AllSongsAdapter adapter = new AllSongsAdapter();
        adapter.setSongData(getAllSongs());
        all_songs_recycler_view.setHasFixedSize(true);
        all_songs_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        all_songs_recycler_view.setAdapter(adapter);

        // SearchView setup
        search_option = findViewById(R.id.search_option);
        search_option.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        // MediaPlayer

    }

    private ArrayList<Song> getAllSongs(){

        ArrayList<Song> myList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{MediaStore.Audio.Media.TITLE,
                                            MediaStore.Audio.Media.ARTIST,
                                           MediaStore.Audio.Media.DATA};

        Cursor songCursor = getContentResolver().query(uri, projection, null, null, null);
        if(songCursor != null){
            songCursor.moveToFirst();
            do{
                String name = songCursor.getString(songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artistName = songCursor.getString(songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String data = songCursor.getString(songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                try{
                    String albumArtPath = songCursor.getString(songCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART));
                    Log.d(TAG, "getAllSongs: Album Path " + albumArtPath);
                    Song song = new Song(name, artistName, data, albumArtPath);
                    myList.add(song);
                }catch (Exception e){
                    e.printStackTrace();
                }


//                Log.d(TAG, "getAllSongs: Name : " + name);
//                Log.d(TAG, "getAllSongs: Artist name: " + artistName);
//                Log.d(TAG, "getAllSongs: Path : " + data);



            }while(songCursor.moveToNext());

            songCursor.close();
        }
        return myList;
    }

    private void getUserPermission(){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // user has granted the permission
            }else{
                // user hasn't granted the permission
                getUserPermission();
            }
        }
    }


    @Override
    protected void onPause() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Title")
                .setContentText("Artist")
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                .addAction(R.drawable.ic_previous, "Last Song", null)
                .addAction(R.drawable.ic_play_button, "Now Playing", null)
                .addAction(R.drawable.ic_next, "Next Song", null)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        notificationManager.notify(10, notification);

        super.onPause();
    }
}