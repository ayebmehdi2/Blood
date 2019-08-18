package com.kareemAndMahmoud.bloodapp.PROFILE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kareemAndMahmoud.bloodapp.R;
import com.kareemAndMahmoud.bloodapp.databinding.SignupBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class EditProfile extends AppCompatActivity {

    StorageReference storageReference;
    SignupBinding binding;
    private Uri pathImage = null;

    FirebaseDatabase database;
    private ProgressDialog PD;
    DatabaseReference reference;
    DatabaseReference referenceP;

    private String email;
    private String info = "";
    private String passw;


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("PROFILES").child(uidIntent).child("status").setValue("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseDatabase.getInstance().getReference().child("PROFILES").child(uidIntent).child("status").setValue("offline");
    }

    private String uidIntent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.signup);

        binding.email.setVisibility(View.GONE);
        binding.emai.setVisibility(View.GONE);
        binding.bti.setVisibility(View.VISIBLE);
        binding.lastBlood.setVisibility(View.VISIBLE);

        binding.d.setText("Edit My profile");

        binding.imgSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        binding.signUp.setText("SAVE");

         SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        uidIntent = preference.getString("uid", null);


        if (uidIntent != null) {

            referenceP = reference.child("PROFILES/" + uidIntent);
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    profilData data = dataSnapshot.getValue(profilData.class);
                    if (data == null) return;
                    email = data.getEmail();
                    passw = data.getPassword();
                    updateUI(data);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            referenceP.addValueEventListener(valueEventListener);
        }

        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.sure.setVisibility(View.VISIBLE);



                binding.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.sure.setVisibility(View.GONE);
                    }
                });

                binding.canc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.sure.setVisibility(View.GONE);
                    }
                });

                binding.yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        PD = new ProgressDialog(EditProfile.this);
                        PD.setMessage("Loading...");
                        PD.setCancelable(true);
                        PD.setCanceledOnTouchOutside(false);
                        PD.show();

                        if (binding.name.getText().toString().length() > 0){
                            info += "\n Name : " + binding.name.getText().toString();
                            referenceP.child("name").setValue(binding.name.getText().toString());
                        }

                        /*
                        if (binding.email.getText().toString().length() > 0){
                            info += "\n Email : " + binding.email.getText().toString();

                            referenceP.child("email").setValue(binding.email.getText().toString());
                        }
                        */

                        String pas = binding.pass.getText().toString();
                        if (pas.length() > 0 && pas.equals(binding.pass2.getText().toString())){
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(email, passw);

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            assert user != null;
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                user.updatePassword(pas).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("EITEPROFILE", "Password updated");
                                                            info += "\n Password : " + pas;
                                                            referenceP.child("password").setValue(pas);
                                                        } else {
                                                            Log.d("EITEPROFILE", "Error password not updated");
                                                        }
                                                    }
                                                });
                                            } else {
                                                Log.d("EITEPROFILE", "Error auth failed");
                                            }
                                        }
                                    });

                        }

                        if (binding.phone.getText().toString().length() > 0){
                            info += "\n Phone Number : " + binding.phone.getText().toString();
                            referenceP.child("number").setValue(binding.phone.getText().toString());
                        }

                        if (binding.loca.getText().toString().length() > 0){
                            info += "\n Location : " + binding.loca.getText().toString();
                            referenceP.child("location").setValue(binding.loca.getText().toString());
                        }


                        if (binding.bloodType.getSelectedItem().toString().length() > 0){
                            info += "\n Blood Type : " + binding.bloodType.getSelectedItem().toString();
                            referenceP.child("bloodType").setValue(binding.bloodType.getSelectedItem().toString());
                        }

                        if (binding.lastBlood.getText().toString().length() > 0){
                            info += "\n Last blood donate : " + binding.lastBlood.getText().toString();
                            referenceP.child("bloodLastTime").setValue(binding.lastBlood.getText().toString());
                        }

                        if (binding.diseases.getText().toString().length() > 0){
                            info += "\n Diseases : " + binding.diseases.getText().toString();
                            referenceP.child("diseases").setValue(binding.diseases.getText().toString());
                        }

                        if (pathImage != null){
                            if (pathImage.toString().length() > 0) { uploadImage(pathImage); }
                        }else {
                            finich();
                        }





                    }
                });

            }
        });

    }


    public void finich(){
        PD.dismiss();
        if (info.length() > 0){
            binding.desc.setText(info);
            binding.cancel.setVisibility(View.GONE);
            binding.yes.setVisibility(View.GONE);
            binding.ok.setVisibility(View.VISIBLE);
            binding.ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(EditProfile.this, ProfileActivity.class);
                    i.putExtra("uid", "me");
                    startActivity(i);
                }
            });
        }
    }


    public void updateUI(profilData data) {
        if (data == null) return;
        binding.name.setText(data.getName());
        binding.phone.setText(data.getNumber());
        //binding.email.setText(data.getEmail());
        binding.loca.setText(data.getLocation());
        binding.diseases.setText(data.getDiseases());
        binding.lastBlood.setText(data.getBloodLastTime());
        binding.pass.setText(data.getPassword());
        binding.pass2.setText(data.getPassword());
        try {
            Glide.with(this).load(data.getPhoto()).into(binding.imgSignup);
        }catch (Exception e){

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            pathImage = data.getData();
            binding.imgSignup.setImageURI(pathImage);
        }
    }


    private void uploadImage(Uri filePath) {

        if (filePath == null){
            finich();
            return;
        }

        Bitmap bitmap = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(filePath);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap == null) {
            finich();
            Toast.makeText(EditProfile.this, "Failed Try again", Toast.LENGTH_SHORT).show();
            return;
        }
        ;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();

        final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                finich();
                Toast.makeText(EditProfile.this, "Failed update photo profile", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        referenceP.child("photo").setValue(url.toString());
                        info += "\n Photo : " + url.toString();
                        finich();
                    }
                });

            }
        });

    }






}
