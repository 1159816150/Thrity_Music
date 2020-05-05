package com.gxun.thrity_music;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Song {
    @PrimaryKey(autoGenerate = false)
    public int id;//歌曲id
    public String name;//歌曲名
    public String singer;//歌手
    public long size;//歌曲所占空间大小
    public String duration;//歌曲时间长度
    public String path;//歌曲地址
    public long albumId;//图片id
    public String albumBip; //专辑图片
    public boolean isPlaying;

    @Override
    public String toString() {
        return "Song{" +
                "name='" + name + '\'' +
                ", singer='" + singer + '\'' +
                ", size=" + size +
                ", duration='" + duration + '\'' +
                ", path='" + path + '\'' +
                ", albumId=" + albumId +
                ", id=" + id +
                ", albumBip=" + albumBip +
                ", isPlaying=" + isPlaying +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlbumBip() {
        return albumBip;
    }

    public void setAlbumBip(String albumBip) {
        this.albumBip = albumBip;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }


}