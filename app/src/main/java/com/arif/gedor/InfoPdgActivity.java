package com.arif.gedor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.arif.gedor.Adapter.ImageAdapter;
import com.arif.gedor.Adapter.ImagesActivity;
import com.arif.gedor.Common.Common;
import com.arif.gedor.DB.Upload;
import com.arif.gedor.DB.UserPdg;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.zip.InflaterInputStream;

public class InfoPdgActivity extends AppCompatActivity {
    private RecyclerView mRvPdg;
    ImageAdapter imageAdapterPdg;

    private FirebaseAuth authPdg;
    private DatabaseReference mDatabaseRef, mDBpdg;
    private FirebaseDatabase firebaseDatabase;

    public TextView txtNama, txtProduk, txtTelepon;
    private ArrayList<Upload> mUploadsPdg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_pdg);

        mRvPdg = findViewById(R.id.recycler_viewPdg);
        mRvPdg.setHasFixedSize(true);
        mRvPdg.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        txtNama = findViewById(R.id.tvNamaPdg);
        txtProduk = findViewById(R.id.tvProdukPdg);
        txtTelepon = findViewById(R.id.tvTeleponPdg);

        authPdg = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDBpdg =  FirebaseDatabase.getInstance().getReference(Common.UserPdg_tbl).child(authPdg.getCurrentUser().getUid()).child("Profil");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(Common.UserPdg_tbl).child(authPdg.getCurrentUser().getUid())
                .child("Produk");

        mDBpdg.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserPdg userPdg = dataSnapshot.getValue(UserPdg.class);
                txtNama.setText(userPdg.getFullname());
                txtProduk.setText(userPdg.getProduk());
                txtTelepon.setText(userPdg.getTelepon());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUploadsPdg = new ArrayList<Upload>();
                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postsnapshot.getValue(Upload.class);
                    upload.setKey(postsnapshot.getKey());
                    mUploadsPdg.add(upload);
                }
                imageAdapterPdg = new ImageAdapter(InfoPdgActivity.this, mUploadsPdg);
                 mRvPdg.setAdapter(imageAdapterPdg);
                imageAdapterPdg.notifyDataSetChanged();
            }

                @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
