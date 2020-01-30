package com.example.billify;

import android.app.Activity;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

import static com.facebook.internal.FeatureManager.Feature.Places;

public class SignupFragment extends Fragment implements View.OnClickListener {

    private static final int RC_SIGN_IN = 1001;
    private int id=1;


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


                                    new MainActivity().fname=name;

                                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(id);

                                    ref.child("Email").setValue(email);

                                    ref.child("Name").setValue(name);

                                    Toast.makeText(new ChooseLoginSignupActivity(),name,Toast.LENGTH_LONG).show();

                                    new MainActivity().tx.setText(me.optString("name"));


                                    startActivity(new Intent(getActivity(),MainActivity.class));
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



        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent,RC_SIGN_IN);
            }
        });


        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());

        accessToken = AccessToken.getCurrentAccessToken();

        if(accessToken != null)
        {
            startActivity(new Intent(getActivity(),MainActivity.class));
        }
        else if(account!=null){
            startActivity(new Intent(getActivity(),MainActivity.class));
        }
        else
        {
            auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();

            if(currentUser !=null)
            {
                startActivity(new Intent(getActivity(),MainActivity.class));

            }
        }


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

                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                final String username = inputUserName.getText().toString().trim();
                final String phone = inputPhone.getText().toString().trim();


                if(TextUtils.isEmpty(email) && TextUtils.isEmpty(phone)){
                    Toast.makeText(getActivity(),"Enter email address!! or Phone is empty!!",Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getActivity(),"Password is empty!!",Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(username)){
                    Toast.makeText(getActivity(),"Username is empty!!",Toast.LENGTH_SHORT).show();
                }



                if(password.length()<6){
                    Toast.makeText(getActivity(),"Password is short",Toast.LENGTH_SHORT).show();
                }

                if(phone.length()<10){
                    Toast.makeText(getActivity(),"Phone number is short",Toast.LENGTH_SHORT).show();
                }


                if((phone.length()==10 && !TextUtils.isEmpty(email)) || !TextUtils.isEmpty(email)) {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(getActivity(), "createuseremailcomplete" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                            if (!task.isSuccessful()) {

                                Toast.makeText(getActivity(), "AuthFailed" + task.getException(), Toast.LENGTH_SHORT).show();
                            } else {
                                FirebaseUser currentperson = FirebaseAuth.getInstance().getCurrentUser();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentperson.getUid());

                                ref.child("Username").setValue(username);

                                ref.child("Phone").setValue(phone);

                                ref.child("Email").setValue(email);

                                ref.child("Password").setValue(password);


                                sendVerificationEmail();
                            }
                        }
                    });
                }

                if((phone.length()==10 && TextUtils.isEmpty(email)))
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("phone",phone);
                    bundle.putString("username",username);
                    bundle.putString("password",password);

                    VerifyPhoneFragment nextFragment = new VerifyPhoneFragment();
                    nextFragment.setArguments(bundle);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainer, nextFragment).commit();



                }

                break;

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (googleApiClient != null)
            googleApiClient.connect();
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
                            fragmentManager
                                    .beginTransaction()
                                    .replace(R.id.fragmentContainer,
                                            new LoginFragment(),
                                            "Login_Fragment").commit();
                        }
                        else
                        {
                        }
                    }
                });
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

            ref.child("Email").setValue(userEmail);

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



    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.stopAutoManage(getActivity());
            googleApiClient.disconnect();
        }
    }





}
