package com.example.sai.musicplayer;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LogIn extends AppCompatActivity implements View.OnClickListener{

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;



    public void login() {
        EditText email = (EditText) findViewById(R.id.email);
        EditText password = (EditText) findViewById(R.id.password);
        try {
            mAuth.signInWithEmailAndPassword(email.getEditableText().toString(), password.getEditableText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                        startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra("flag",121));
                    else
                        Snackbar.make(findViewById(R.id.email), "\nAuthentication failed", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e) {
            Snackbar.make(findViewById(R.id.email),"\nEnter valid credentials",Snackbar.LENGTH_LONG);
        }
    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 9001);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("d", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Log", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("flag",121));
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Warning", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.logingoogletext), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Intent gintent = getIntent();
        if(gintent.getIntExtra("flag",0) == 111)
            Snackbar.make(findViewById(R.id.password),"\nAccount created, please log in",Snackbar.LENGTH_LONG).show();

        else if(gintent.getIntExtra("flag",0) == 121)
            Snackbar.make(findViewById(R.id.password), "\nEmail sent",Snackbar.LENGTH_LONG).show();

        findViewById(R.id.email).setOnClickListener(this);
        findViewById(R.id.googleloginbutton).setOnClickListener(this);


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("7999973452-6199dccl1od8tkoab9np44eh6h27mptn.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        account = GoogleSignIn.getLastSignedInAccount(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email:
                EditText email = (EditText) findViewById(R.id.email);
                email.setCursorVisible(true);
                break;
            case R.id.googleloginbutton:
                signIn();
                break;
            case R.id.signuptext:
                startActivity(new Intent(this, SignUpPage.class));
                break;
            case R.id.loginbutton:
                login();
                break;
            case R.id.forgotpassword:
                startActivity(new Intent(this,ForgotPassword.class));
                break;


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 9001) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = null;
            try {
                account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
