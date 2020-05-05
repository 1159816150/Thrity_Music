package com.gxun.thrity_music;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class SongViewModel extends AndroidViewModel {
    SongDao songDao;
    private SongRepository songRepository;

    public SongViewModel(@NonNull Application application) {
        super(application);
        songRepository=new SongRepository(application);
    }


    void deleteAllSongs(){
        songRepository.deleteAllSongs();
    }

    LiveData<List<Song>>getAllSongsLive(){
        return songRepository.getAllSongsLive();
    }
    void insertSongs(Song...songs){
        songRepository.insertSongs(songs);
    }
    void updateSongs(Song...songs){
        songRepository.updateSongs(songs);
    }
}
