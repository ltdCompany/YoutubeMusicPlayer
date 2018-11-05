package com.example.sai.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class search extends AppCompatActivity implements Serializable {
    EditText search;
    songurl surl;
    ArrayList<String> searchresults;
    ArrayList<String> searchresultsalbum = new ArrayList<>();
    ArrayList<String> searchresultsyoutube = new ArrayList<>();
    ArrayList<String> songurls = new ArrayList<>();
    ArrayList<String> albumcover = new ArrayList<>();
    ArrayList<String> albumname = new ArrayList<>();
    ArrayList<String> ytsongids = new ArrayList<>();
    ArrayList<String> yttokens = new ArrayList<>();
    String ytsongurl = new String();
    ArrayList<String> ytalbumcover = new ArrayList<>();
    ArrayList<String> ytalbumname = new ArrayList<>();
    Intent addtoq;
    String songfromdb;
    ArrayList<String> songsinfs = new ArrayList<>();
    int letsadd = 0;

    public class DataWrapper implements Serializable {
        private ArrayList<MainActivity.MusicPlayer> musicPlayer;

        public DataWrapper(ArrayList<MainActivity.MusicPlayer> data) {
            this.musicPlayer = data;
        }

        public ArrayList<MainActivity.MusicPlayer> getMusicPlayer() {
            return this.musicPlayer;
        }
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

    public static class songurl {
        public String url;
        public String name;
        public String albumcover;
        public String album;
        public songurl(String url,String name,String albumcover,String album){

        }
        public songurl(){

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        addtoq = new Intent(this,songinfo.class);

        final TextView songtext = findViewById(R.id.songtext);
        songtext.setVisibility(View.INVISIBLE);

        search = findViewById(R.id.search);
        final ListView searchlist = findViewById(R.id.searchlist);
        searchresults = new ArrayList<>();
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,searchresults);
        searchlist.setAdapter(arrayAdapter);

        final TextView albumtext = findViewById(R.id.albumtext);
        albumtext.setVisibility(View.INVISIBLE);
        final ListView searchlistalbum = findViewById(R.id.searchlistalbum);
        final ArrayAdapter albumArrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,searchresultsalbum);
        searchlistalbum.setAdapter(albumArrayAdapter);

        final TextView youTubeText = findViewById(R.id.youtubetext);
        youTubeText.setVisibility(View.INVISIBLE);
        final ListView searchlistyoutube = findViewById(R.id.searchyoutubelist);
        final ArrayAdapter youtubeArrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,searchresultsyoutube);
        searchlistyoutube.setAdapter(youtubeArrayAdapter);



        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("/songs64");
        final DatabaseReference eb = FirebaseDatabase.getInstance().getReference("/songs64");

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, int i1, int i2) {
                searchresults.clear();
                songurls.clear();
                albumcover.clear();
                albumname.clear();

                searchresultsyoutube.clear();
                youtubeArrayAdapter.notifyDataSetChanged();

                searchresultsalbum.clear();
                albumArrayAdapter.notifyDataSetChanged();

                arrayAdapter.notifyDataSetChanged();

                db.orderByKey().startAt(charSequence.toString()).endAt(charSequence.toString()+"\uf8ff").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        //if(searchresultsalbum.size() == 0)
                        //    albumtext.setVisibility(View.INVISIBLE);

                        songtext.setVisibility(View.VISIBLE);
                        surl = dataSnapshot.getValue(songurl.class);
                        searchresults.add(surl.name);
                        songurls.add(surl.url);
                        albumcover.add(surl.albumcover);
                        albumname.add(surl.album);



                        arrayAdapter.notifyDataSetChanged();



                        searchlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                addtoq.putExtra("name",String.valueOf(searchresults.get(i)));
                                addtoq.putExtra("url",String.valueOf(songurls.get(i)));
                                addtoq.putExtra("albumcover",albumcover.get(i));
                                addtoq.putExtra("album",albumname.get(i));
                                startActivity(addtoq);

                            }
                        });

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



                eb.orderByKey().addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        if(searchresults.size() == 0)
                            songtext.setVisibility(View.INVISIBLE);
                        //final String searchstring = charSequence.toString();

                        songfromdb = dataSnapshot.getKey();

                        final DatabaseReference foundsong = FirebaseDatabase.getInstance().getReference("/songs64/"+songfromdb);

                        foundsong.orderByChild("album").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                if(charSequence.toString().length() > 4)

                                    if(dataSnapshot.getValue().toString().toLowerCase().contains(charSequence.toString())) {


                                    foundsong.orderByKey().addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            songsinfs.add(dataSnapshot.getValue().toString());

                                            // if(songsinfs.size()>2)

                                            if(songsinfs.size() % 4 == 0) {

                                                albumtext.setVisibility(View.VISIBLE);

                                                if(searchresultsalbum.size() == 0) {
                                                    albumname.add(songsinfs.get(0 + (letsadd * 4)));
                                                    albumcover.add(songsinfs.get(1 + (letsadd * 4)));
                                                    // searchresultsalbum.add(songsinfs.get(2 + (letsadd * 4)));
                                                    searchresultsalbum.add(songsinfs.get(0 + (letsadd * 4)));
                                                    songurls.add(songsinfs.get(3 + (letsadd * 4)));

                                                }

                                                else if(!searchresultsalbum.get(searchresultsalbum.size()-1).equals(songsinfs.get(0 + (letsadd * 4)))) {

                                                    albumname.add(songsinfs.get(0 + (letsadd * 4)));
                                                    albumcover.add(songsinfs.get(1 + (letsadd * 4)));
                                                    // searchresultsalbum.add(songsinfs.get(2 + (letsadd * 4)));
                                                    searchresultsalbum.add(songsinfs.get(0 + (letsadd * 4)));
                                                    songurls.add(songsinfs.get(3 + (letsadd * 4)));
                                                }

                                                albumArrayAdapter.notifyDataSetChanged();



                                                      /*  if(!songsinfs.get(2 + (letsadd*4)).equals(info.getStringExtra("name"))) {

                                                            albumsongs.add(songsinfs.get(2 + (letsadd * 4)));
                                                            albumsongsurl.add(songsinfs.get(3 + (letsadd * 4)));
                                                            arrayAdapter.notifyDataSetChanged();
                                                        } */
                                                letsadd++;

                                            }


                                            searchlistalbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                                    addtoq.putExtra("name","albumnotsong");
                                                    addtoq.putExtra("url",String.valueOf(songurls.get(i)));
                                                    addtoq.putExtra("albumcover",albumcover.get(i));
                                                    addtoq.putExtra("album",albumname.get(i));
                                                    startActivity(addtoq);


                                                }
                                            });

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



                StringRequest post = new StringRequest(Request.Method.GET, "https://api.w3hills.com/youtube/search?api_key=64DDE546-C061-C65C-6DEE-B269C41AFB45&keyword=" + charSequence.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            searchresultsyoutube.clear();
                            ytalbumcover.clear();
                            ytalbumname.clear();
                            ytsongids.clear();
                            yttokens.clear();
                            youtubeArrayAdapter.notifyDataSetChanged();
                            JSONObject jsonResponse = new JSONObject(response);
                            // JSONArray jsonArray = jsonResponse.getJSONArray("videos");
                            // jsonArray = jsonResponse.getJSONArray(null);
                            // JSONArray arra = jsonArray.getJSONArray(3);
                            //JSONObject object = jsonResponse.getJSONObject("videos");
                            JSONArray jsonArray =jsonResponse.getJSONArray("videos");
                            for(int i=0;i<jsonArray.length();i++) {
                                if(i<6) {
                                    youTubeText.setVisibility(View.VISIBLE);
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    searchresultsyoutube.add(object.getString("title"));
                                    Log.i("volleyurl", object.getString("title"));
                                    ytalbumname.add(object.getString("title"));
                                    ytalbumcover.add(object.getString("thumbnail"));
                                    ytsongids.add(object.getString("id"));
                                    yttokens.add(object.getString("token"));

                                    youtubeArrayAdapter.notifyDataSetChanged();

                                    Log.i("objectid",object.getString("id"));


                                }
                            }





                        } catch (Exception e) {
                            Log.i("volleyurl",e.toString());
                        }

                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("volleyurl",error.toString());

                            }
                        });
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                post.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                requestQueue.add(post);




               /* searchlistyoutube.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view,final int i, long l) {

                       /* StringRequest post = new StringRequest(Request.Method.GET, "https://youtubetoany.com/@api/json/mp3/" + ytsongids.get(i), new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("ytsongid",ytsongids.get(i));
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    // JSONArray jsonArray = jsonResponse.getJSONArray("videos");
                                    // jsonArray = jsonResponse.getJSONArray(null);
                                    // JSONArray arra = jsonArray.getJSONArray(3);
                                    JSONObject object = jsonResponse.getJSONObject("vidInfo");
                                    object = object.getJSONObject("3");
                                    ytsongurl = "https:" + object.getString("dloadUrl");

                                    Log.i("songurl",ytsongurl);

                                    Intent youtubetoq = new Intent(getApplicationContext(),MainActivity.class);

                                    MainActivity.MusicPlayer temp = new MainActivity.MusicPlayer();
                                    temp.songname = searchresultsyoutube.get(i);
                                    temp.songurl = ytsongurl;
                                    temp.songid = ytsongids.get(i);
                                    temp.albumcover = ytalbumcover.get(i);
                                    temp.albumname = ytalbumname.get(i);

                                    MainActivity.musicPlayer.add(temp);
                                    savedata();


                                   /* youtubetoq.putExtra("name",String.valueOf(searchresultsyoutube.get(i)));
                                    youtubetoq.putExtra("url",String.valueOf(ytsongurl));
                                    youtubetoq.putExtra("albumcover",ytalbumcover.get(i));
                                    youtubetoq.putExtra("album",ytalbumname.get(i));
                                    youtubetoq.putExtra("flag",2);
                                    //startActivity(youtubetoq);

                                    //ytsongurls.add(url);
                                    //youtubeArrayAdapter.notifyDataSetChanged();


                                   // Log.i("volleyurl", url);


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
                       // RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                        post.setRetryPolicy(new DefaultRetryPolicy(
                                10000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        requestQueue.add(post); */


                   // }
                }






          //  }); */

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchlistyoutube.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent youtubetoq = new Intent(getApplicationContext(),MainActivity.class);

                MainActivity.MusicPlayer temp = new MainActivity.MusicPlayer();
                temp.songname = searchresultsyoutube.get(i);
                temp.songurl = null;
                temp.songid = ytsongids.get(i);
                temp.albumcover = ytalbumcover.get(i);
                temp.albumname = ytalbumname.get(i);
                temp.token = yttokens.get(i);

                MainActivity.musicPlayer.add(temp);
                savedata();
                startActivity(youtubetoq);

            }
        });

    }
}
