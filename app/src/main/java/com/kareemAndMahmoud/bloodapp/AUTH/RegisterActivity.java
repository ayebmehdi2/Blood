package com.kareemAndMahmoud.bloodapp.AUTH;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kareemAndMahmoud.bloodapp.HOME.HomeActivity;
import com.kareemAndMahmoud.bloodapp.PROFILE.profilData;
import com.kareemAndMahmoud.bloodapp.R;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {


    private EditText inName, inEmail, inPass, inPass2, inNumber, inLocation /* inLastBlood*/, inDiseases;
    private String name, email, password, password2, number, loca , bloodType, diseases ;
    private FirebaseAuth auth;
    private Button btnSignUp;
    private ImageView image;
    private ProgressDialog PD;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private Spinner inBloodType;


    private String pathImage = "";


    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        image = findViewById(R.id.img_signup);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

        PD = new ProgressDialog(this);
        PD.setMessage("Loading...");
        PD.setCancelable(true);
        PD.setCanceledOnTouchOutside(false);

        auth = FirebaseAuth.getInstance();

        inName = findViewById(R.id.name);
        inEmail =  findViewById(R.id.email);
        inPass =  findViewById(R.id.pass);
        inPass2 =  findViewById(R.id.pass2);
        inNumber =  findViewById(R.id.phone);
        btnSignUp =  findViewById(R.id.sign_up);
        inLocation = findViewById(R.id.loca);

        inBloodType =  findViewById(R.id.blood_type);
        //inLastBlood =  findViewById(R.id.last_blood);

        inDiseases = findViewById(R.id.diseases);



        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = inEmail.getText().toString();
                  password = inPass.getText().toString();
                 password2 = inPass2.getText().toString();
                name = inName.getText().toString();
                 number = inNumber.getText().toString();
                 loca = inLocation.getText().toString();

                 bloodType = inBloodType.getSelectedItem().toString();
                //lastBlood = inLastBlood.getText().toString();
                diseases = inDiseases.getText().toString();




                try {

                        if (!(password.length() > 0 )){
                            Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_LONG).show();
                            return;
                        }



                    if (!(bloodType.length() > 0 )){
                        Toast.makeText(RegisterActivity.this, "Enter Blood Type", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (!(diseases.length() > 0 )){
                        Toast.makeText(RegisterActivity.this, "Enter your diseases", Toast.LENGTH_LONG).show();
                        return;
                    }


                    if (!(loca.length() > 0 )){
                        Toast.makeText(RegisterActivity.this, "Enter Location", Toast.LENGTH_LONG).show();
                        return;
                    }

                        if (!(password2.length() > 0 )){
                            Toast.makeText(RegisterActivity.this, "Enter Password Num 2", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (!(email.length() > 0 )){
                            Toast.makeText(RegisterActivity.this, "Enter Email", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (!(number.length() > 0 )){
                            Toast.makeText(RegisterActivity.this, "Enter Number", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (!(password.equals(password2))){
                            Toast.makeText(RegisterActivity.this, "Pass 1 and pass 2 not the same", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (!(pathImage.toString().length() > 0 )){
                        Toast.makeText(RegisterActivity.this, "Image not found", Toast.LENGTH_LONG).show();

                    }


                        PD.show();
                        auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(
                                        RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(
                                                    RegisterActivity.this,
                                                    "Authentication Failed Check the email or password",
                                                    Toast.LENGTH_LONG).show();

                                        } else {


                                            if (pathImage.length() > 0 ){
                                                uploadImage(pathImage);
                                            }else {
                                                SharedPreferences.Editor preference = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this).edit();
                                                preference.putString("uid", auth.getUid());
                                                preference.putString("name", name);
                                                preference.apply();
                                                reference.child("PROFILES/"+auth.getUid()).setValue(new profilData(name, email, password, number
                                                        , loca, null, auth.getUid(), bloodType, null, diseases, "online"));

                                                FirebaseMessaging.getInstance().subscribeToTopic("user_"+auth.getUid());

                                                PD.dismiss();

                                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                            }

                                            }

                                        }

                                });

                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            if (data.getData() == null) return;
            pathImage = data.getData().toString();
            image.setImageURI(data.getData());
        }
    }


    private void uploadImage(String filePath) {

        if (filePath == null) return;
        Bitmap bitmap = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(Uri.parse(filePath));
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap == null){
            Toast.makeText(RegisterActivity.this, "Failed Try again", Toast.LENGTH_SHORT).show();
            return;
        };
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();

        final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                Toast.makeText(RegisterActivity.this, "Failed "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor preference = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this).edit();
                preference.putString("uid", auth.getUid());
                preference.putString("name", name);
                preference.apply();
                reference.child("PROFILES/"+auth.getUid()).setValue(new profilData(name, email, password, number
                        , loca, null, auth.getUid(), bloodType, null, diseases, "online"));
                FirebaseMessaging.getInstance().subscribeToTopic("user_"+auth.getUid());

                PD.dismiss();

                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(intent);

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        SharedPreferences.Editor preference = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this).edit();
                        preference.putString("uid", auth.getUid());
                        preference.putString("name", name);
                        preference.putString("img", pathImage.toString());
                        preference.apply();
                        reference.child("PROFILES/"+auth.getUid()).setValue(new profilData(name, email, password, number
                                , loca, url, auth.getUid(), bloodType, null, diseases, "online"));
                        FirebaseMessaging.getInstance().subscribeToTopic("user_"+auth.getUid());

                        PD.dismiss();

                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });

            }
        });


        }


    }




