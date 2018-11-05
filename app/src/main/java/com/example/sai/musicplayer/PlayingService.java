package com.example.sai.musicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Sai on 24-03-2018.
 */

public class PlayingService extends Service {

    static RemoteViews remoteViews;
    static Notification notification;
    static NotificationManager notificationManager;
    Bitmap albumcovernotif;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
/*


    //MediaPlayer song;
    //SeekBar seek;
    boolean songplaying=false;
    ImageView playbutton,pausebutton;
    TextView timerun,timeleft,searchmusic;
    long min,sec;
    ArrayList<String> songid=new ArrayList<String>();
    ArrayList<String> songs;
    ArrayAdapter arrayAdapter;
    //SharedPreferences sharedPreferences;
    //static String nowPlaying = "";


    public void playorpause(View view) {
        if(songplaying) {
            MainActivity.song.pause();
            songplaying=false;
            playpausebutton();
        }
        else {
            MainActivity.song.start();
            songplaying=true;
            playpausebutton();
        }
    }

    public void playpausebutton() {
        if(songplaying) {
            playbutton.setVisibility(View.INVISIBLE);
            pausebutton.setVisibility(View.VISIBLE);
        }

        else {
            playbutton.setVisibility(View.VISIBLE);
            pausebutton.setVisibility(View.INVISIBLE);
        }
    }

   /* public void timeruntext() {
        String MIN,SEC;
        min= TimeUnit.MILLISECONDS.toMinutes(song.getCurrentPosition());
        sec= TimeUnit.MILLISECONDS.toSeconds(song.getCurrentPosition());
        MIN=String.valueOf(min);
        if(sec>=60) sec=sec%60;
        SEC=String.valueOf(sec);
        if(min<10) MIN="0" + String.valueOf(min);
        if(sec<10) SEC="0" + String .valueOf(sec);
        timerun.setText(MIN + ":" + SEC);
    } */

   /* public void timelefttext() {
        String MIN,SEC;
        min= TimeUnit.MILLISECONDS.toMinutes(MainActivity.song.getDuration()-MainActivity.song.getCurrentPosition());
        sec= TimeUnit.MILLISECONDS.toSeconds(MainActivity.song.getDuration()-MainActivity.song.getCurrentPosition());
        MIN=String.valueOf(min);
        if(sec>=60) sec=sec%60;
        SEC=String.valueOf(sec);
        if(min<10) MIN="0" + String.valueOf(min);
        if(sec<10) SEC="0" + String .valueOf(sec);
        Log.i("timeleft",MIN.toString());
        MainActivity.timeleft.setText(MIN + ":" + SEC);
        //timeleft.setText(MIN + ":" + SEC);

    }





    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1,new Notification());

        NotificationManager notificationManager=(NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "some_channel_id";
        CharSequence channelName = "Some Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent pauseIntent = new Intent(getApplicationContext(),NotificationAction.class).setAction("pause");
        Intent playIntent = new Intent(getApplicationContext(),NotificationAction.class).setAction("play");
        Intent gotoq=new Intent(getApplicationContext(),MainActivity.class).putExtra("flag",2);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),1,gotoq,0);
        PendingIntent pausePendingIntent = pendingIntent.getBroadcast(getApplicationContext(),0,pauseIntent,0);
        PendingIntent playPendingIntent = pendingIntent.getBroadcast(getApplicationContext(),1,playIntent,0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentTitle(MainActivity.nowPlaying)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_pause_black_24dp,"PAUSE",pausePendingIntent)
                .addAction(R.drawable.ic_play_arrow_black_24dp,"PLAY",playPendingIntent);
        mBuilder.setOngoing(true);
        notificationManager.notify(1,mBuilder.build());

        return START_STICKY;
    } */

   public class Notifimage extends AsyncTask<Intent,Void,Void> {

