package com.bscs.resellingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class orderPlaced extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);


        Button btnHome=findViewById(R.id.home);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent check=new Intent(orderPlaced.this,HomeScreen.class);
                check.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(check);
            }
        });

    }
}