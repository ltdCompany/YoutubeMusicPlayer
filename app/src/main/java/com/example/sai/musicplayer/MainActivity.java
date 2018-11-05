package com.example.sai.musicplayer;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.MutableShort;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.protyposis.android.mediaplayer.UriSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    static ExoPlayer song;
    static SeekBar seek;
    static boolean songplaying = false, longclick = false, shuffle = false, songselected = false;
    Button playpause, nextsong, prevsong, shufflebutton, repeatbutton;
    static TextView timerun, timeleft, searchmusic;
    long min, sec;
    static ArrayList<String> songid = new ArrayList<String>();
    static ArrayList<String> songs, albumcover, albumname;
    customadapter ca;
    SharedPreferences sharedPreferences;
    static String nowPlaying = "", yturl;
    Context context = this;
    static int clicked, repeat = 0, randomsong, songclicked, currentWindow;
    static ListView list;
    static Random random = new Random();
    static long playbackPosition;
    final static Handler handler = new Handler();
    static Runnable run;
    static ArrayList<MusicPlayer> musicPlayer = new ArrayList<>();
    MusicPlayer temp = new MusicPlayer();

    public static class MusicPlayer {

        String songurl, songname, albumname, albumcover, songid, token;

        MusicPlayer(String songurl, String songname, String albumname, String albumcover, String songid, String token) {

        }

        MusicPlayer() {

        }
    }



    //  public void searchmusic(View view) {
    //      startActivity(new Intent(this,search.class).putExtra("songlist",songs).putExtra("songurl",songid));
    //  }

    private MediaSource buildMediaSource(Uri uri) {
        OkHttpClient client = new OkHttpClient();
        return new ExtractorMediaSource.Factory(new OkHttpDataSourceFactory(client, "mediaplayer", null)).createMediaSource(uri);
    }

    public class ReloadUrl extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(final Integer... i) {
            Log.i("CONVERTURL","WORKING");
            StringRequest post = new StringRequest(Request.Method.GET, "www.convertmp3.io/fetch/?format=JSON&video=https://www.youtube.com/watch?v=" + musicPlayer.get(i[0]).songid, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.i("CONVERTURL","WORKING");
                        JSONObject jsonResponse = new JSONObject(response);

                        JSONObject object = jsonResponse.getJSONObject("");
                        JSONArray array = object.getJSONArray("");
                        object = array.getJSONObject(0);
                        //object = object.getJSONObject("3");
                        Log.i("CONVERTURL",object.getString("link"));
                        musicPlayer.get(i[0]).songurl = Uri.parse(object.getString("link")).toString();

                        Log.i("party", musicPlayer.get(i[0]).songurl);

                        if (song != null) {
                            song.stop();
                            song.release();
                            song = null;
                        }

                        song = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getApplicationContext()), new DefaultTrackSelector(), new DefaultLoadControl());
                        song.setPlayWhenReady(true);
                        Uri uri = Uri.parse(musicPlayer.get(i[0]).songurl);
                        MediaSource mediaSource = buildMediaSource(uri);
                        song.prepare(mediaSource);
                        handler.post(run);
                        //song.seekTo(currentWindow, playbackPosition);
                        nowPlaying = musicPlayer.get(i[0]).songname;
                        songplaying = true;
                        startService(new Intent(getApplicationContext(), PlayingService.class));


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

    public void pleaseplay(final int i) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        Log.i("CONVERTURL","IN FUNCTION");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://www.convertmp3.io/fetch/?format=JSON&video=https://www.youtube.com/watch?v=" + musicPlayer.get(i).songid, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("CONVERTURL","INSIDE RESPONSE");
                try {
                    Log.i("CONVERTURL",response.getString("link"));
                    musicPlayer.get(i).songurl = response.getString("link");
                    if (song != null) {
                        song.stop();
                        song.release();
                        song = null;
                    }

                    song = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getApplicationContext()), new DefaultTrackSelector(), new DefaultLoadControl());
                    song.setPlayWhenReady(true);
                    Uri uri = Uri.parse(musicPlayer.get(i).songurl);
                    MediaSource mediaSource = buildMediaSource(uri);
                    song.prepare(mediaSource);
                    handler.post(run);
                    //song.seekTo(currentWindow, playbackPosition);
                    nowPlaying = musicPlayer.get(i).songname;
                    songplaying = true;
                    startService(new Intent(getApplicationContext(), PlayingService.class));
                } catch (JSONException e) {
                    Log.i("CONVERTURL",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("CONVERTURL","FAIL");

            }
        });


        requestQueue.add(jsonObjectRequest);

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));





    }

    public void playsong(final int i) {
        if (song != null) {
            song.stop();
            song.release();
            song = null;
        }
        clicked = i;
        Log.i("CONVERTURL",musicPlayer.get(i).songid);
        //highlightlist(clicked);
        ReloadUrl reloadUrl = new ReloadUrl();
        //reloadUrl.execute(i);
        pleaseplay(i);
       /* if (!musicPlayer.get(i).songid.equals("1")) {
            reloadUrl.execute(i);
        } else {
            if (song != null) {
                reloadUrl.cancel(true);
                song.stop();
                song.release();
                song = null;
            }
            song = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), new DefaultTrackSelector(), new DefaultLoadControl());
            song.setPlayWhenReady(true);
            Uri uri = Uri.parse(musicPlayer.get(i).songurl);
            MediaSource mediaSource = buildMediaSource(uri);
            song.prepare(mediaSource);
            handler.post(run);
            //song.seekTo(currentWindow, playbackPosition);
            nowPlaying = musicPlayer.get(i).songname;
            songplaying = true;
            startService(new Intent(getApplicationContext(), PlayingService.class));
        } */
    }

    private void releasePlayer() {
        if (song != null) {
            handler.removeCallbacks(run);
            playbackPosition = song.getCurrentPosition();
            currentWindow = song.getCurrentWindowIndex();
            //playWhenReady = song.getPlayWhenReady();
            song.release();
            song = null;
        }
    }

    public void repeatshufflebutton(int repeat, boolean shuffle) {
        switch (repeat) {
            case 0:
                repeatbutton.setBackgroundResource(R.drawable.ic_repeat_black_24dp);
                break;
            case 1:
                repeatbutton.setBackgroundResource(R.drawable.repeatclicked);
                break;
            case 2:
                shuffle = false;
                repeatbutton.setBackgroundResource(R.drawable.repeat1_clicked);
                break;
        }

        if (shuffle)
            shufflebutton.setBackgroundResource(R.drawable.shuffle_clicked);
        else if (!shuffle)
            shufflebutton.setBackgroundResource(R.drawable.ic_shuffle_black_24dp);
    }

    public void shuffleorrepeat(View view) {
        if (view.getId() == R.id.shuffle) {
            if (repeat == 2) {
                repeat = 0;
                shuffle = true;
            } else if (shuffle) {
                shuffle = false;
            } else if (!shuffle) {
                shuffle = true;
            }
            repeatshufflebutton(repeat, shuffle);
        } else if (view.getId() == R.id.repeat) {
            if (repeat < 2) repeat++;
            else {
                repeat = 0;
                shuffle = false;
            }
            repeatshufflebutton(repeat, shuffle);
        }
    }

    public void highlightlist(int i) {
        View highlightview;
        for (int j = 0; j < list.getChildCount(); j++)
            list.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
        highlightview = list.getChildAt(i);
        highlightview.setBackgroundResource(R.drawable.songselectedinq_color);
    }

   /* public void playsong(int i) {
        song = new MediaPlayer();
        try {
            nowPlaying = songs.get(i);
            song.setDataSource(MainActivity.songid.get(i));
            song.prepareAsync();
            song.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    seek.setMax(mediaPlayer.getDuration());
                    songplaying = true;
                    startService(new Intent(getApplicationContext(), PlayingService.class));
                    //playpause.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
                }
            });
           // nowplaying.setText(MainActivity.nowPlaying);
        } catch (Exception e) {
            Toast.makeText(this, "No song in queue", Toast.LENGTH_SHORT).show();
            song = null;
        }
    } */

    public void nextorprev(View view) {
        if (view.getId() == R.id.nextsong) {
            if (shuffle) {
                randomsong = random.nextInt(musicPlayer.size());
                while (clicked == randomsong)
                    randomsong = random.nextInt(musicPlayer.size());
                playsong(randomsong);
            } else {
                if (repeat == 1 && clicked + 1 == musicPlayer.size()) {
                    currentWindow = 0;
                    playbackPosition = 0;
                    playsong(0);
                } else if (repeat == 2) {
                    song.seekTo(0);
                } else if (repeat != 2 && clicked <= musicPlayer.size() && clicked + 1 != musicPlayer.size()) {
                    currentWindow = 0;
                    playbackPosition = 0;
                    playsong(clicked + 1);

                }
            }
        } else if (view.getId() == R.id.prevsong) {
            if (!nowPlaying.equals("")) {
                if (song != null && Math.abs((int) song.getCurrentPosition()) > 5000)// {
                    song.seekTo(0);
                else {
                    if (repeat == 1 && clicked == 0) {
                        playsong(musicPlayer.size() - 1);
                    } else if (repeat == 2) {
                        song.seekTo(0);
                    } else if (repeat == 0 && clicked == 0) {
                        song.seekTo(0);
                    } else if (repeat != 2 && clicked > 0) {
                        playsong(clicked - 1);
                    }
                }
            }
        }
    }

    public void playnextsong() {
        handler.removeCallbacks(run);
        song.stop();
        song.release();
        song = null;
        Log.i("nextsong", "function");
        if (clicked + 1 == musicPlayer.size() && repeat == 1)
            playsong(0);
        else if (clicked + 1 < musicPlayer.size() && repeat != 2)
            playsong(clicked + 1);
        else if (repeat == 2)
            playsong(clicked);
    }

    public void playorpause(View view) {
        // if (song != null) {
        if (songplaying) {
            song.setPlayWhenReady(false);
            currentWindow = song.getCurrentWindowIndex();
            playbackPosition = song.getCurrentPosition();
            songplaying = false;
            playpausebutton();
        } else {
            if (nowPlaying.equals(""))
                playsong(0);
            else {
                //song.seekTo(playbackPosition);
                song.setPlayWhenReady(true);

            }
            songplaying = true;
            playpausebutton();
        }
        startService(new Intent(this, PlayingService.class).putExtra("flag", 3));
    }

    public void playpausebutton() {
        if (songplaying) {
            playpause.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
        } else {
            playpause.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
        }
    }

    public void timeruntext() {
        String MIN, SEC;
        min = TimeUnit.MILLISECONDS.toMinutes(Math.abs((int) song.getCurrentPosition()));
        sec = TimeUnit.MILLISECONDS.toSeconds(Math.abs((int) song.getCurrentPosition()));
        MIN = String.valueOf(min);
        if (sec >= 60) sec = sec % 60;
        SEC = String.valueOf(sec);
        if (min < 10) MIN = "0" + String.valueOf(min);
        if (sec < 10) SEC = "0" + String.valueOf(sec);
        timerun.setText(MIN + ":" + SEC);
    }

    public void timelefttext() {
        String MIN, SEC;
        min = TimeUnit.MILLISECONDS.toMinutes(Math.abs((int) song.getDuration()) - Math.abs(((int) song.getCurrentPosition())));
        sec = TimeUnit.MILLISECONDS.toSeconds(Math.abs((int) song.getDuration()) - Math.abs((int) song.getCurrentPosition()));
        MIN = String.valueOf(min);
        if (sec >= 60) sec = sec % 60;
        SEC = String.valueOf(sec);
        if (min < 10) MIN = "0" + String.valueOf(min);
        if (sec < 10) SEC = "0" + String.valueOf(sec);
        timeleft.setText(MIN + ":" + SEC);

    }

    protected void loaddata() {
        sharedPreferences = this.getSharedPreferences("com.example.sai.musicplayer", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("musicplayer", null);
        Type type = new TypeToken<ArrayList<MusicPlayer>>() {
        }.getType();
        //ArrayList<MusicPlayer> newMusicPlayer = (ArrayList<MusicPlayer>) ObjectSerializer.deserialize(sharedPreferences.getString("musicplayer",ObjectSerializer.serialize(new ArrayList<MusicPlayer>())));
        musicPlayer = gson.fromJson(json, type);

        if (musicPlayer == null)
            musicPlayer = new ArrayList<MusicPlayer>();
        ca.notifyDataSetChanged();
        //if(!nowPlaying.equals(""))
        //highlightlist(clicked);
    }

    protected void savedata() {

        try {
            Gson gson = new Gson();
            String json = gson.toJson(musicPlayer);
            sharedPreferences.edit().putString("musicplayer", json).commit();

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this,LogIn.class));

        } else if (currentUser != null) {
            Intent gintent = getIntent();
            if(gintent.getIntExtra("flag",0) == 121)
                Snackbar.make(findViewById(R.id.playpause),"\nWelcome " + currentUser.getDisplayName() + "\n",Snackbar.LENGTH_SHORT).show();


            Toolbar actionbar = (Toolbar) findViewById(R.id.actionbar);
            setSupportActionBar(actionbar);


            //searchmusic = findViewById(R.id.searchmusic);

            playpause = findViewById(R.id.playpause);
            nextsong = findViewById(R.id.nextsong);
            prevsong = findViewById(R.id.prevsong);
            shufflebutton = findViewById(R.id.shuffle);
            repeatbutton = findViewById(R.id.repeat);
            timerun = findViewById(R.id.timerun);
            timeleft = findViewById(R.id.timeleft);

            playpausebutton();

            seek = findViewById(R.id.seekBar5);

            list = findViewById(R.id.list);
            songs = new ArrayList<String>();
            albumcover = new ArrayList<>();
            albumname = new ArrayList<>();
            //arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,songs);
            //ca = new customadapter(this,songs);
            ca = new customadapter(this, musicPlayer);
            //list.setAdapter(arrayAdapter);
            list.setAdapter(ca);

            loaddata();
            repeatshufflebutton(repeat, shuffle);

            if (repeat == 2) {
                shuffle = false;
                shufflebutton.setBackgroundResource(R.drawable.ic_shuffle_black_24dp);
            }



            // if (songselected) {
            //    startService(new Intent(getApplicationContext(),highlightsonginq.class).putExtra("pos",songclicked));
            // }

            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    timelefttext();
                    timeruntext();

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (song != null && songplaying)
                        song.seekTo(seekBar.getProgress());
                    else if (song != null && !songplaying)
                        playbackPosition = seekBar.getProgress();
                    seekBar.setProgress(seekBar.getProgress());

                }
            });

            /*Intent q = getIntent();
            try {

                if (q.getIntExtra("flag", 0) == 1) {
                    songinfo.DataWrapper dw = (songinfo.DataWrapper) getIntent().getSerializableExtra("musicplayer");
                    ArrayList<MusicPlayer> newMusicPlayer = dw.getMusicPlayer();
                    for (int i = 0; i < newMusicPlayer.size(); i++)
                        musicPlayer.add(newMusicPlayer.get(i));

                    ca.notifyDataSetChanged();
                    if(songplaying)
                        highlightlist(clicked);
                    savedata();
                }
                ca.notifyDataSetChanged();
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            } */


            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    if (!longclick) {
                        songclicked = i;
                        songselected = true;
                        //startService(new Intent(getApplicationContext(),highlightsonginq.class).putExtra("pos",i));
                        // stopService(new Intent(getApplicationContext(),highlightsonginq.class));
                   /* for (int j = 0; j < adapterView.getChildCount(); j++)
                        adapterView.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                    view.setBackgroundResource(R.drawable.songselectedinq_color); */


                        if (song != null) {
                            song.stop();
                            song.release();
                        }
                        currentWindow = 0;
                        playbackPosition = 0;


                        playsong(i);

                    }
                }
            });


            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    longclick = true;
                    clicked = i;
                    handler.removeCallbacks(run);
                    new AlertDialog.Builder(context)
                            .setIcon(R.drawable.ic_remove_from_queue_black_24dp)
                            .setTitle("Remove " + musicPlayer.get(i).songname + "?")
                            .setMessage("Are you sure you want to remove " + musicPlayer.get(i).songname + " from queue?")
                            .setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // if(!usingMediaPlayer) {
                                    //if (songs.get(clicked).equals(nowPlaying) && song != null) {
                                    if (musicPlayer.get(clicked).songname.equals(nowPlaying) && song != null) {
                                        //handler.removeCallbacks(run);
                                        song.release();
                                        //song = new MediaPlayer();
                                        songplaying = false;
                                        seek.setProgress(0);
                                        timerun.setText("00:00");
                                        timelefttext();
                                        playpausebutton();
                                    }
                                    //}
                               /* else if(usingMediaPlayer) {
                                    //if (songs.get(clicked).equals(nowPlaying) && mediaPlayer != null) {
                                    if (musicPlayer.get(clicked).songname.equals(nowPlaying) && mediaPlayer != null) {
                                        mediaPlayer.stop();
                                        //handler.removeCallbacks(run)
                                        //song = new MediaPlayer();
                                        songplaying = false;
                                        seek.setProgress(0);
                                        timerun.setText("00:00");
                                        timelefttext();
                                        mediaPlayer.release();
                                        mediaPlayer = null;
                                        playpausebutton();
                                    }
                                }*/
                                /*songs.remove(clicked);
                                songid.remove(clicked);
                                albumcover.remove(clicked);
                                albumname.remove(clicked); */

                                    musicPlayer.remove(clicked);

                                    //arrayAdapter.notifyDataSetChanged();
                                    ca.notifyDataSetChanged();
                                    savedata();
                                    nowPlaying = "";
                               /* try {
                                    sharedPreferences.edit().putString("songs",ObjectSerializer.serialize(songs)).commit();
                                    sharedPreferences.edit().putString("songid",ObjectSerializer.serialize(songid)).commit();
                                    sharedPreferences.edit().putString("albumcover",ObjectSerializer.serialize(albumcover)).commit();
                                    sharedPreferences.edit().putString("albumname",ObjectSerializer.serialize(albumname)).commit();
                                } catch (IOException e) {
                                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                } */
                                    longclick = false;
                                }
                            })
                            .setNegativeButton("CANCEL", null)
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    if (song != null)
                                        handler.post(run);
                                }
                            })
                            .show();
                    return Boolean.parseBoolean(null);
                }
            });


            run = new Runnable() {
                @Override
                public void run() {
               /* if(song != null) {
                    if (Math.abs((int) song.getCurrentPosition()) + 1000 >= Math.abs((int) song.getDuration())) {
                        int index = songs.indexOf(nowPlaying);
                        if (index + 1 == songs.size()) {
                            songplaying = false;
                            playpausebutton();
                            seek.setProgress(0);
                            timerun.setText("00:00");
                            timelefttext();
                        } else {
                            song.release();
                            song = null;
                            playsong(index + 1);
                        }
                    } */
                    /*if(usingMediaPlayer && mediaPlayer != null) {
                        seek.setMax(mediaPlayer.getDuration());
                        seek.setProgress(mediaPlayer.getCurrentPosition());
                    } */
                    if (/*!usingMediaPlayer && */song != null) {
                        if (song.getCurrentPosition() + 1000 > song.getDuration() && song.getCurrentPosition() > 1000)
                            playnextsong();
                        else {
                            seek.setMax((int) song.getDuration());
                            seek.setProgress(Math.abs((int) song.getCurrentPosition()));
                        }
                    }
                    playpausebutton();

                    handler.postDelayed(this, 100);
                }
            };


        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.queue_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            case R.id.about:
                startActivity(new Intent(this,AboutPage.class));
                return true;
            case R.id.searchmusic:
                startActivity(new Intent(this,search.class));
                return true;
            case R.id.signout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this,LogIn.class));
                return true;
                default:
                    return super.onOptionsItemSelected(item);



        }
    }
}


