package com.gxun.thrity_music;

import android.provider.UserDictionary;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SongDao {
@Insert
    void insertSongs(Song... songs);
@Update
   void updateSongs(Song... songs);
@Delete
    void deleteSongs(Song... songs);
@Query("delete from song")
   void deleteAllSongs();
@Query("select * from Song order by id desc")
    LiveData<List<Song>>getAllSongsLive();
}

