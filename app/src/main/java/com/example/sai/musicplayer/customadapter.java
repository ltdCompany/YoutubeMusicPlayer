package com.example.sai.musicplayer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Sai on 28-03-2018.
 */

public class customadapter extends BaseAdapter {
    Activity context;

    public customadapter(MainActivity mainActivity, ArrayList<MainActivity.MusicPlayer> musicPlayer) {

        this.context = mainActivity;

    }


    @Override
    public int getCount() {
        return MainActivity.musicPlayer.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        view = layoutInflater.inflate(R.layout.customlist,null);
        TextView songname = view.findViewById(R.id.songname);
        TextView albumname = view.findViewById(R.id.albumname);
        ImageView albumcover = view.findViewById(R.id.albumcover);

       /* songname.setText(MainActivity.songs.get(i));
        albumname.setText(MainActivity.albumname.get(i));
        Picasso.get()
                .load(MainActivity.albumcover.get(i))
                .into(albumcover); */

       songname.setText(MainActivity.musicPlayer.get(i).songname);
       albumname.setText((MainActivity.musicPlayer.get(i).albumname));
       Picasso.get()
               .load(MainActivity.musicPlayer.get(i).albumcover)
               .into(albumcover);


        return view;
    }
}
