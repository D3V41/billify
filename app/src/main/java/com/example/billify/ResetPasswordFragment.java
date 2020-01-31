package com.example.billify;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetPasswordFragment extends Fragment {
    private EditText inputEmail;
    private Button btnReset, btnBack;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_reset_password,container,false);
        inputEmail = (EditText) view.findViewById(R.id.email);
        btnReset = (Button) view.findViewById(R.id.btn_reset_password);
        btnBack = (Button) view.findViewById(R.id.btn_back);

        auth = FirebaseAuth.getInstance();



        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String getEmailId = inputEmail.getText().toString().trim();

                ConnectivityManager conMgr = (ConnectivityManager)getActivity().getSystemService (Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetworkInfo = conMgr.getActiveNetworkInfo();
                // Pattern for email id validation
                Pattern p = Pattern.compile("[a-zA-Z0-9._-]+@[a" +
                        "-z]+\\.+[a-z]+");
                // Match the pattern
                Matcher m = p.matcher(getEmailId);

                // First check if email id is not null else show error toast
                if (getEmailId.equals("") || getEmailId.length() == 0)


                    Toast.makeText(getActivity(), "Please enter your Email Id.",
                            Toast.LENGTH_SHORT).show();

                    // Check if email id is valid or not
                else if (!m.find())
                    Toast.makeText(getActivity(), "Your Email Id is invalid",
                            Toast.LENGTH_SHORT).show();

                else if (conMgr == null || activeNetworkInfo == null)
                {



                    AlertDialog dialog = new AlertDialog.Builder(getActivity())

                            .setMessage("Please check your connections")
                            .create();
                    dialog.show();

                }
                // Else submit email id and fetch passwod or do your stuff
                else
                {
                    progressDialog=new ProgressDialog(getActivity());
                    progressDialog.setMax(100);
                    progressDialog.setMessage("Its loading....");
                    progressDialog.setTitle("Please wait.");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();

                    auth.sendPasswordResetEmail(getEmailId).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                progressDialog.dismiss();

                                Toast.makeText(getActivity(),"Reset password link sent to your email..",Toast.LENGTH_LONG).show();


                                getActivity().finish();
                            }
                            else {
                                progressDialog.dismiss();

                                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                        .setTitle("Incorrect Password")
                                        .setMessage("Password, that you have entered is not registered.")
                                        .create();
                                dialog.show();

                            }
                        }
                    });
                }

            }
        });

        return view;
    }


}
