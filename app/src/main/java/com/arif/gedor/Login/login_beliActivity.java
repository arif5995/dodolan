package com.arif.gedor.Login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arif.gedor.Maps.MapPembeliActivity;
import com.arif.gedor.MenuActivity;
import com.arif.gedor.R;
import com.arif.gedor.register_beliActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

public class login_beliActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText txtEmail, txtPassword;
    private Button BtnLoginPbl;
    private TextView txtRegisterPbl;


    FirebaseAuth auth;
    DatabaseReference userPembeli;
    FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_beli);

        txtEmail = findViewById(R.id.edtEmailPbl);
        txtPassword = findViewById(R.id.edtPasswordPbl);
        txtRegisterPbl = findViewById(R.id.txtregisterPbl);

        findViewById(R.id.btnLoginPbl).setOnClickListener(this);

        //firebase
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser User = auth.getCurrentUser();

        txtRegisterPbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login_beliActivity.this, register_beliActivity.class));
                finish();
            }

        });

    }

    private void LoginUserPbl(){
        final String Email = txtEmail.getText().toString().trim();
        final String Password = txtPassword.getText().toString().trim();

        if(Email.isEmpty()){
            txtEmail.setError("Cek Email");
            txtEmail.requestFocus();
            return;
        }
        if(Password.isEmpty()){
            txtPassword.setError("Cek Password");
            txtPassword.requestFocus();
            return;
        }
        if(Password.length()<6){
            txtPassword.setError("Cek Password");
            txtPassword.requestFocus();
            return;
        }
        final AlertDialog waitingDialog = new SpotsDialog(login_beliActivity.this,"Harap Sabar");
        waitingDialog.show();
        auth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    checkEmailVerification();
                    waitingDialog.dismiss();
                }else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(login_beliActivity.this, MenuActivity.class));
    }


    private void checkEmailVerification() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        Boolean isLogin = firebaseUser.isEmailVerified();
        startActivity(new Intent(login_beliActivity.this, MapPembeliActivity.class));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLoginPbl:
                LoginUserPbl();
                break;
        }

    }
}
