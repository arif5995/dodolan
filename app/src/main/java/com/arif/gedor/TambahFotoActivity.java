package com.arif.gedor;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.arif.gedor.Adapter.ImagesActivity;
import com.arif.gedor.Common.Common;
import com.arif.gedor.DB.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import dmax.dialog.SpotsDialog;

public class TambahFotoActivity extends AppCompatActivity {

    DatabaseReference FotouserPedagang;
    ImageView UImage;
    EditText edtProduk, edtHarga, edtRincian;
    Button btnUpload, btnShow;
    private Uri mImageUri;

    private StorageTask mUploadTask;
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_foto);

        //database
        mStorage = FirebaseStorage.getInstance().getReference("Produk_Pedagang");
        FotouserPedagang = FirebaseDatabase.getInstance().getReference(Common.UserPdg_tbl);

        edtProduk= findViewById(R.id.edtNamaProduk);
        edtHarga = findViewById(R.id.edtHarga);
        edtRincian = findViewById(R.id.edtRincian);
        UImage = findViewById(R.id.UploadImage);
        btnShow = findViewById(R.id.btnShow);
        btnUpload = findViewById(R.id.btnUpload);
        
        
        UImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Openfile();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUploadTask != null && mUploadTask.isInProgress()){
                    Toast.makeText(TambahFotoActivity.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
                    btnUpload.setEnabled(false);

                }else {
                    uploadfile();
                }
            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TambahFotoActivity.this, ImagesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void uploadfile() {

        final AlertDialog waitingDialog = new SpotsDialog(TambahFotoActivity.this,"Harap Sabar");
        waitingDialog.show();
        if (mImageUri != null){
            StorageReference fileReference = mStorage.child(System.currentTimeMillis()+"."+ getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(TambahFotoActivity.this, "success", Toast.LENGTH_SHORT).show();
                            waitingDialog.dismiss();
                            Upload uppload = new Upload();
                            uppload.setNamaProduk(edtProduk.getText().toString());
                            uppload.setHarga(edtHarga.getText().toString());
                            uppload.setRincian(edtRincian.getText().toString());
                            uppload.setUploadImage(taskSnapshot.getDownloadUrl().toString());

                            FotouserPedagang.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Produk")
                                    .child(FotouserPedagang.push().getKey()).setValue(uppload)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                          edtProduk.setText("");
                                          edtHarga.setText("");
                                          edtRincian.setText("");
                                          UImage.setImageDrawable(null);
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TambahFotoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            waitingDialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        }
                    });
        }else {
            Toast.makeText(this,"no file selected",Toast.LENGTH_SHORT).show();
        }
    }

    private void Openfile() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).fit().into(UImage);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
