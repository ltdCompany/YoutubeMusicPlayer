package com.example.sai.musicplayer;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
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
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

import static com.example.sai.musicplayer.MainActivity.currentWindow;
import static com.example.sai.musicplayer.MainActivity.playbackPosition;
import static com.example.sai.musicplayer.MainActivity.run;
import static com.example.sai.musicplayer.MainActivity.song;

/**
 * Created by Sai on 24-03-2018.
 */

public class NotificationAction extends BroadcastReceiver {
    Context appcontext;


    private MediaSource buildMediaSource(Uri uri) {
        OkHttpClient client = new OkHttpClient();
        return new ExtractorMediaSource.Factory(
                new OkHttpDataSourceFactory(client,"mediaplayer",null))
                .createMediaSource(uri);
    }

    public void convertAndPlay(final int i,final Context c) {
        RequestQueue requestQueue = Volley.newRequestQueue(c);
        Log.i("CONVERTURL","IN FUNCTION");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://www.convertmp3.io/fetch/?format=JSON&video=https://www.youtube.com/watch?v=" + MainActivity.musicPlayer.get(i).songid, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("CONVERTURL","INSIDE RESPONSE");
                try {
                    Log.i("CONVERTURL",response.getString("link"));
                    MainActivity.musicPlayer.get(i).songurl = response.getString("link");
                    if (song != null) {
                        song.stop();
                        song.release();
                        song = null;
                    }

                    song = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(c), new DefaultTrackSelector(), new DefaultLoadControl());
                    song.setPlayWhenReady(true);
                    Uri uri = Uri.parse(MainActivity.musicPlayer.get(i).songurl);
                    MediaSource mediaSource = buildMediaSource(uri);
                    song.prepare(mediaSource);
                    MainActivity.handler.post(run);
                    //song.seekTo(currentWindow, playbackPosition);
                    MainActivity.nowPlaying = MainActivity.musicPlayer.get(i).songname;
                    MainActivity.songplaying = true;
                    MainActivity.clicked = i;
                    c.startService(new Intent(c, PlayingService.class));
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


    public void playsong(int i,Context c) {
        //MainActivity.song = new MediaPlayer();
        if(song != null) {
            song.stop();
            song.release();
        }
        song = null;
        MainActivity.handler.removeCallbacks(run);
        convertAndPlay(i,c);
    }




    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("playpause")) {
            if(MainActivity.songplaying) {
                song.setPlayWhenReady(false);
                MainActivity.songplaying = false;
            }
            else if(!MainActivity.songplaying) {

                song.setPlayWhenReady(true);
                MainActivity.songplaying = true;
            }
            context.startService(new Intent(context,PlayingService.class).putExtra("flag",3));
        }
        else if(intent.getAction().equals("nextsong")) {

            int i;
            for(i=0;i<MainActivity.musicPlayer.size();i++)
                if(MainActivity.musicPlayer.get(i).songname.equals(MainActivity.nowPlaying))  break;

            MainActivity.handler.removeCallbacks(run);
            if(i + 1 == MainActivity.musicPlayer.size()) {
                song.stop();
                song.release();
                currentWindow = 0;
                playbackPosition = 0;
                playsong(0,context);
            }
            else if(MainActivity.repeat == 2) {
                song.seekTo(0);
            }
            else if (MainActivity.repeat != 2 && i<=MainActivity.musicPlayer.size() && i + 1 != MainActivity.musicPlayer.size()) {
                song.stop();
                song.release();
                MainActivity.currentWindow = 0;
                MainActivity.playbackPosition = 0;
                playsong(i + 1,context);

            }
            //MainActivity.handler.post(run);
            context.startService(new Intent(context,PlayingService.class));
        }

        else if(intent.getAction().equals("prevsong")) {
            if (song != null && Math.abs((int) song.getCurrentPosition()) > 5000)// {
                song.seekTo(0);
            else {
                if (MainActivity.repeat == 1 && MainActivity.clicked == 0) {
                    playsong(MainActivity.musicPlayer.size() - 1,context);
                } else if (MainActivity.repeat == 2) {
                    song.seekTo(0);
                } else if (MainActivity.repeat == 0 && MainActivity.clicked == 0) {
                    song.seekTo(0);
                } else if (MainActivity.repeat != 2 && MainActivity.clicked > 0) {
                    playsong(MainActivity.clicked - 1,context);
                }
            }
            context.startService(new Intent(context,PlayingService.class));

        }

        else if(intent.getAction().equals("close")) {
            PlayingService.notificationManager.cancel(1);
            System.exit(1);
        }
    }
}
