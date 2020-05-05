package com.gxun.thrity_music;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import com.master.permissionhelper.PermissionHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    List<Song> songList = new ArrayList<>();
    LiveData<List<Song>> allSong;
   MediaPlayer mediaPlayer = new MediaPlayer();
    private TextView musicTime, musicTotal;
    StringAndBitmap stringAndBitmap = new StringAndBitmap();
    private PermissionHelper permissionHelper;
    RecyclerView recyclerView;
    ImageButton startButton, upButton, downButton;
    MyAdapter myAdapter;
    int position;
    MusicService musicService = new MusicService();
    private static TextView nowTime; //音乐当前时间
    //音乐进度条
    static SeekBar seekBar;
    private String TAG = "HelloActivity";
    //是否正在播放
    private boolean isPlaying = false;
    public static Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //super.handleMessage(msg);
            // 将SeekBar位置设置到当前播放位置，
            // msg.arg1是service传过来的音乐播放进度信息,将其设置为进度条进度
            seekBar.setProgress(msg.arg1);
            //将进度时间其转为mm:ss时间格式
            nowTime.setText(new SimpleDateFormat("mm:ss", Locale.getDefault()).format(new Date(msg.arg1)));
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SongViewModel songViewModel = ViewModelProviders.of(this).get(SongViewModel.class);
        // 关于权限的代码
        permissionHelper = new PermissionHelper(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        permissionHelper.request(new PermissionHelper.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                initListView(songViewModel);
                //获取权限后扫描数据库获取信息
                Log.d(TAG, "onPermissionGranted() called");
            }
            @Override
            public void onIndividualPermissionGranted(String[] grantedPermission) {
                Log.d(TAG, "onIndividualPermissionGranted() called with: grantedPermission = [" + TextUtils.join(",", grantedPermission) + "]");
            }
            @Override
            public void onPermissionDenied() {
                Log.d(TAG, "onPermissionDenied() called");
            }
            @Override
            public void onPermissionDeniedBySystem() {
                Log.d(TAG, "onPermissionDeniedBySystem() called");
            }
        });
// 权限代码结束
        initView();
        //声明Adapter
        myAdapter = new MyAdapter(recyclerView, songViewModel);
        //利用ViewModel将获取的songs传输到Adapter
        songViewModel.getAllSongsLive().observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                int temp = myAdapter.getItemCount();
                myAdapter.setAllSongs(songs);
                songList = songs;

                if (temp != songs.size()) {
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(myAdapter);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                myAdapter.setListener(new MyAdapter.MyClickListener() {
                    @Override
                    public void onClick(Song song, int postion) {
                        startButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_black_24dp));

                        play(songList.get(postion).getPath());
                        position=postion;
                        musicTotal.setText(songList.get(postion).getDuration());
                    }
                });
            }
        });
    }
    private void initView() {
        startButton = findViewById(R.id.start_button);
        upButton = findViewById(R.id.up_button);
        downButton = findViewById(R.id.down_button);
        nowTime = findViewById(R.id.musicTime);
        musicTotal = findViewById(R.id.musicTotal);
        //获取recyclerView
        recyclerView = findViewById(R.id.recyclerView);
        //获取seekBar
        seekBar = findViewById(R.id.seekBar);
        setListener();
    }
    private void setListener() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    try {
                        mediaPlayer.seekTo(seekBar.getProgress());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // 权限代码
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionHelper != null) {
            permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
// 权限代码结束

    //nitListView()实现对手机中MediaDataBase的扫描
    public void initListView(SongViewModel songViewModel) {
        songList.clear();
        songViewModel.deleteAllSongs();
        //获取ContentResolver的对象，并进行实例化
        ContentResolver resolver = getContentResolver();

        //获取游标
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER); //创建游标MediaStore.Audio.Media.EXTERNAL_CONTENT_URI获取音频的文件，后面的是关于select筛选条件，这里填土null就可以了
        //游标归零
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
                song.albumBip = stringAndBitmap.bitmapToString(getAlbumArt(albumID));
                //将歌曲放入数据库
                // songList.add(song);
                songViewModel.insertSongs(song);
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "本地没有音乐哦", Toast.LENGTH_SHORT).show();
        }
        cursor.close();                                                                         //关闭游标
    }
    //获取专辑图片的方法
    private Bitmap getAlbumArt(int album_id) {                              //前面我们只是获取了专辑图片id，在这里实现通过id获取掉专辑图片
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);
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

            bm = BitmapFactory.decodeResource(getResources(), R.mipmap.touxiang2);
        }
        return bm;
    }

    protected void play(String path) {

        File file = new File(path);
        if (file.exists() && file.length() > 0) {
            try {

                mediaPlayer.reset();
                // 设置指定的流媒体地址
                mediaPlayer.setDataSource(path);
                // 设置音频流的类型
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                // 通过异步的方式装载媒体资源
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                      @Override
                                                      public void onPrepared(MediaPlayer mp) {
                                                          // 装载完毕 开始播放流媒体
                                                          if (!mediaPlayer.isPlaying()) {
                                                              mediaPlayer.start();
                           Timer timer = new Timer();
                           timer.schedule(new TimerTask() {
                               @Override
                               public void run() {
                                   //实例化一个Message对象
                                   Message msg = Message.obtain();
                                   //Message对象的arg1参数携带音乐当前播放进度信息，类型是int
                                   seekBar.setMax(mediaPlayer.getDuration());
                                   msg.arg1 = mediaPlayer.getCurrentPosition();
                                   //使用MainActivity中的handler发送信息
                                   MainActivity.handler.sendMessage(msg);
                               }
                           }, 0, 50);

                                                          }
                                                      }
                                                  }
                );

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 117.117      * 暂停
     * 118.118
     */
    protected void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            startButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_black_24dp));
        } else {
            mediaPlayer.start();
            startButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_black_24dp));
        }
    }

    private String parseTime(int oldTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");// 时间格式
        String newTime = sdf.format(new Date(oldTime));
        return newTime;
    }

    @Override
    public void onResume() {
        super.onResume();
        myAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除绑定
        //   unbindService(conn);
    }
}




