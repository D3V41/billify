package com.example.loginapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenManager;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class SignupActivity extends AppCompatActivity {

    private EditText inputUserName,inputPhone,inputEmail,inputPassword;
    private Button btnSignIn,btnSignUp,btnResetPassword;
    private LoginButton btnFacebook;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        btnFacebook = (LoginButton) findViewById(R.id.login_button);
        inputUserName = (EditText) findViewById(R.id.username);
        inputPhone = (EditText) findViewById(R.id.phone);



        callbackManager = CallbackManager.Factory.create();
        btnFacebook.setPermissions(Arrays.asList("email", "public_profile"));




        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this,ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                final String username = inputUserName.getText().toString().trim();
                final String phone = inputPhone.getText().toString().trim();


                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Enter email address!!",Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"Password is empty!!",Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(username)){
                    Toast.makeText(getApplicationContext(),"Username is empty!!",Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(getApplicationContext(),"Phone is empty!!",Toast.LENGTH_SHORT).show();
                }

                if(password.length()<6){
                    Toast.makeText(getApplicationContext(),"Password is short",Toast.LENGTH_SHORT).show();
                }

                if(phone.length()<10){
                    Toast.makeText(getApplicationContext(),"Phone number is short",Toast.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignupActivity.this,"createuseremailcomplete"+task.isSuccessful(),Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);

                        if(!task.isSuccessful()){

                            Toast.makeText(SignupActivity.this,"AuthFailed"+task.getException(),Toast.LENGTH_SHORT).show();
                        }

                        else{
                            FirebaseUser currentperson = FirebaseAuth.getInstance().getCurrentUser();

                            DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(currentperson.getUid());

                            ref.child("Username").setValue(username);

                            ref.child("Phone").setValue(phone);

                            ref.child("Email").setValue(email);

                            ref.child("Password").setValue(password);


                            sendVerificationEmail();
                        }
                    }
                });

            }

        });

        btnFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                if (response.getError() != null) {

                                } else {
                                    String email = response.getJSONObject().optString("email");
                                    String id = me.optString("id");
                                    String name = me.optString("name");



                                    FirebaseUser currentperson = FirebaseAuth.getInstance().getCurrentUser();

                                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(id);

                                    ref.child("Email").setValue(email);

                                    ref.child("Name").setValue(name);


                                    startActivity(new Intent(SignupActivity.this,MainActivity.class));
                                }
                            }
                        }).executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker tokenManager = new AccessTokenTracker(){
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,AccessToken currentAccessToken){

            if(currentAccessToken==null){
                inputEmail.setText("");
                inputPhone.setText("");
                inputUserName.setText("");
                Toast.makeText(SignupActivity.this,"User logged out",Toast.LENGTH_SHORT).show();
            }
        }

    };




    public void OnResume(){
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            finish();
                        }
                        else
                        {
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }
}
