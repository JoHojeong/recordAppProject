package com.tvacstudio.audiorecorder;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class UploadActivity extends AppCompatActivity {

    Button selectFile, upload;
    TextView notification;
    Uri audioUri;

    FirebaseStorage storage;
    FirebaseDatabase database;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload1);

        storage=FirebaseStorage.getInstance(); //파이어베이스 스토리지로 리턴
        database=FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스로 리턴

        selectFile=findViewById(R.id.selectFile);
        upload=findViewById(R.id.upload);
        notification=findViewById(R.id.notification);

        selectFile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)==PERMISSION_GRANTED){
                    selectAudio();
                }
                else{
                    ActivityCompat.requestPermissions(UploadActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(audioUri!=null)  //유저가 파일을 선택했을 때
                uploadFile(audioUri);
                else
                    Toast.makeText(UploadActivity.this, "Select a File", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void uploadFile(Uri audioUri) {

        progressDialog=new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading file...");
        progressDialog.setProgress(0);
        progressDialog.show();

        final String fileName = System.currentTimeMillis()+"";
        StorageReference storageReference=storage.getReference();

        storageReference.child("Uploads").child(fileName).putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String url=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                DatabaseReference reference=database.getReference();

                reference.child(fileName).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                            Toast.makeText(UploadActivity.this,"File successfully uploaded", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(UploadActivity.this,"File not successfully uploaded", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadActivity.this,"File not successfully uploaded", Toast.LENGTH_SHORT).show();


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                double currentProgress=(int)100 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount();
                progressDialog.setProgress((int) currentProgress);

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==9 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            selectAudio();
        }
        else{
            Toast.makeText(UploadActivity.this,"please provide permission..",Toast.LENGTH_SHORT).show();

        }

    }

    private void selectAudio() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT); // to fetch files
        startActivityForResult(intent, 86);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            audioUri = data.getData();
            notification.setText("A file is selected : "+ data.getData().getLastPathSegment());
        } else {
            Toast.makeText(UploadActivity.this, "Please select a file", Toast.LENGTH_SHORT).show();
        }
    }
}
