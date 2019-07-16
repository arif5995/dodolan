package com.arif.gedor.Adapter;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arif.gedor.Common.Common;
import com.arif.gedor.DB.Upload;
import com.arif.gedor.MenuActivity;
import com.arif.gedor.R;
import com.arif.gedor.TambahFotoActivity;
import com.arif.gedor.UpdateFotoActivity;
import com.arif.gedor.menu_PedagangActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImagesActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener {
    private RecyclerView mRecyclerView;
    ImageAdapter imageAdapter;
    //database
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage mStorage;
    private ValueEventListener mDBListener;
    private FloatingActionButton tmbproduct;


    public TextView txtNamaProduk, txtHarga, txtRincian;
    private EditText edtSearch;
    public ImageView imageView, navBack,btnSearch;

    public Button btnTmb, btnKembali;

    public static final String KEY_SHARED = "FirstActivity.KEY";

    private ProgressBar mProgress_Circle;
    private ArrayList<Upload> mUploads;
    ArrayList<String> nama_Produk;
    ArrayList<String> harga_Produk;
    ArrayList<String> rincian_Produk;
    ArrayList<String> image_Produk;


    CardView cvListImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mUploads = new ArrayList<Upload>();
        imageAdapter = new ImageAdapter(ImagesActivity.this, mUploads);
        imageAdapter.setOnClickListener(ImagesActivity.this);

        txtNamaProduk = findViewById(R.id.nameProduk);
        txtHarga = findViewById(R.id.harga);
        txtRincian = findViewById(R.id.Rincian);
        imageView = findViewById(R.id.imageProduk);
        cvListImage = findViewById(R.id.cvListImage);
        tmbproduct = findViewById(R.id.tmbProduct);
        navBack = findViewById(R.id.navBack);
        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);

        mUploads = new ArrayList<Upload>();
        imageAdapter = new ImageAdapter(ImagesActivity.this, mUploads);
        mRecyclerView.setAdapter(imageAdapter);
        imageAdapter.setOnClickListener(ImagesActivity.this);

        nama_Produk = new ArrayList<>();
        harga_Produk = new ArrayList<>();
        rincian_Produk = new ArrayList<>();
        image_Produk = new ArrayList<>();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        tmbproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ImagesActivity.this, TambahFotoActivity.class);
                startActivity(intent);
            }
        });

        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ImagesActivity.this, menu_PedagangActivity.class);
                startActivity(intent);
            }
        });
        //database
        mStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(Common.UserPdg_tbl).child(auth.getCurrentUser().getUid())
                .child("Produk");

       mDBListener = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    mUploads.clear();
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()){
                    Upload upload = postsnapshot.getValue(Upload.class);
                    upload.setKey(postsnapshot.getKey());
                    mUploads.add(upload);
                }
                imageAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(imageAdapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ImagesActivity.this, menu_PedagangActivity.class));
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(int position) {
        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageREF = mStorage.getReferenceFromUrl(selectedItem.getUploadImage());
        imageREF.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
            DatabaseReference DTProduk = mDatabase.child(selectedKey).getRef();
            String KeyImg = DTProduk.getKey().toString();


            Intent intent = new Intent(ImagesActivity.this, UpdateFotoActivity.class);
                            intent.putExtra(KEY_SHARED, KeyImg);
                            startActivity(intent);


            }
        });

    }

    @Override
    public void onDeleteClick(int position) {
        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getUploadImage());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabase.child(selectedKey).removeValue();
                Toast.makeText(ImagesActivity.this, "Item Dihapus", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.removeEventListener(mDBListener);
    }


}
