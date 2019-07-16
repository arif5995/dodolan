package com.arif.gedor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;

import com.arif.gedor.Login.login_beliActivity;
import com.arif.gedor.Login.login_pdgActivity;

public class MenuActivity extends AppCompatActivity {
    private Button rvPedagang, rvPembeli;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        rvPedagang = findViewById(R.id.btnLoginPdg);
        rvPembeli = findViewById(R.id.btnLoginPbl);

        rvPedagang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, login_pdgActivity.class));

            }
        });

        rvPembeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this,login_beliActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed(){
      moveTaskToBack(true);
    }

}
