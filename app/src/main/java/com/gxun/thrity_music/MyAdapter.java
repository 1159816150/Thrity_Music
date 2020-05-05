package com.gxun.thrity_music;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
   Context context;
   int currentPos =-1;


    public void setFocusItemPos(int pos){
        currentPos = pos;
        notifyDataSetChanged();
    }




    StringAndBitmap stringAndBitmap=new StringAndBitmap();
    private List<Song> allSongs = new ArrayList<>();
    private SongViewModel songViewModel;
    public MyAdapter(RecyclerView recyclerView,SongViewModel songViewModel) {
        this.context = recyclerView.getContext();
        this.songViewModel=songViewModel;
    }

    public List<Song> getAllSongs() {
        return allSongs;
    }

    public void setAllSongs(List<Song> allSongs) {
        this.allSongs=allSongs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        itemView = layoutInflater.inflate(R.layout.music_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Song song = allSongs.get(position);
        holder.textViewSong.setText(song.getName());
        holder.textViewSinger.setText(song.getSinger());
        holder.imageViewAlbumArt.setImageBitmap(stringAndBitmap.stringToBitmap( song.albumBip));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener!=null){
                    listener.onClick(allSongs.get(position),position);
                    if (position==currentPos){
                        holder.imageViewAlbumArt.setImageResource(R.mipmap.gnote);
                    }else {
                        holder.imageViewAlbumArt.setImageBitmap(stringAndBitmap.stringToBitmap( song.albumBip));
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return allSongs.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSong, textViewSinger, textViewTime;
        ImageView imageViewMusic,imageViewAlbumArt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSong = itemView.findViewById(R.id.songTitle);
            textViewSinger = itemView.findViewById(R.id.singer);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            imageViewMusic = itemView.findViewById(R.id.imageView);
            imageViewAlbumArt=itemView.findViewById(R.id.albumArt);
        }
    }
    private MyClickListener listener;

    //传入点击事件具体实现的方法
    public void setListener(MyClickListener listener) {
        this.listener = listener;
    }

    //声明一个点击事件的接口
    public interface MyClickListener{
        void onClick(Song song,int postion);
        //void onLongClick(String username,int postion);
    }
}
