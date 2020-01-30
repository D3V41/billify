package com.example.billify;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity
{
    ImageView splash;
    Animation animation;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

       splash = findViewById(R.id.imageView);
        animation = AnimationUtils.loadAnimation(this,R.anim.splash_animation);

//       splash.setAnimation(animation);

        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {

                Intent intent  = new Intent(WelcomeActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);

        printHashKey();

    }

    private void printHashKey() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.billify",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}
