package com.example.sai.musicplayer;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AboutPage extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public void sendemail(View view) {

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        String to = new String();
        to = "sainath17136@cse.ssn.edu.in";

        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"sainath17136@cse.ssn.edu.in"});

        emailIntent.setType("text/plain");

        startActivity(emailIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);

        FirebaseUser user = auth.getCurrentUser();
        TextView username = (TextView) findViewById(R.id.username);
        username.setText(user.getDisplayName() + "?");

        TextView version = (TextView) findViewById(R.id.version);
        version.setText("Version = " + BuildConfig.VERSION_NAME +"(Beta)");


    }
}
