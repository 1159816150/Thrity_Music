package com.gxun.thrity_music;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
public class MusicService extends Service {
    public static int getA() {
        return a;
    }

    public static void setA(int a) {
        MusicService.a = a;
    }

    //用来计位置
    private static int a=0;
    public MediaPlayer mediaPlayer;
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }
    MusicBinder musicBinder=new MusicBinder();
    //获得binder
    class MusicBinder extends Binder{
        public MusicService getService(){
            return MusicService.this;
        }
    }
    //开始方法
    public void start(List<Song> list){
        if(mediaPlayer==null){
            mediaPlayer=MediaPlayer.create(this, Uri.parse(list.get(a).path));

        }
        mediaPlayer.start();

    }
    //暂停方法
    public void stop(){
        if(mediaPlayer!=null){
            mediaPlayer.pause();
        }
    }
    //下一首
    public void next(List<Song> list) throws IOException {
        mediaPlayer.pause();
        a++;
        if(a>=list.size()){
            a=0;
        }
        mediaPlayer=MediaPlayer.create(this, Uri.parse(list.get(a).path));
        mediaPlayer.start();
    }
    //上一首
    public void up(List<Song> list) throws IOException {
        mediaPlayer.pause();
        --a;
        if(a<0){
            a=(list.size()-1);
        }
        mediaPlayer=MediaPlayer.create(this, Uri.parse(list.get(a).path));
        mediaPlayer.start();
    }

}

