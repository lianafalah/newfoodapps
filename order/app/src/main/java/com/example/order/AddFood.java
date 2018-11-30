package com.example.order;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

public class AddFood extends AppCompatActivity {
        private ImageButton foodImage;
        private static final int GALLREQ =1;
        private static final int GET_FROM_GALLERY=3;
        private EditText name,desc,price;
        private Uri uri = null;
        private StorageReference storageReference= null;
        private DatabaseReference mRef;
        private FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        name = (EditText) findViewById(R.id.itemName);
        desc = (EditText) findViewById(R.id.itemDesc);
        price = (EditText) findViewById(R.id.itemPrice);

        storageReference = FirebaseStorage.getInstance().getReference("item");
        //mRef=FirebaseDatabase.getInstance().getReference("item");
    }
    public void imageButtonClicked(View view){
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        //DETECT

        if (requestCode == GET_FROM_GALLERY && resultCode== Activity.RESULT_OK){
            Uri selectedImage = data.getData();
            Bitmap bitmap=null;
            try {
                bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            foodImage =(ImageButton) findViewById(R.id.foodImageButton);
            foodImage.setImageURI(selectedImage);
        }
    }
    public void addItemButtonClicked(View view) {
        final String name_text = name.getText().toString().trim();
        final String desc_text = desc.getText().toString().trim();
        final String price_text = price.getText().toString().trim();
        if (!TextUtils.isEmpty(name_text) && !TextUtils.isEmpty(desc_text) && !TextUtils.isEmpty(price_text)) {
            StorageReference filepath = storageReference.child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final String downloadurl = taskSnapshot.getStorage().getDownloadUrl().toString();
                    Toast.makeText(AddFood.this, "Image uploaded", Toast.LENGTH_LONG).show();
                    final DatabaseReference newPost = mRef.push();
                    newPost.child("name").setValue(name_text);
                    newPost.child("desc").setValue(desc_text);
                    newPost.child("price").setValue(price_text);
                    newPost.child("image").setValue(downloadurl.toString());
                }
            });
        }
    }
}
