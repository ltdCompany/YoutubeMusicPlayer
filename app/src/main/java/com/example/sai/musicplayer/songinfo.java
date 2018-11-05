package com.example.sai.musicplayer;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.MissingFormatArgumentException;

import okhttp3.OkHttpClient;

import static com.example.sai.musicplayer.MainActivity.currentWindow;
import static com.example.sai.musicplayer.MainActivity.playbackPosition;
import static com.example.sai.musicplayer.MainActivity.run;
import static com.example.sai.musicplayer.MainActivity.songs;

public class songinfo extends AppCompatActivity implements Serializable {
    Intent info;
    Button prevsong,playpause,nextsong,addqueue;
    TextView nowplaying , songInfo;
    ArrayAdapter arrayAdapter;
    ArrayList<String> albumsongs = new ArrayList<>();
    ArrayList<String> albumsongsurl = new ArrayList<>();
    ArrayList<String> albumcoversintent = new ArrayList<>();
    ArrayList<String> albumnamesintent = new ArrayList<>();
    ArrayList<String> songstoq = new ArrayList<>();
    ArrayList<String> urlstoq = new ArrayList<>();
    ArrayList<String> alcovertoq = new ArrayList<>();
    ArrayList<String> alnametoq = new ArrayList<>();
    String songtoq,urltoq;
    int clicked;
    int letsadd = 0;
    String songfromdb;
    ArrayList<String> songsinfs = new ArrayList<>();

    ArrayList<MainActivity.MusicPlayer> toMusicPlayer = new ArrayList<>();


    public class DataWrapper implements Serializable {
        private ArrayList<MainActivity.MusicPlayer> musicPlayer;

        public DataWrapper(ArrayList<MainActivity.MusicPlayer> data) {
            this.musicPlayer = data;
        }

        public ArrayList<MainActivity.MusicPlayer> getMusicPlayer() {
            return this.musicPlayer;
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        OkHttpClient client = new OkHttpClient();
        return new ExtractorMediaSource.Factory(
                new OkHttpDataSourceFactory(client,"mediaplayer",null))
                .createMediaSource(uri);
    }

