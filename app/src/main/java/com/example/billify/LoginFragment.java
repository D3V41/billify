package com.example.billify;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.billify.R;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private static final int RC_SIGN_IN = 1001;


    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset,btnGoogle;
    private SignInButton btnGoogleSignIn;
    private LoginButton btnFacebook;
    private CallbackManager callbackManager;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;
    private AccessToken accessToken;
    private GoogleApiClient googleApiClient;
    private DatabaseReference databaseReference;
    private GoogleSignInOptions gso;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Billify bf;
    ProgressDialog progressDialog;
    Friend f;
    public LoginFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_login,container,false);


        auth = FirebaseAuth.getInstance();

        bf = (Billify) getApplicationContext();

        inputEmail = (EditText) view.findViewById(R.id.email);
        inputPassword = (EditText) view.findViewById(R.id.password);

        btnLogin = (Button) view.findViewById(R.id.sign_in_button);
        btnSignup = (Button) view.findViewById(R.id.sign_up_button);
        btnReset = (Button) view.findViewById(R.id.btn_reset_password);
        btnGoogle = (Button) view.findViewById(R.id.google);
        databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        fragmentManager =getActivity().getSupportFragmentManager();

        progressDialog = new ProgressDialog(getActivity());

        btnFacebook = (LoginButton) view.findViewById(R.id.login_button);
        btnGoogleSignIn = (SignInButton) view.findViewById(R.id.google_button);




        callbackManager = CallbackManager.Factory.create();
        btnFacebook.setPermissions(Arrays.asList("email", "public_profile"));

        setListeners();

        gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient=new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(),1, (GoogleApiClient.OnConnectionFailedListener) getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();


        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent,RC_SIGN_IN);      }
        });





        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());

        accessToken = AccessToken.getCurrentAccessToken();

             return view;
    }


    private void setListeners()
    {
        btnLogin.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnSignup.setOnClickListener(this);



        // Set check listener over checkbox for showing and hiding password
        /*show_hide_password
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {

                        // If it is checkec then show password else hide
                        // password
                        if (isChecked) {

                            show_hide_password.setText("Hide password");// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT);
                            password.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            show_hide_password.setText("Show Password");// change
                            // checkbox
                            // text

                            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            inputPassword.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password

                        }

                    }
                });*/
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                checkValidation();
                break;

            case R.id.btn_reset_password:

                fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainer,
                                new ResetPasswordFragment(),
                                "ForgotPassword_Fragment").commit();
                break;
            case R.id.sign_up_button:

                // Replace signup frgament with animation

                fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new SignupFragment(),
                                "SignUp_Fragment").commit();
                break;

        }


    }

    private void checkValidation() {
        // Get email id and password
        ConnectivityManager conMgr = (ConnectivityManager)getActivity().getSystemService (Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = conMgr.getActiveNetworkInfo();

        String getEmailId = inputEmail.getText().toString();
        String getPassword = inputPassword.getText().toString();

        // Check patter for email id
        Pattern p = Pattern.compile("[a-zA-Z0-9._-]+@[a" +
                "-z]+\\.+[a-z]+");

        Matcher m = p.matcher(getEmailId);

        // Check for both field is empty or not
        if (getEmailId.equals("") || getEmailId.length() == 0
                || getPassword.equals("") || getPassword.length() == 0) {
//            loginLayout.startAnimation(shakeAnimation);
            Toast.makeText(getActivity(), "Enter both credentials.", Toast.LENGTH_SHORT)
                    .show();

        }
        // Check if email id is valid or not
        else if (!m.find())
            Toast.makeText(getActivity(), "Email id is invalid", Toast.LENGTH_SHORT)
                    .show();
            // Else do login and do your stuff
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


            auth.signInWithEmailAndPassword(getEmailId,getPassword)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>()
                    {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {

                                final FirebaseUser user = auth.getCurrentUser();
                                //Toast.makeText(getApplicationContext(), "Provide +"+user.getUid() , Toast.LENGTH_LONG).show();
                                if(user.isEmailVerified())
                                {
                                    databaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {

                                            String nm = (String) dataSnapshot.child("Name").getValue();
                                            String pr = (String) dataSnapshot.child("Profile").getValue();
                                            String em = (String) dataSnapshot.child("Email").getValue();
                                            String cn = (String) dataSnapshot.child("Contact").getValue();
                                            String bl =  (String) dataSnapshot.child("Balance").getValue();



                                            DatabaseHelper hp =new DatabaseHelper(getActivity());
                                            if(hp.findByEmail(em))
                                            {
                                                Toast.makeText(getApplicationContext(), "email exist" , Toast.LENGTH_LONG).show();
                                            }
                                            else if(hp.findByContact(cn))
                                            {
                                                Toast.makeText(getApplicationContext(), "contact exist" , Toast.LENGTH_LONG).show();
                                            }
                                            else
                                                {
                                               // if(hp.addNew(user.getUid(), nm, em, cn, Integer.parseInt(bl), pr))
                                                Toast.makeText(getApplicationContext(), "added profile" , Toast.LENGTH_LONG).show();
                                            }

                                            getActivity().finish();


                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError)
                                        {


                                        }
                                    });


                                    //getActivity().finish();
                                }
                                else
                                {
                                    AlertDialog dialog = new AlertDialog.Builder(getActivity())

                                            .setMessage("Please verify your email")
                                            .create();
                                    dialog.show();
                                }
                                progressDialog.dismiss();



                            }
                            else
                            {
                                progressDialog.dismiss();

                                AlertDialog dialog = new AlertDialog.Builder(getActivity())

                                        .setMessage("You have been not registered yet")
                                        .create();
                                dialog.show();
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
       getActivity().finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.stopAutoManage(getActivity());
            googleApiClient.disconnect();
        }
    }

}




