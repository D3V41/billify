package com.example.billify;



import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
{



    Toolbar toolbar;
    MenuItem mitem;
    private TabLayout tabLayout;
    private FirebaseAuth auth;
    AccessToken accessToken;
    DrawerLayout mDrawerLayout;
    GoogleSignInAccount account;
  NavigationView mNavigationView;

    private SimpleFragmentPageAdapter sadapter;
    FirebaseAuth firebaseAuth;
    String fname;
    static TextView tx;
    public final String[] firebaseusername = new String[1];


    View headerView;
    Billify billify;
    FragmentManager mFragmentManager;
    Menu mn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        billify=(Billify) getApplicationContext();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff) ;
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        mFragmentManager = getSupportFragmentManager();
        headerView = mNavigationView.getHeaderView(0);


        mn = mNavigationView.getMenu();
        mitem=mn.findItem(R.id.nav_item_login);
        tx = (TextView)headerView.findViewById(R.id.useremail);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {


            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)
            {
                mDrawerLayout.closeDrawers();

                if(menuItem.getItemId()==R.id.nav_item_home)
                {


                }



                if(menuItem.getItemId()==R.id.nav_item_restore)
                {

                }

                if(menuItem.getItemId()==R.id.nav_item_backup)
                {


                }

                if(menuItem.getItemId()==R.id.nav_item_help)
                {

                }
                if(menuItem.getItemId()==R.id.nav_item_feedback)
                {
                    Intent Email = new Intent(Intent.ACTION_SEND);
                    Email.setType("text/email");
                    Email.putExtra(Intent.EXTRA_EMAIL, new String[] { "17ceuog010@ddu.ac.in" });
                    Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                    Email.putExtra(Intent.EXTRA_TEXT, "Dear ...," + "");
                    startActivity(Intent.createChooser(Email, "Send Feedback:"));
                    return true;
                }

                if(menuItem.getItemId()==R.id.nav_item_share)
                {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    String shareBody ="https://play.google.com/store/apps/details?id="+getPackageName();
                    share.putExtra(android.content.Intent.EXTRA_SUBJECT, "Birthday Reminder");
                    share.putExtra(Intent.EXTRA_TITLE, "Birthday Reminder");
                    // share.setData();
                    share.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(share, "Share via"));
                    return true;
                }
                if(menuItem.getItemId()==R.id.nav_item_login)
                {

                    if(menuItem.getTitle().equals("Logout"))
                    {
                        if(AccessToken.getCurrentAccessToken() != null)
                        {
                            LoginManager.getInstance().logOut();
                            Intent marketIntent = new Intent(MainActivity.this, ChooseLoginSignupActivity.class);
                            startActivity(marketIntent);

                        }
                        else if(account!=null)
                        {
                            GoogleSignInOptions gso = new GoogleSignInOptions.
                                    Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                                    build();

                            GoogleSignInClient googleSignInClient=GoogleSignIn.getClient(MainActivity.this,gso);
                            googleSignInClient.signOut();
                          //  startActivity(new Intent(MainActivity.this,ChooseLoginSignupActivity.class));
                        }
                        else
                        {
                            FirebaseAuth.getInstance().signOut();
                            Intent marketIntent = new Intent(MainActivity.this, ChooseLoginSignupActivity.class);
                            startActivity(marketIntent);


                        }
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    }
                    else
                    {
                        Intent marketIntent = new Intent(MainActivity.this, ChooseLoginSignupActivity.class);
                        startActivity(marketIntent);

                    }

                }

                if(menuItem.getItemId()==R.id.nav_item_rate)
                {

                    Uri marketUri = Uri.parse("market://details?id=" + getPackageName());
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);

                }


                return false;
            }

        });


        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name,
                R.string.app_name);


        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();
    }



    @Override
    public void onStart()
    {
        super.onStart();


        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        sadapter = new SimpleFragmentPageAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(sadapter);
        viewPager.setCurrentItem(0);

        tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("All");
        tabLayout.getTabAt(1).setText("Group ");

         account = GoogleSignIn.getLastSignedInAccount(this);
        accessToken = AccessToken.getCurrentAccessToken();

        if(accessToken != null)
        {
           final String[] username = new String[1];

            tx.setText(billify.fb_flag);
            mitem.setTitle("Logout");
        }
        else if(account!=null)
        {
            tx.setText(account.getDisplayName());
            mitem.setTitle("Logout");
        }
        else
        {
            auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();


            if(currentUser !=null)
            {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tx.setText(dataSnapshot.child("Username").getValue().toString());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




                mitem.setTitle("Logout");


            }
        }

        }








    @Override
    protected void onStop() {
        super .onStop() ;
        //startNotification();
    }




    @Override
    protected void onPause ()
    {
        super .onPause();

        //startNotification();
    }






}