    public void savedata() {
        try {
            SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.sai.musicplayer", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = gson.toJson(MainActivity.musicPlayer);
            sharedPreferences.edit().putString("musicplayer",json).commit();

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

   /* public void backtoq(View view) {
        startActivity(new Intent(this,MainActivity.class)
                .putExtra("flag",1)
                .putExtra("name",songstoq)
                .putExtra("url",urlstoq)
                .putExtra("album",alnametoq)
                .putExtra("albumcover",alcovertoq));
    } */

    public void albumtoq(View view) {
        for(int i=0;i<albumsongs.size();i++) {
            MainActivity.MusicPlayer temp = new MainActivity.MusicPlayer();
            temp.songname = albumsongs.get(i);
            temp.songurl = albumsongsurl.get(i);
            temp.songid = "1";
            temp.albumname = info.getStringExtra("album");
            temp.albumcover = info.getStringExtra("albumcover");

            MainActivity.musicPlayer.add(temp);
            //toMusicPlayer.add(temp);
            temp = null;
            /*songstoq.add(albumsongs.get(i));
            urlstoq.add(albumsongsurl.get(i));
            alcovertoq.add(info.getStringExtra("albumcover"));
            alnametoq.add(info.getStringExtra("album"));
            MainActivity.songs.add(albumsongs.get(i));
            MainActivity.songid.add(albumsongsurl.get(i));
            MainActivity.albumcover.add(info.getStringExtra("albumcover"));
            MainActivity.albumname.add(info.getStringExtra("album")); */
        }
        Toast.makeText(this, "Album added to queue", Toast.LENGTH_SHORT).show();


    }

    public void btoq(View view) {
        savedata();
        Intent intent = new Intent(this,MainActivity.class);
        //intent.putExtra("musicplayer",new DataWrapper(toMusicPlayer));
        //intent.putExtra("flag",1);
        /*intent.putExtra("name",songstoq);
        intent.putExtra("url",urlstoq);
        intent.putExtra("albumcover",alcovertoq);
        intent.putExtra("album",alnametoq);
        intent.putExtra("flag",1); */
        startActivity(intent);
    }

    public void addtoq(View view) {

        MainActivity.MusicPlayer temp = new MainActivity.MusicPlayer();
        temp.songname = songtoq;
        temp.songurl = urltoq;
        temp.songid = "1";
        temp.albumname = info.getStringExtra("album");
        temp.albumcover = info.getStringExtra("albumcover");

        MainActivity.musicPlayer.add(temp);
        savedata();
        temp = null;
            /*songstoq.add(albumsongs.get(clicked));
            urlstoq.add(albumsongsurl.get(clicked));
            alcovertoq.add(info.getStringExtra("albumcover"));
            alnametoq.add(info.getStringExtra("album"));
            MainActivity.songs.add(albumsongs.get(clicked));
            MainActivity.songid.add(albumsongsurl.get(clicked));
            MainActivity.albumcover.add(info.getStringExtra("albumcover"));
            MainActivity.albumname.add(info.getStringExtra("album")); */

            Toast.makeText(this, "Added " + temp.songname + " to Queue", Toast.LENGTH_SHORT).show();
    }

    public class LoadUrl extends AsyncTask<Integer , Void, Void> {

        @Override
        protected Void doInBackground(final Integer... i) {
            StringRequest post = new StringRequest(Request.Method.GET, "https://youtubetoany.com/@api/json/mp3/" + MainActivity.musicPlayer.get(i[0]).songid, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);

                        JSONObject object = jsonResponse.getJSONObject("vidInfo");
                        object = object.getJSONObject("3");
                        MainActivity.musicPlayer.get(i[0]).songurl = "https:" + object.getString("dloadUrl");

                        MainActivity.song = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getApplicationContext()), new DefaultTrackSelector(), new DefaultLoadControl());
                        MainActivity.song.setPlayWhenReady(true);
                        Uri uri = Uri.parse(MainActivity.musicPlayer.get(i[0]).songurl);
                        MediaSource mediaSource = buildMediaSource(uri);
                        MainActivity.song.prepare(mediaSource);
                        MainActivity.handler.post(run);
                        //song.seekTo(currentWindow, playbackPosition);
                        MainActivity.nowPlaying = MainActivity.musicPlayer.get(i[0]).songname;
                        nowplaying.setText(MainActivity.nowPlaying);
                        MainActivity.songplaying = true;




                    } catch (Exception e) {
                        Log.i("volleyurl", e.toString());
                    }

                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("volleyurl", error.toString());

                        }
                    });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

            post.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(post);
            return null;
        }
    }

    public void playsong(int i) {
        //MainActivity.song = new MediaPlayer();
        if(MainActivity.song != null) {
            MainActivity.song.stop();
            MainActivity.song.release();
        }
        MainActivity.song = null;
        MainActivity.handler.removeCallbacks(run);
        try {
            playpause.setBackgroundResource(R.drawable.ic_pause_black_24dp);
           /* MainActivity.song.setDataSource(MainActivity.songid.get(i));
            MainActivity.song.prepare();
            MainActivity.song.start(); */
            clicked = i;
            if(!MainActivity.musicPlayer.get(i).songid.equals("1")) {
                LoadUrl loadUrl = new LoadUrl();
                loadUrl.execute(i);
            }
            else {
                MainActivity.song = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), new DefaultTrackSelector(), new DefaultLoadControl());
                MainActivity.song.setPlayWhenReady(true);
                Uri uri = Uri.parse(MainActivity.musicPlayer.get(i).songurl);
                MediaSource mediaSource = buildMediaSource(uri);
                MainActivity.song.prepare(mediaSource);
                MainActivity.handler.post(run);
                //song.seekTo(currentWindow, playbackPosition);
                MainActivity.nowPlaying = MainActivity.musicPlayer.get(i).songname;
                nowplaying.setText(MainActivity.nowPlaying);
                MainActivity.songplaying = true;
            }

        } catch (Exception e) {
            Toast.makeText(this, "No song in queue", Toast.LENGTH_SHORT).show();
        }
    }

    public void mediacontrol(View view) {
        int i;
        for(i=0;i<MainActivity.musicPlayer.size();i++)
            if(MainActivity.musicPlayer.get(i).songname.equals(MainActivity.nowPlaying))  break;
        if(view.getId()==R.id.playpause) {
            if(!MainActivity.nowPlaying.equals("")) {
                if (MainActivity.songplaying) {
                    //MainActivity.song.pause();
                    // new code
                    MainActivity.handler.removeCallbacks(run);
                    MainActivity.playbackPosition = MainActivity.song.getCurrentPosition();
                    MainActivity.currentWindow = MainActivity.song.getCurrentWindowIndex();
                    MainActivity.song.setPlayWhenReady(false);
                    //new code ends
                    MainActivity.songplaying = false;
                    playpause.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                } else {
                    //MainActivity.song.start();
                    //new code
                    /*MainActivity.song = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), new DefaultTrackSelector(), new DefaultLoadControl());
                    MainActivity.song.setPlayWhenReady(true);
                    Uri uri = Uri.parse(MainActivity.songid.get(MainActivity.songs.indexOf(MainActivity.nowPlaying)));
                    Log.i("songid",MainActivity.songid.get(MainActivity.songs.indexOf(MainActivity.nowPlaying)));
                    MediaSource mediaSource = buildMediaSource(uri);
                    MainActivity.song.prepare(mediaSource, true, false); */
                    MainActivity.song.setPlayWhenReady(true);
                    MainActivity.song.seekTo(MainActivity.currentWindow, MainActivity.playbackPosition);
                    MainActivity.handler.post(run);

                    MainActivity.songplaying = true;

                    playpause.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                }
            }
            else {
                playsong(0);
            }
        }
        else if(view.getId() == R.id.prevsong) {
            MainActivity.handler.removeCallbacks(run);
            if (!MainActivity.nowPlaying.equals("")) {
                if (Math.abs((int) MainActivity.song.getCurrentPosition()) > 5000)// {
                    MainActivity.song.seekTo(0);
                else {
                    if (MainActivity.repeat == 1 && i == 0) {
                        MainActivity.song.stop();
                        MainActivity.song.release();
                        playsong(MainActivity.musicPlayer.size() - 1);
                    } else if (MainActivity.repeat == 2) {
                        MainActivity.song.seekTo(0);
                    }
                    else if (MainActivity.repeat == 0 && i == 0) {
                        MainActivity.song.seekTo(0);
                    } else if (MainActivity.repeat != 2 && i > 0) {
                        MainActivity.song.stop();
                        MainActivity.song.release();
                        playsong(i - 1);
                    }
                }
            }
            MainActivity.handler.post(run);
        }
        else if(view.getId() == R.id.nextsong) {
            MainActivity.handler.removeCallbacks(run);
            if(MainActivity.repeat == 1 && i + 1 == MainActivity.musicPlayer.size()) {
                MainActivity.song.stop();
                MainActivity.song.release();
                currentWindow = 0;
                playbackPosition = 0;
                playsong(0);
            }
            else if(MainActivity.repeat == 2) {
                MainActivity.song.seekTo(0);
            }
            else if (MainActivity.repeat != 2 && i<=MainActivity.musicPlayer.size() && i + 1 != MainActivity.musicPlayer.size()) {
                MainActivity.song.stop();
                MainActivity.song.release();
                currentWindow = 0;
                playbackPosition = 0;
                playsong(i + 1);

            }
            MainActivity.handler.post(run);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songinfo);

        info = getIntent();

        if(!info.getStringExtra("name").equals("albumnotsong")) {
            albumsongs.add(info.getStringExtra("name"));
            albumsongsurl.add(info.getStringExtra("url"));
            alcovertoq.add(info.getStringExtra("albumcover"));
            alnametoq.add(info.getStringExtra("album"));
        }
       // albumcoversintent.add(info.getStringExtra("albumcover"));
       // albumnamesintent.add(info.getStringExtra("album"));


        ImageView albumCover = findViewById(R.id.albumcover);
        Picasso.get()
                .load(info.getStringExtra("albumcover"))
                .placeholder(R.drawable.placeholder)
                .into(albumCover);

        prevsong = findViewById(R.id.prevsong);
        playpause = findViewById(R.id.playpause);
        nextsong = findViewById(R.id.nextsong);
        addqueue = findViewById(R.id.addqueue);
        ListView albumlist = findViewById(R.id.albumlist);
        arrayAdapter = new ArrayAdapter(getApplicationContext(),R.layout.albumlist_layout,albumsongs);
        albumlist.setAdapter(arrayAdapter);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("/songs64");

        db.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                songfromdb = dataSnapshot.getKey();

                    final DatabaseReference foundsong = FirebaseDatabase.getInstance().getReference("/songs64/"+songfromdb);

                    foundsong.orderByChild("album").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                           if(dataSnapshot.getValue().toString().equals(info.getStringExtra("album"))) {

                               foundsong.orderByKey().addChildEventListener(new ChildEventListener() {
                                   @Override
                                   public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                       songsinfs.add(dataSnapshot.getValue().toString());

                                     // if(songsinfs.size()>2)

                                       if(songsinfs.size() % 4 == 0) {

                                           if(!songsinfs.get(2 + (letsadd*4)).equals(info.getStringExtra("name"))) {

                                               albumsongs.add(songsinfs.get(2 + (letsadd * 4)));
                                               albumsongsurl.add(songsinfs.get(3 + (letsadd * 4)));
                                               arrayAdapter.notifyDataSetChanged();
                                           }
                                           letsadd++;

                                       }


                                       Log.i("cometwo",dataSnapshot.getValue().toString());
                                   }

                                   @Override
                                   public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                   }

                                   @Override
                                   public void onChildRemoved(DataSnapshot dataSnapshot) {

                                   }

                                   @Override
                                   public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                   }

                                   @Override
                                   public void onCancelled(DatabaseError databaseError) {

                                   }
                               });

                                Log.i("gotit",dataSnapshot.getValue().toString());

                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                //}

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        /*DatabaseReference db = FirebaseDatabase.getInstance().getReference("/albums64/"+info.getStringExtra("album"));

        db.orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                albumsongs.add(dataSnapshot.getKey());
                albumsongsurl.add(dataSnapshot.getValue().toString());
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); */

        albumlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (int j = 0; j < adapterView.getChildCount(); j++)
                    adapterView.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                view.setBackgroundResource(R.drawable.songselectedinalbum_color);
                clicked = i;
                songInfo.setText(albumsongs.get(i) + "\nAlbum:" + info.getStringExtra("album"));
                songtoq = albumsongs.get(i);
                urltoq = albumsongsurl.get(i);
            }
        });



        songInfo = findViewById(R.id.songinfo);
        nowplaying = findViewById(R.id.nowplaying);

        if(!info.getStringExtra("name").equals("albumnotsong"))
            songInfo.setText(info.getStringExtra("name") + "\nAlbum:" + info.getStringExtra("album"));
        else
            songInfo.setText("Album:" + info.getStringExtra("album"));


        if(!MainActivity.nowPlaying.equals("")) {
            nowplaying.setText(MainActivity.nowPlaying);
        }

        else nowplaying.setText("Play a Song");

        if(MainActivity.songplaying)
            playpause.setBackgroundResource(R.drawable.ic_pause_black_24dp);



    }
}
