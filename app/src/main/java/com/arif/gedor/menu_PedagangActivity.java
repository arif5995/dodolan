package com.arif.gedor;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.arif.gedor.Adapter.ImagesActivity;
import com.arif.gedor.Common.Common;
import com.arif.gedor.Login.login_beliActivity;
import com.arif.gedor.Login.login_pdgActivity;
import com.arif.gedor.Maps.MapsPedagangActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class menu_PedagangActivity extends AppCompatActivity {

    CardView CvMap, CvFoto;
    TextView Logout;

    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__pedagang);

        CvMap = (CardView)findViewById(R.id.CvMap);
        CvFoto = (CardView)findViewById(R.id.CvFoto);
        Logout = findViewById(R.id.tvLogOut);

        firebaseAuth = FirebaseAuth.getInstance();


        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(menu_PedagangActivity.this)
                        .setMessage("Apa Anda Ingin Keluar ?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(menu_PedagangActivity.this, MenuActivity.class));
                                firebaseAuth.signOut();
                                clearDBmarkerPdg();
                                finish();
                            }
                        })
                        .setNegativeButton("Tidak", null)
                        .show();
            }
        });

        CvFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(menu_PedagangActivity.this, ImagesActivity.class));
            }
        });

        CvMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(menu_PedagangActivity.this,MapsPedagangActivity.class));
            }
        });
    }

    private void clearDBmarkerPdg() {
        DatabaseReference dbLokasi = firebaseDatabase.getReference(Common.lokasiPdg_tbl).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dbLokasi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dbLokasiPdg: dataSnapshot.getChildren())
                {
                    dbLokasiPdg.getRef().removeValue();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Apa Anda Ingin Keluar ?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(menu_PedagangActivity.this, MenuActivity.class));
                        firebaseAuth.signOut();
                        finish();
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }
}
