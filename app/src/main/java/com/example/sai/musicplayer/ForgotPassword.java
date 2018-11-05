package com.example.sai.musicplayer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.SnackbarContentLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        final FirebaseAuth auth = FirebaseAuth.getInstance();

        final EditText email = (EditText) findViewById(R.id.email);
        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!email.getEditableText().toString().equals("")) {
                    auth.sendPasswordResetEmail(email.getEditableText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                startActivity(new Intent(getApplicationContext(),LogIn.class).putExtra("flag",121));
                            else
                                Snackbar.make(findViewById(R.id.button), "\nAccount does not exist/Invalid Email",Snackbar.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });
    }
}
