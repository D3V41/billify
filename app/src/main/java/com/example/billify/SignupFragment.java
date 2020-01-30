package com.example.billify;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupFragment extends Fragment implements View.OnClickListener {

    private static final int RC_SIGN_IN = 1001;

    ProgressDialog progressDialog;
    private EditText inputUserName,inputPhone,inputEmail,inputPassword;
    private Button btnSignIn,btnSignUp,btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private CallbackManager callbackManager;
    private SignInButton btnGoogleSignIn;
    private LoginButton btnFacebook;
    private CallbackManager callbackManager1;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;
    private AccessToken accessToken;
    private GoogleApiClient googleApiClient;
    Billify bf;
    Friend f;
    private GoogleSignInOptions gso;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_signup,container,false);

        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) view.findViewById(R.id.sign_in_button);
        btnSignUp = (Button) view.findViewById(R.id.sign_up_button);
        inputEmail = (EditText) view.findViewById(R.id.email);
        inputPassword = (EditText) view.findViewById(R.id.password);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        btnResetPassword = (Button) view.findViewById(R.id.btn_reset_password);
        inputUserName = (EditText) view.findViewById(R.id.username);
        inputPhone = (EditText) view.findViewById(R.id.phone);

        fragmentManager =getActivity().getSupportFragmentManager();

        btnFacebook = (LoginButton) view.findViewById(R.id.login_button);
        btnGoogleSignIn = (SignInButton) view.findViewById(R.id.google_button);



        callbackManager = CallbackManager.Factory.create();
        btnFacebook.setPermissions(Arrays.asList("email", "public_profile"));


        btnSignUp.setOnClickListener(this);
        btnResetPassword.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);



        gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient=new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(),1, (GoogleApiClient.OnConnectionFailedListener) getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent,RC_SIGN_IN);
            }
        });




        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainer,
                                new LoginFragment(),
                                "Login_Fragment").commit();
                break;

            case R.id.btn_reset_password:

                fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainer,
                                new ResetPasswordFragment(),
                                "ForgotPassword_Fragment").commit();
                break;
            case R.id.sign_up_button:

                checkValidation();
                break;
        }
    }



    private void checkValidation()
    {
        ConnectivityManager conMgr = (ConnectivityManager)getActivity().getSystemService (Context.CONNECTIVITY_SERVICE);
        // Get all edittext texts
        final String getFullName = inputUserName.getText().toString();
        final String getEmailId = inputEmail.getText().toString();
        final String getMobileNumber = inputPhone.getText().toString();
        NetworkInfo activeNetworkInfo = conMgr.getActiveNetworkInfo();
        final String getPassword = inputPassword.getText().toString();
        //String getConfirmPassword = confirmPassword.getText().toString();

        // Pattern match for email id
        Pattern p = Pattern.compile("[a-zA-Z0-9._-]+@[a" +
                "-z]+\\.+[a-z]+");

        Pattern p1 = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$");


        Matcher m = p.matcher(getEmailId);
        Matcher m1 = p1.matcher(getPassword);

        // Check if all strings are null or not
        if (getFullName.equals("") || getFullName.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || getMobileNumber.equals("") || getMobileNumber.length() == 0

                || getPassword.equals("") || getPassword.length() == 0)
                //|| getConfirmPassword.equals("")
                //|| getConfirmPassword.length() == 0)

            Toast.makeText(getActivity(), "All fields are required.", Toast.LENGTH_SHORT)
                    .show();


            // Check if email id valid or not
        else if (!m.find())
            Toast.makeText(getActivity(), "Your Email Id is Invalid.", Toast.LENGTH_SHORT)
                    .show();
        else if (!m1.find())
        {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())

                    .setMessage("Your password should must contain atleast one digit from 0-9, atleast one lowercase characters, atleast one uppercase characters, atleast one special symbol" +
                            "and length must be between size of 8 to 20")
                    .create();
            dialog.show();
        }

        // Check if both password should be equal
       /* else if (!getConfirmPassword.equals(getPassword))
            Toast.makeText(getActivity(), "Both password doesn't match.", Toast.LENGTH_SHORT)
                    .show();*/
            // Else do signup or do your stuff
        else if (conMgr == null || activeNetworkInfo == null)
        {



            AlertDialog dialog = new AlertDialog.Builder(getActivity())

                    .setMessage("Please check your connections")
                    .create();
            dialog.show();

        }
        else
        {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMax(100);
            progressDialog.setMessage("Its loading....");
            progressDialog.setTitle("Please wait");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();


            auth.createUserWithEmailAndPassword(getEmailId,getPassword)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>()
                    {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {

                                auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            FirebaseUser currentperson = FirebaseAuth.getInstance().getCurrentUser();

                                            DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(currentperson.getUid());

                                            ref.child("Name").setValue(getFullName);

                                            ref.child("Contact").setValue(getMobileNumber);

                                            ref.child("Email").setValue(getEmailId);
                                            ref.child("Balance").setValue("0");
                                            ref.child("Profile").setValue("");

                                            //ref.child("Password").setValue(getPassword);


                                            FirebaseAuth.getInstance().signOut();
                                            Toast.makeText(getActivity(), "Registered Successfully.Please check your email for verification", Toast.LENGTH_SHORT)
                                                    .show();
                                            progressDialog.dismiss();

                                            getActivity().finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT)
                                                    .show();
                                        }


                                    }
                                });
//



                            }
                            else
                            {

                                Toast.makeText(getActivity(),"Error", Toast.LENGTH_SHORT)
                                        .show();
                                progressDialog.dismiss();
                            }





                        }



                    });
        }

    }






    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            String userName = account.getDisplayName().toString();
            String userEmail = account.getEmail().toString();
            String userId = account.getId().toString();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

            f = new Friend();

            f = new Friend();
            f.setName(userName);
            f.setEmail(userEmail);
            f.setContact("");
            f.setProfile("");
            f.setBalance(0);
            f.setId(userId);

            bf.setYou(f);

            ref.child("Email").setValue(userEmail);
            ref.child("Contact").setValue("");
            ref.child("Balance").setValue("");
            ref.child("Profile").setValue("");
            ref.child("Name").setValue(userName);
            Toast.makeText(getActivity(),"sign in successfull",Toast.LENGTH_SHORT).show();
        }

        else{
            Toast.makeText(getActivity(),"sign in cancle",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(),ChooseLoginSignupActivity.class));
        }

        gotoMainActivity();

    }

    private void gotoMainActivity(){
        Intent intent=new Intent(getActivity(),MainActivity.class);
        startActivity(intent);
    }



}
