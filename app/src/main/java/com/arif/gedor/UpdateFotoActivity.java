package com.arif.gedor;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UpdateFotoActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference FotouserPedagang;


    ImageView UImageUpd;
    EditText edtProdukUpd, edtHargaUpd, edtRincianUpd;
    Button btnSimapan, btnKembali;
    private Uri mImageUri;

    private StorageTask mUploadTask;
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_foto);


        edtProdukUpd = findViewById(R.id.edtNamaProdukUpd);
        edtHargaUpd = findViewById(R.id.edtHargaUpd);
        edtRincianUpd = findViewById(R.id.edtRincianUpd);
        UImageUpd = findViewById(R.id.UploadImageUpd);

        btnSimapan = findViewById(R.id.btnSimpan);
        btnKembali = findViewById(R.id.btnKembali);

        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateFotoActivity.this, ImagesActivity.class);
                startActivity(intent);

            }
        });

        btnSimapan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateImage();
            }
        });

        UImageUpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Openfile();
            }
        });

        String KeyImg = getIntent().getExtras().getString(ImagesActivity.KEY_SHARED);

        //database
        mStorage = FirebaseStorage.getInstance().getReference("Produk_Pedagang");
        FotouserPedagang = FirebaseDatabase.getInstance().getReference(Common.UserPdg_tbl).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Produk").child(KeyImg);

        FotouserPedagang.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    Upload uploadPdg = dataSnapshot.getValue(Upload.class);
                    edtProdukUpd.setText(uploadPdg.getNamaProduk());
                    edtHargaUpd.setText(uploadPdg.getHarga());
                    edtRincianUpd.setText(uploadPdg.getRincian());
                    UImageUpd.setImageURI(mImageUri);

                    Picasso.get().load(uploadPdg.getUploadImage()).fit().into(UImageUpd);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
            Picasso.get().load(mImageUri).fit().into(UImageUpd);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void UpdateImage() {
        final String KeyImg = getIntent().getExtras().getString(ImagesActivity.KEY_SHARED);
        if (mImageUri != null) {
            StorageReference fileReference = mStorage.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Upload uppload = new Upload();
                            uppload.setNamaProduk(edtProdukUpd.getText().toString());
                            uppload.setHarga(edtHargaUpd.getText().toString());
                            uppload.setRincian(edtRincianUpd.getText().toString());
                            uppload.setUploadImage(taskSnapshot.getDownloadUrl().toString());
                            Toast.makeText(UpdateFotoActivity.this, "success", Toast.LENGTH_SHORT).show();


                            FotouserPedagang.setValue(uppload)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            edtProdukUpd.setText("");
                                            edtHargaUpd.setText("");
                                            edtRincianUpd.setText("");
                                            UImageUpd.setImageDrawable(null);
                                        }
                                    });
                        }
                    });
        }
    }
}