       @Override
       protected Void doInBackground(Intent... intents) {
           try {
               if(intents[0].getIntExtra("flag",0) != 3) {
                   URL url = new URL(MainActivity.musicPlayer.get(MainActivity.clicked).albumcover);
                   HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                   connection.setDoInput(true);
                   connection.connect();
                   InputStream input = connection.getInputStream();
                   albumcovernotif = BitmapFactory.decodeStream(input);
                   remoteViews.setImageViewBitmap(R.id.albumcovernotif, albumcovernotif);
               }
               else
                   remoteViews.setImageViewBitmap(R.id.albumcovernotif, albumcovernotif);

               Palette palette = Palette.from(albumcovernotif).generate();
               remoteViews.setInt(R.id.layout,"setBackgroundColor",palette.getDarkVibrantColor(0) != 0 ? palette.getDarkVibrantColor(0) : palette.getDarkMutedColor(0));


               startForeground(1,new Notification());

               notificationManager=(NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
               String channelId = "com.example.sai.musicplayer";
               CharSequence channelName = "MusicPlayer";
               int importance = NotificationManager.IMPORTANCE_LOW;
               NotificationChannel notificationChannel = null;
               if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                   notificationChannel = new NotificationChannel(channelId, channelName, importance);
                   notificationManager.createNotificationChannel(notificationChannel);
               }


               //Intent pauseIntent = new Intent(getApplicationContext(),NotificationAction.class).setAction("pause");

               Intent nextSongIntent = new Intent(getApplicationContext(),NotificationAction.class).setAction("nextsong");
               Intent prevSongIntent = new Intent(getApplicationContext(),NotificationAction.class).setAction("prevsong");

               Intent playPauseIntent = new Intent(getApplicationContext(),NotificationAction.class).setAction("playpause");
               Intent gotoq=new Intent(getApplicationContext(),MainActivity.class).putExtra("flag",2);
               PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),1,gotoq,0);
               //PendingIntent pausePendingIntent = pendingIntent.getBroadcast(getApplicationContext(),0,pauseIntent,0);
               PendingIntent playPausePendingIntent = pendingIntent.getBroadcast(getApplicationContext(),1,playPauseIntent,0);

               Intent closeIntent = new Intent(getApplicationContext(),NotificationAction.class).setAction("close");
               PendingIntent closePendingIntent = pendingIntent.getBroadcast(getApplicationContext(),1,closeIntent,0);
               PendingIntent nextSongPendingIntent = pendingIntent.getBroadcast(getApplicationContext(),1,nextSongIntent,0);
               PendingIntent prevSongPendingInetnt = pendingIntent.getBroadcast(getApplicationContext(),1,prevSongIntent,0);
               remoteViews.setOnClickPendingIntent(R.id.playpausenotif,playPausePendingIntent);
               remoteViews.setOnClickPendingIntent(R.id.closenotif,closePendingIntent);
               remoteViews.setOnClickPendingIntent(R.id.nextsongnotif,nextSongPendingIntent);
               remoteViews.setOnClickPendingIntent(R.id.closenotif,closePendingIntent);
               remoteViews.setOnClickPendingIntent(R.id.prevsongnotif,prevSongPendingInetnt);

               NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                       .setSmallIcon(android.R.drawable.sym_def_app_icon)
                       .setContentIntent(pendingIntent)
                       .setPriority(NotificationCompat.PRIORITY_LOW)
                       .setCustomBigContentView(remoteViews);
               mBuilder.setOngoing(true);
               notification = mBuilder.build();
               notificationManager.notify(1,notification);



           } catch (Exception e) {
               Log.i("bitmapconversionerror",e.toString());
           }
           return null;
       }
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {

       remoteViews = new RemoteViews(getPackageName(),R.layout.notification);
       remoteViews.setTextViewText(R.id.songname,MainActivity.musicPlayer.get(MainActivity.clicked).songname);
       if(MainActivity.songplaying)
           remoteViews.setImageViewResource(R.id.playpausenotif,R.drawable.exo_controls_pause);
       else if(!MainActivity.songplaying)
           remoteViews.setImageViewResource(R.id.playpausenotif,R.drawable.exo_controls_play);

       Notifimage notifimage = new Notifimage();
       notifimage.execute(intent);
       //notifimage = new Notifimage(intent);
       //notifimage.execute();


       //Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/musicplayer-3d570.appspot.com/o/Ae%20Dil%20Hai%20Mushkil%20(2016)%2FCover.jpg?alt=media&token=66e240b4-908e-4b60-9ba2-0c0f7868b39f");

      /* try {
           URL url = new URL(MainActivity.musicPlayer.get(MainActivity.clicked).albumcover);
           HttpURLConnection connection = (HttpURLConnection) url.openConnection();
           connection.setDoInput(true);
           connection.connect();
           InputStream input = connection.getInputStream();
           Bitmap albumcovernotif = BitmapFactory.decodeStream(input);
           remoteViews.setImageViewBitmap(R.id.albumcovernotif,albumcovernotif);
       } catch (Exception e) {
           Log.i("bitmapconversionerror",e.toString());
       } */

       /// /remoteViews.setImageViewUri(R.id.albumcovernotif,uri);
       //remoteViews.setImageViewResource(R.id.albumcovernotif,R.drawable.exo_controls_next);









       return START_STICKY;
   }

}
