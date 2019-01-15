package com.stucom.rushd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class FirstView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstview);



        Button btn_Registrar = findViewById(R.id.btn_registrar);
        btn_Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstView.this, Registrarse.class);
                startActivity(intent);
            }
        });
        Button btnSet = findViewById(R.id.btn_settings);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstView.this, Settings.class);
                startActivity(intent);
            }
        });


    }



}
