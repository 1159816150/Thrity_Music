package com.gxun.thrity_music;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class SongRepository {
    LiveData<List<Song>>allSongsLive;
    List<Song>allSongs;
    SongDao songDao;
    SongRepository(Context context){
      SongDatebase songDatebase=SongDatebase.getDatabase(context.getApplicationContext());
      songDao =songDatebase.getSongDao();
      allSongsLive=songDao.getAllSongsLive();
    }
    public LiveData<List<Song>> getAllSongsLive(){
        return  allSongsLive;
    }

    public  List<Song>getAllSongs(){
        return allSongs;
    }
    void insertSongs(Song...songs){

        new InsertAsyncTask(songDao).execute(songs);
    }
    void updateSongs(Song...songs){
        new InsertAsyncTask(songDao).execute(songs);
    }
    void deleteAllSongs(Song...songs){
        new DeleteAllAsyncTask(songDao).execute();
    }


    static class UpdateAsyncTask extends AsyncTask<Song,Void,Void>
    {
        private SongDao songDao;
        public UpdateAsyncTask(SongDao songDao) {
            this.songDao = songDao;
        }

        @Override
        protected Void doInBackground(Song... songs) {
            songDao.updateSongs(songs);
            return null;
        }
    }


    static class DeleteAllAsyncTask extends AsyncTask<Void,Void,Void>
    {
        private SongDao songDao;
        public DeleteAllAsyncTask(SongDao songDao) {
            this. songDao= songDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            songDao.deleteAllSongs();
            return null;
        }
    }
    static class InsertAsyncTask extends AsyncTask<Song,Void,Void>
    {
        private SongDao songDao;
        public InsertAsyncTask(SongDao songDao){
            this.songDao=songDao;
        }

        @Override
        protected Void doInBackground(Song... songs) {
            songDao.insertSongs(songs);
            return null;
        }
    }
}
