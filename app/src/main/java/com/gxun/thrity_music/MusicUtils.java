package com.gxun.thrity_music;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
public class MusicUtils {

        //定义一个集合，存放从本地读取到的内容
        public static List<Song> songList;
        public static Song song;
        private static String name;
        private static String singer;
        private static String path;
        private static int duration;
        private static long size;
        private static long albumId;
        private static long id;
        //获取专辑封面的Uri
        private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

        public static List<Song> getmusic(Context context) {
            StringAndBitmap stringAndBitmap = null;
            songList= new ArrayList<>();


            Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    , null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

            assert cursor != null;
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));            //获取歌名
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));         //获取歌唱者
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));           //获取专辑名
                    int albumID = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));            //获取专辑图片id
                    int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    //创建Song对象，并赋值
                    Song song = new Song();
                    song.id = id;
                    song.name = title;
                    song.singer = artist;
                    song.path = path;
                    song.duration = MusicUtils.formatTime(duration);
                    song.albumBip = stringAndBitmap.bitmapToString(getAlbumArt(albumID,context));
                    //将歌曲放入数据库
                    songList.add(song);
                    //songViewModel.insertSongs(song);

                } while (cursor.moveToNext());
            } else {
                Toast.makeText(context, "本地没有音乐哦", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
            return songList;
        }

        //    转换歌曲时间的格式
        public static String formatTime(int time) {
            if (time / 1000 % 60 < 10) {
                String tt = time / 1000 / 60 + ":0" + time / 1000 % 60;
                return tt;
            } else {
                String tt = time / 1000 / 60 + ":" + time / 1000 % 60;
                return tt;
            }
        }

    //获取专辑图片的方法
    private static Bitmap getAlbumArt(int album_id,Context context) {                              //前面我们只是获取了专辑图片id，在这里实现通过id获取掉专辑图片
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur =context. getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);
        String album_art = null;
        assert cur != null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        Bitmap bm = null;
        if (album_art != null) {
            bm = BitmapFactory.decodeFile(album_art);
        } else {

            bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.touxiang2);
        }
        return bm;
    }
}
