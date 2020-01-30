package com.example.billify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AddExpenseActivity extends AppCompatActivity
{

    DatabaseReference databaseReference;
    EditText discription,title,participate,amount,f_name,f_email,f_contact;
    Spinner category,billed_by,split;
    Toolbar toolbar;
    Button add_new,add_exp;
    ImageView ln_image;
    ArrayList<Friend> par_friends = new ArrayList<Friend>();
    String image="";
    RecyclerView recyclerview;
    Uri imgUri;
    DatabaseHelper db;
    private static Calendar calender;
    FloatingActionButton bill_image;
    int uneqflag=0;
    HashMap<String,Integer> paid_hash;
    HashMap<String,Integer> boorow_hash;
    int sz;
    int[] split_arr;
    int[] paid_arr;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.keepSynced(true);


        discription = (EditText)findViewById(R.id.discription);
        title = (EditText)findViewById(R.id.title);
        participate=(EditText)findViewById(R.id.participate);
        amount = (EditText)findViewById(R.id.amount);

        category = (Spinner)findViewById(R.id.category);
        billed_by = (Spinner)findViewById(R.id.paid);
        split = (Spinner)findViewById(R.id.split);
        db = new DatabaseHelper(this);
        add_new=(Button)findViewById(R.id.add_new);
        add_exp=(Button)findViewById(R.id.add_exp);


        paid_hash = new HashMap<String, Integer>();
        boorow_hash = new HashMap<String, Integer>();
        sz= par_friends.size();
        participate.setText("");
        bill_image=(FloatingActionButton)findViewById(R.id.bill_image);
        ln_image=(ImageView)findViewById(R.id.ln_image);
        toolbar = findViewById(R.id.toolbar);
        final Billify bf=(Billify)getApplicationContext();
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        par_friends.add(bf.getYou());

        split.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 2)
                {

                    split_arr = popup();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        billed_by.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 2)
                {


                    paid_arr =  popup();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        participate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.recyclerview, null);
                recyclerview = (RecyclerView) alertLayout.findViewById(R.id.recycler_view);
                recyclerview.setHasFixedSize(true);
                recyclerview.setLayoutManager(new LinearLayoutManager(AddExpenseActivity.this));


                recyclerview.setAdapter(bf.getCadpt());

               // participate.setText(participate.getText() + bf.getSelected());
                AlertDialog dialog = new AlertDialog.Builder(AddExpenseActivity.this)

                        .setView(alertLayout)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener()
                                {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {


                                par_friends.addAll(bf.getSelected());
                                for(int i=0;i<bf.getSelected().size();i++)
                                {

                                    participate.setText(participate.getText() + ", " + bf.getSelected().get(i).getName());
                                }
                                bf.setSelected(null);
                            }
                        }).setNegativeButton("cancle",null).create();

                dialog.show();




            }
        });
        add_exp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String tt = String.valueOf(title.getText());
                String categ = category.getSelectedItem().toString();
                String dis = String.valueOf(discription.getText());

                String amo = String.valueOf(amount.getText());
                String by = billed_by.getSelectedItem().toString();
                int total = Integer.parseInt(amo);
                int devide=0;
                String uuid = UUID.randomUUID().toString();
                String uuid1;
                SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
                Date todayDate = new Date();
                String thisDate = currentDate.format(todayDate);

                int size_jk = par_friends.size();

                if(billed_by.getSelectedItemPosition() == 1)
                {
                    paid_arr = new int[size_jk];
                    paid_arr[0] = Integer.parseInt(amo);
                    for(int i=1;i<size_jk;i++)
                    {
                        paid_arr[i]=0;
                    }
                }


                if(split.getSelectedItemPosition() == 1)
                {
                   split_arr = new int[size_jk];
                    devide = Integer.parseInt(amo)/size_jk;
                    for(int i=0;i<size_jk;i++)
                    {
                        split_arr[i]=devide;
                    }
                }

                if(implementHashing(split_arr,paid_arr,size_jk))
                {
                    if(db.addExpense(uuid,dis,tt,categ,total,thisDate,image,0))
                    {
                        Toast.makeText(AddExpenseActivity.this,"Successfully inserted",Toast.LENGTH_LONG).show();
                       /* Iterator myVeryOwnIterator = meMap.keySet().iterator();
                        while(myVeryOwnIterator.hasNext()) {
                            String key=(String)myVeryOwnIterator.next();
                            String value=(String)meMap.get(key);
                            Toast.makeText(ctx, "Key: "+key+" Value: "+value, Toast.LENGTH_LONG).show();
                        }*/
                        Iterator myVeryOwnIterator = paid_hash.keySet().iterator();
                        Iterator borro_itr = boorow_hash.keySet().iterator();

                        String key=(String)myVeryOwnIterator.next();
                        int value=(int)paid_hash.get(key);

                       String key1 =(String)borro_itr.next();
                       int value1=(int)boorow_hash.get(key1);
                        int net=0;
                        while(true)
                        {



                            uuid1 = UUID.randomUUID().toString();
                            net = value - value1;

                            Log.d("hasing", "Key: "+value+" Value: "+value1);
                            if(net < 0)
                            {
                                db.addIndivisual(uuid1,uuid,key,key1,value,value1,0);
                            }
                            else
                            {
                                db.addIndivisual(uuid1,uuid,key,key1,value1,value1,0);
                            }


                            if(net < 0 )
                            {
                                value1 = value1 -value;
                                paid_hash.put(key1,value1);
                            }
                            else if(borro_itr.hasNext())
                            {
                               key1 =(String)borro_itr.next();
                               Log.d("dsd",key1);
                               value1=(int)boorow_hash.get(key1);
                            }
                            else
                            {
                                key1="";
                                value1=0;
                            }



                            if(net > 0 && value1 != 0)
                            {
                                value = net;
                                paid_hash.put(key,net);
                            }
                            else if(myVeryOwnIterator.hasNext())
                            {
                                key =(String)myVeryOwnIterator.next();
                                Log.d("dsd",key);
                                value=(int)paid_hash.get(key);

                            }
                            else
                                break;
                        //  ;

                            Log.d("hasing12", "Key: "+value+" Value: "+value1);
                           // Toast.makeText(this, "Key: "+key+" Value: "+value, Toast.LENGTH_LONG).show();
                        }

                    }
                }




            }
        });

        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.add_new, null);

                f_contact = (EditText)alertLayout.findViewById(R.id.f_contact);
                f_name = (EditText)alertLayout.findViewById(R.id.f_name);
                f_email = (EditText)alertLayout.findViewById(R.id.f_email);

                AlertDialog dialog = new AlertDialog.Builder(AddExpenseActivity.this)
                        .setTitle("New Connection")
                        .setView(alertLayout)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String name = String.valueOf(f_name.getText());
                                String email = String.valueOf(f_email.getText());
                                String contact = String.valueOf(f_contact.getText());

                                if(name.length() == 0)
                                {
                                    Toast.makeText(AddExpenseActivity.this,"Please enter name",Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    if(contact.length() == 0)
                                    {
                                        if(email.length() == 0)
                                        {

                                            Toast.makeText(AddExpenseActivity.this,"Please enter contact no or email",Toast.LENGTH_LONG).show();
                                        }
                                        else
                                        {
                                            Pattern p = Pattern.compile("[a-zA-Z0-9._-]+@[a" +
                                                    "-z]+\\.+[a-z]+");

                                            Matcher m = p.matcher(email);
                                            if(!m.find())
                                            {
                                                 Toast.makeText(AddExpenseActivity.this,"Please enter valid email",Toast.LENGTH_LONG).show();
                                            }
                                            else
                                            {
                                                final String nm=name;
                                               final String em=email;

                                               final String[][] uid = {new String[5]};

                                               if(db.findByEmail(email))
                                                   Toast.makeText(AddExpenseActivity.this,"All ready your friend",Toast.LENGTH_LONG).show();
                                               else
                                               {
                                                   databaseReference.orderByChild("Email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                                       @Override
                                                       public void onDataChange(DataSnapshot dataSnapshot)
                                                       {


                                                           if(dataSnapshot.getValue() == null)
                                                           {
                                                               //send request for add new friend on server
                                                               if(db.addNew(null,nm,em,"",0,""))
                                                               {
                                                                   Toast.makeText(AddExpenseActivity.this,"Success",Toast.LENGTH_LONG).show();
                                                               }


                                                           }
                                                           else
                                                           {
                                                               String key = dataSnapshot.getValue().toString();
                                                               uid[0] = key.split("\\{");
                                                               Toast.makeText(AddExpenseActivity.this,"get data + " + uid[0][1],Toast.LENGTH_LONG).show();


                                                           }


                                                       }
                                                       @Override
                                                       public void onCancelled(DatabaseError databaseError)
                                                       {


                                                       }
                                                   });

                                               }




                                            }
                                        }

                                    }
                                    else
                                    {
                                        if(db.findByContact(contact))
                                            Toast.makeText(AddExpenseActivity.this,"All ready your friend",Toast.LENGTH_LONG).show();
                                        else
                                        {
                                            final String nm=name;
                                            final String cn=contact;

                                            final String[][] uid = {new String[5]};
                                            databaseReference.orderByChild("Contact").equalTo(contact).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot)
                                                {

                                                    if(dataSnapshot.getValue() == null)
                                                    {
                                                        if(db.addNew(null,nm,"",cn,0,""))
                                                        {
                                                            Toast.makeText(AddExpenseActivity.this,"Success contact",Toast.LENGTH_LONG).show();
                                                        }

                                                    }
                                                    else
                                                    {
                                                        String key = dataSnapshot.getValue().toString();
                                                        uid[0] = key.split("\\{");
                                                        Toast.makeText(AddExpenseActivity.this,"get contact data + " + dataSnapshot.getValue(),Toast.LENGTH_LONG).show();


                                                    }



                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError)
                                                {

                                                }
                                            });

                                        }

                                    }
                                }

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        bill_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(ContextCompat.checkSelfPermission(AddExpenseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    SelectImage();
                }
                else
                {
                    ActivityCompat.requestPermissions(AddExpenseActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }
            }
        });






    }

    private boolean implementHashing(int[] split_arr, int[] paid_arr,int size)
    {
        for(int i=0;i<size;i++)
        {
            int net = paid_arr[i] - split_arr[i];

            if(net > 0)
            {
                paid_hash.put(par_friends.get(i).getId(),net);
                Log.d(net+" hj"+i,par_friends.get(i).getId()+"fgdg"+paid_hash.get(par_friends.get(i).getId())+"");
            }
            else {
                net=   split_arr[i] - paid_arr[i];
                boorow_hash.put(par_friends.get(i).getId(), net);
                Log.d(net+" hj12"+i,"hhjf "+par_friends.get(i).getId()+" jk" +boorow_hash.get(par_friends.get(i).getId())+"");
            }




        }


        return true;

        /**/
    }

    public int[] popup()
    {

        sz = par_friends.size();
        final int[] arr = new int[sz];
        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.unequall, null);

        final LinearLayout myRoot = (LinearLayout)alertLayout.findViewById(R.id.uneq);

        myRoot.removeAllViews();
        TextView[] tx = new TextView[sz];
        final EditText[] ex = new EditText[sz];
        final int count=0;
        for(int i=0;i<sz;i++)
        {
            LinearLayout ln = new LinearLayout(AddExpenseActivity.this);
            ln.setGravity(Gravity.CENTER);
            ln.setOrientation(LinearLayout.HORIZONTAL);
            ln.setId(i);

            tx[i] = new TextView(AddExpenseActivity.this);
            ex[i] = new EditText(AddExpenseActivity.this);



            ex[i].setId(i);
            tx[i].setId(i);

            tx[i].setText(par_friends.get(i).getName());

            ln.addView(tx[i]);
            ln.addView(ex[i]);
            myRoot.addView(ln);


        }

        AlertDialog dialog = new AlertDialog.Builder(AddExpenseActivity.this)

                .setView(alertLayout)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {


                        int sum=0;
                        for(int i=0;i<sz;i++)
                        {
                            int c = Integer.parseInt(String.valueOf(ex[i].getText()));
                            arr[i] =c;
                            sum =sum+c;
                            //Log.d("msg",arr[i]+"");
                        }
                        if(sum != Integer.parseInt(String.valueOf(amount.getText())))
                        {
                            uneqflag=1;
                            Toast.makeText(getApplicationContext(), "Provide valid value", Toast.LENGTH_LONG).show();
                        }





                    }
                }).setNegativeButton("cancle",null).create();

        dialog.show();




        return arr;

    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == 9) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                SelectImage();
            else
                Toast.makeText(this, "Please provide permission to choose image", Toast.LENGTH_LONG).show();

        } else if (requestCode == 70 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // SelectContact();

        } else
            Toast.makeText(this, "Please provide permission to access contacts", Toast.LENGTH_LONG).show();
    }

        public void SelectImage()
        {
            Intent intent = new Intent();

            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 86);
        }

    public void SelectContact()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent,70);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


            if(requestCode == 86 && data!= null )
            {
                imgUri = data.getData();

                try
                {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);


                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    // In case you want to compress your image, here it's at 40%
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();


                    long lengthbmp = byteArray.length;




                    if(lengthbmp > 200000)
                    {
                        AlertDialog dialog = new AlertDialog.Builder(AddExpenseActivity.this)

                                .setMessage("Image size should be less than 500KB")
                                .create();
                        dialog.show();
                        imgUri=null;


                    }
                    else
                    {
                        image = Base64.encodeToString(byteArray, Base64.DEFAULT);



                    String imageDataBytes = image.substring(image.indexOf(",")+1);

                    InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));

                    Bitmap bitmap1 = BitmapFactory.decodeStream(stream);




                        ln_image.setImageBitmap(bitmap1);
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                //name.setText(data.getData().getPath()+"");
            }

       /* else if(requestCode == 70 && data!= null )
        {
            Uri contactData = data.getData();

            Cursor c = managedQuery(contactData, null, null, null, null);
            if (c.moveToFirst()) {
                String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                try {
                    if (hasPhone.equalsIgnoreCase("1")) {
                        Cursor phones = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                null, null);
                        phones.moveToFirst();
                        String cNumber = phones.getString(phones.getColumnIndex("data1"));
                        //System.out.println("number is:" + cNumber);
                        contact.setText(cNumber);
                    }
                    String name1 = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    name.setText(name1);
                }
                catch (Exception ex)
                {
                    ex.getMessage();
                }
            }
        }*/
            else
            {

            }


    }



    public void UploadData() {

    }
}
