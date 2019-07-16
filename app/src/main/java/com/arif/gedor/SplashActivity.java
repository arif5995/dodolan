package com.arif.gedor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.arif.gedor.Login.login_pdgActivity;

public class SplashActivity extends AppCompatActivity {
    LinearLayout up,down;
    Button btnGabung;
    Animation uptodown, downtoup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        btnGabung = (Button)findViewById(R.id.btnGbung);
        up = (LinearLayout)findViewById(R.id.Up);
        down = (LinearLayout)findViewById(R.id.down);
        uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this, R.anim.downtoup);
        btnGabung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, MenuActivity.class));
                finish();

            }
        });
        up.setAnimation(uptodown);
        down.setAnimation(downtoup);
    }
}
