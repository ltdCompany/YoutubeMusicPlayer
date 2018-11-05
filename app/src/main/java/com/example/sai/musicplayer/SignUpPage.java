package com.example.sai.musicplayer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.time.Duration;

public class SignUpPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);



        final EditText password = (EditText) findViewById(R.id.password);
        final EditText confirmpassword = (EditText) findViewById(R.id.confirmpassword);
        final EditText email = (EditText) findViewById(R.id.email);
        final EditText username = (EditText) findViewById(R.id.username);

        final ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl("musicplayer-3d570.firebaseapp.com")
                .setHandleCodeInApp(true)
                .setAndroidPackageName("com.example.sai.musicplayer",true, "12")
                .build();


        Button signupbutton = (Button) findViewById(R.id.signupbutton);
        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(email.getEditableText().toString().equals("") || username.getEditableText().toString().equals("") || password.getEditableText().toString().equals("") || confirmpassword.getEditableText().toString().equals(""))
                    Snackbar.make(findViewById(R.id.username), "\nFill in all details",Snackbar.LENGTH_LONG).show();

                else if(password.getEditableText().toString().contains(" "))
                    Snackbar.make(findViewById(R.id.username),"\nSpaces are not allowed for passwords",Snackbar.LENGTH_LONG).show();

                else if(!password.getEditableText().toString().equals(confirmpassword.getEditableText().toString()))
                    Snackbar.make(findViewById(R.id.password),"\nPasswords do not match",Snackbar.LENGTH_LONG).show();

                else if(password.getEditableText().toString().length() < 8)
                    Snackbar.make(findViewById(R.id.password), "\nMinimum size of password must be 8 characters", Snackbar.LENGTH_LONG).show();

                else if(password.getEditableText().toString().equals(confirmpassword.getEditableText().toString()) && !email.equals(null)) {
                    final FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.createUserWithEmailAndPassword(email.getEditableText().toString(),password.getEditableText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username.getEditableText().toString())
                                        .build();
                                user.updateProfile(userProfileChangeRequest);
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getApplicationContext(), LogIn.class).putExtra("flag", 111));
                            }
                            else
                                Snackbar.make(findViewById(R.id.password),"\nAccount already exists/Invalid email",Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}
