package com.kareemAndMahmoud.bloodapp.MESSAGING.CHATROOM;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kareemAndMahmoud.bloodapp.HOME.HomeActivity;
import com.kareemAndMahmoud.bloodapp.MESSAGING.AdapMessage;
import com.kareemAndMahmoud.bloodapp.MESSAGING.GROUP_MESSAGING.GroupMessaging;
import com.kareemAndMahmoud.bloodapp.MESSAGING.Message;
import com.kareemAndMahmoud.bloodapp.Notifications;
import com.kareemAndMahmoud.bloodapp.PROFILE.ProfileActivity;
import com.kareemAndMahmoud.bloodapp.PROFILE.profilData;
import com.kareemAndMahmoud.bloodapp.R;
import com.kareemAndMahmoud.bloodapp.ShowImage;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class RoomChatActivity extends AppCompatActivity implements AdapMessage.ClickMsgs {

    private DatabaseReference referenceMsg;
    private ValueEventListener listenerForMesg;

    private String lastMesg = "Sey hello";

    private AdapMessage adapMessage;
    private ArrayList<Message> messages;

    private ImageView PhotoView;
    private TextView NameView;
    private EditText message;

    private String myName, YourName;
    private String myPhotoUri, YourPhoto;


    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference reference;

    private String MyUid = "me";
    private String YourId = "you";

    private LinearLayoutManager layoutManager;
    String nameChiled = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        findViewById(R.id.back_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        PhotoView = findViewById(R.id.ph);
        ImageView pickImage = findViewById(R.id.pick_img);
        ImageView sendMsg = findViewById(R.id.send);
        NameView = findViewById(R.id.namee);
        message = findViewById(R.id.edittext);
        RecyclerView rec = findViewById(R.id.rec_msg);


        adapMessage = new AdapMessage(this, this);
        rec.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rec.setLayoutManager(layoutManager);
        rec.setAdapter(adapMessage);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RoomChatActivity.this);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        YourId = getIntent().getStringExtra("uid");
        MyUid = preferences.getString("uid", "");

        if (YourId == null) return;

        PhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RoomChatActivity.this, ProfileActivity.class);
                i.putExtra("uid", YourId);
                startActivity(i);
            }
        });


        NameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RoomChatActivity.this, ProfileActivity.class);
                i.putExtra("uid", YourId);
                startActivity(i);
            }
        });

        ImageView on = findViewById(R.id.onnn);
        ImageView of = findViewById(R.id.offff);

        reference.child("PROFILES").child(MyUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profilData userInfo = dataSnapshot.getValue(profilData.class);
                if (userInfo == null) return;
                myName = userInfo.getName();
                myPhotoUri = userInfo.getPhoto();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference.child("PROFILES").child(YourId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profilData userInfo = dataSnapshot.getValue(profilData.class);
                if (userInfo == null) return;
                YourName = userInfo.getName();
                NameView.setText(YourName);
                YourPhoto = userInfo.getPhoto();
                try {
                    Glide.with(RoomChatActivity.this).load(userInfo.getPhoto()).into(PhotoView);
                }catch (Exception e){

                }
                if (userInfo.getStatus() != null){
                    if (userInfo.getStatus().length() > 0){
                        if (userInfo.getStatus().equals("online")){
                            on.setVisibility(View.VISIBLE);
                            of.setVisibility(View.GONE);
                        }else if (userInfo.getStatus().equals("offline")){
                            on.setVisibility(View.GONE);
                            of.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.e("RoomChat", "YouUid : " + YourId);

        int compar = MyUid.compareTo(YourId);
        if (compar > 0){
            nameChiled = MyUid + "---" + YourId;
        }else {
            nameChiled = YourId + "---" + MyUid;
        }

        Log.e("RoomChat", "nameChile : " + nameChiled);

        messages = new ArrayList<>();
        referenceMsg = reference.child("MESSAGS").child(nameChiled);
        listenerForMesg = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                messages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    if (message == null) return;
                    messages.add(message);
                    adapMessage.swapAdapter(messages);
                }
                layoutManager.scrollToPositionWithOffset(messages.size(),0);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        referenceMsg.addValueEventListener(listenerForMesg);

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = message.getText().toString();
                if (content.length() > 0) {
                    Message message = new Message(MyUid, YourId, myName, myPhotoUri, content,
                            null, (int) System.currentTimeMillis(), MyUid, null);
                    referenceMsg.push().setValue(message);
                    lastMesg = content;

                    notif(message);

                    reference.child("PROFILES").child(MyUid).child("PERSON_MESSAGE").child(nameChiled).setValue(new PersonMessage(YourId, YourName, YourPhoto, myName + " : " + lastMesg));
                    reference.child("PROFILES").child(YourId).child("PERSON_MESSAGE").child(nameChiled).setValue(new PersonMessage(MyUid, myName, myPhotoUri, myName + " : " + lastMesg));

                }
                message.setText("");
            }
        });

        findViewById(R.id.pick_location).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RoomChatActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                    return;
                }
                prog = new ProgressDialog(RoomChatActivity.this);
                prog.setTitle("Uploading...");
                prog.show();
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                        0, mLocationListener);
            }
        });

        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            uploadImage(data.getData());
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
            referenceMsg.removeEventListener(listenerForMesg);
            messages.clear();
    }


    public void notif(Message message){
        Notifications not = new Notifications();
        if (message.getConStr() != null && message.getConStr().length() > 0){
            not.sendNotificationToUser(message);
        }
    }



    ProgressDialog prog;

    private void uploadImage(Uri filePath) {

        if (filePath == null) return;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());

        Bitmap bitmap = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(filePath);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap == null){
            Toast.makeText(RoomChatActivity.this, "Failed Try again", Toast.LENGTH_SHORT).show();
            return;
        };
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressDialog.dismiss();
                Toast.makeText(RoomChatActivity.this, "Failed "+exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressDialog.dismiss();
                        Toast.makeText(RoomChatActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                        Message message = new Message(MyUid, YourId, myName, myPhotoUri, null,
                                uri.toString(), (int) System.currentTimeMillis(), MyUid, null);
                        referenceMsg.push().setValue(message);

                        notif(message);

                        reference.child("PROFILES").child(MyUid).child("PERSON_MESSAGE").child(nameChiled).setValue(new PersonMessage(YourId, YourName, YourPhoto, myName + " : Sent Photo"));
                        reference.child("PROFILES").child(YourId).child("PERSON_MESSAGE").child(nameChiled).setValue(new PersonMessage(MyUid, myName, myPhotoUri, myName + " : Sent Photo"));

                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                        .getTotalByteCount());
                progressDialog.setMessage("Uploaded "+(int)progress+"%");
            }
        });


    }

    public void menu(View view){
        findViewById(R.id.menu).setVisibility(View.VISIBLE);
        findViewById(R.id.addone).setVisibility(View.GONE);
        findViewById(R.id.del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("PROFILES").child(MyUid).child("PERSON_MESSAGE").child(YourId).removeValue();
                startActivity(new Intent(RoomChatActivity.this, HomeActivity.class));
            }
        });
        findViewById(R.id.grp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RoomChatActivity.this, GroupMessaging.class);
                i.putExtra("uid", YourId);
                i.putExtra("t", "create");
                startActivity(i);
            }
        });
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.menu).setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            prog.dismiss();
            if ( location == null ) return;
            String loca = "geo:" + String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());
            Message message = new Message(MyUid, YourId, myName, myPhotoUri, null,
                    null, (int) System.currentTimeMillis(), MyUid, loca);
            referenceMsg.push().setValue(message);
            lastMesg = loca;

            notif(message);

            reference.child("PROFILES").child(MyUid).child("PERSON_MESSAGE").child(nameChiled).setValue(new PersonMessage(YourId, YourName, YourPhoto, myName + " : " + lastMesg));
            reference.child("PROFILES").child(YourId).child("PERSON_MESSAGE").child(nameChiled).setValue(new PersonMessage(MyUid, myName, myPhotoUri, myName + " : " + lastMesg));

            mLocationManager.removeUpdates(mLocationListener);
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }
        @Override
        public void onProviderEnabled(String s) {

        }
        @Override
        public void onProviderDisabled(String s) {

        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                            0, mLocationListener);
                }
            }
        }


    }

    private LocationManager mLocationManager;

    public void showMap(Uri geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void openLocation(String uri) {
        showMap(Uri.parse(uri));
    }

    @Override
    public void clickImage(String source) {
        Intent intent = new Intent(this, ShowImage.class);
        intent.putExtra("url", source);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("PROFILES").child(MyUid).child("status").setValue("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseDatabase.getInstance().getReference().child("PROFILES").child(MyUid).child("status").setValue("offline");
    }


}