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
import com.arif.gedor.DB.UserPbl;
import com.arif.gedor.Login.login_beliActivity;
import com.arif.gedor.Login.login_pdgActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

public class register_beliActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtFullName, txtEmail, txtPassword, txtConfirm, txtTelpon, txtLoginPbl;
    private Button btnRegisterPbl;

    FirebaseAuth auth;
    DatabaseReference userPedagang;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //firebase
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userPedagang = firebaseDatabase.getReference(Common.UserPbl_tbl);

        setContentView(R.layout.activity_register_beli);
        txtFullName = findViewById(R.id.edtNamaPbl);
        txtEmail = findViewById(R.id.edtEmailPbl);
        txtPassword = findViewById(R.id.edtPasswordPbl);
        txtConfirm = findViewById(R.id.edtComfrimPbl);
        txtTelpon = findViewById(R.id.edtTlpnPbl);
        btnRegisterPbl = findViewById(R.id.btnRegisterPbl);
        txtLoginPbl = findViewById(R.id.txtloginPbl);


        auth = FirebaseAuth.getInstance();

        findViewById(R.id.btnRegisterPbl).setOnClickListener(this);
        txtLoginPbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(register_beliActivity.this, login_beliActivity.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {

        }
    }

    private void RegisterUserPbl() {
        final String Fullname = txtFullName.getText().toString().trim();
        final String Email = txtEmail.getText().toString().trim();
        final String Password = txtPassword.getText().toString().trim();
        final String Confirm = txtConfirm.getText().toString().trim();
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

        if(Telepon.isEmpty()){
            txtTelpon.setError("Cek No. Telepon");
            txtTelpon.requestFocus();
            return;
        }
        
        final AlertDialog waitingDialog = new SpotsDialog(register_beliActivity.this);
        waitingDialog.show();
        auth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserPbl userPbl = new UserPbl(
                                    Fullname, Email, Password, Confirm, Telepon
                            );
                            firebaseDatabase.getReference(Common.UserPbl_tbl).child(FirebaseAuth.getInstance()
                                    .getCurrentUser().getUid()).child("Profil")
                                    .setValue(userPbl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        txtFullName.setText("");
                                        txtEmail.setText("");
                                        txtPassword.setText("");
                                        txtConfirm.setText("");
                                        txtTelpon.setText("");
                                        Toast.makeText(register_beliActivity.this, "Alhamdulillah Success", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(register_beliActivity.this,login_beliActivity.class));
                                        waitingDialog.dismiss();
                                    } else {
                                        waitingDialog.dismiss();
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                            Toast.makeText(getApplicationContext(), "Email Sudah Ada", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(register_beliActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(register_beliActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnRegisterPbl:
                RegisterUserPbl();
                break;
        }
    }


}
