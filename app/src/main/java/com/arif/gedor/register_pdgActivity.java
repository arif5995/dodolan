package com.arif.gedor;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arif.gedor.Common.Common;
import com.arif.gedor.DB.UserPdg;
import com.arif.gedor.Login.login_pdgActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

public class register_pdgActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtFullName, txtEmail, txtPassword, txtConfirm, txtProduk
            ,txtTelpon ,txtLogin;
    private Button BtnRegister;

    FirebaseAuth auth;
    DatabaseReference userPedagang;
    FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //firebase
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userPedagang = firebaseDatabase.getReference(Common.UserPdg_tbl);

        setContentView(R.layout.activity_register_pdg);

        txtFullName = findViewById(R.id.edtNamaPdg);
        txtEmail = findViewById(R.id.edtEmailPdg);
        txtPassword = findViewById(R.id.edtPasswordPdg);
        txtConfirm = findViewById(R.id.edtComfrimPdg);
        txtProduk = findViewById(R.id.edtProdukPdg);
        txtTelpon = findViewById(R.id.edtTlpnPdg);
        BtnRegister = findViewById(R.id.btnRegisterPdg);
        txtLogin = findViewById(R.id.txtloginPdg);


        auth = FirebaseAuth.getInstance();

        findViewById(R.id.btnRegisterPdg).setOnClickListener(this);
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(register_pdgActivity.this,login_pdgActivity.class));
                finish();
            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
        if(auth.getCurrentUser() != null){

        }
    }

    private void registerUserPdg(){
        final String Fullname = txtFullName.getText().toString().trim();
        final String Email = txtEmail.getText().toString().trim();
        final String Password = txtPassword.getText().toString().trim();
        final String Confirm = txtConfirm.getText().toString().trim();
        final String Produk = txtProduk.getText().toString().trim();
        final String Telepon = txtTelpon.getText().toString().trim();

        if(Fullname.isEmpty()){
            txtFullName.setError("Cek Fullname");
            txtFullName.requestFocus();
            return;
        }
        if(Email.isEmpty()){
            txtEmail.setError("Cek Email");
            txtEmail.requestFocus();
            return;
        }
        if(Password.length()<6){
            txtPassword.setError("Cek Password");
            txtPassword.requestFocus();
            return;
        }
        if(Confirm.length()<6){
            txtConfirm.setError("Cek Confirm Password");
            txtConfirm.requestFocus();
            return;
        }
        if(Produk.isEmpty()){
            txtProduk.setError("Cek Produk");
            txtProduk.requestFocus();
            return;
        }
        if(Telepon.isEmpty()){
            txtTelpon.setError("Cek No. Telepon");
            txtTelpon.requestFocus();
            return;
        }
        final AlertDialog waitingDialog = new SpotsDialog(register_pdgActivity.this);
        waitingDialog.show();
        auth.createUserWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            UserPdg userPdg = new UserPdg(
                                    Fullname, Email, Password, Confirm, Produk, Telepon
                            );
                            FirebaseDatabase.getInstance().getReference(Common.UserPdg_tbl)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Profil")
                                    .setValue(userPdg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        txtFullName.setText("");
                                        txtEmail.setText("");
                                        txtPassword.setText("");
                                        txtConfirm.setText("");
                                        txtProduk.setText("");
                                        txtTelpon.setText("");
                                        Toast.makeText(register_pdgActivity.this,"Alhamdulillah Success",Toast.LENGTH_SHORT ).show();
                                        startActivity(new Intent(register_pdgActivity.this,login_pdgActivity.class));
                                        waitingDialog.dismiss();
                                    }else {
                                        waitingDialog.dismiss();
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                            Toast.makeText(getApplicationContext(), "Email Sudah Ada", Toast.LENGTH_LONG).show();
                                        }else {
                                            Toast.makeText(register_pdgActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(register_pdgActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnRegisterPdg:
                registerUserPdg();
                break;
        }

    }
}
