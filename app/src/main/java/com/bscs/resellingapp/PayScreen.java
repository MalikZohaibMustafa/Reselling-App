package com.bscs.resellingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PayScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_screen);

        Button btnConfirm=findViewById(R.id.confirmpayment);
        Button btnsave=findViewById(R.id.savecreditcard);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent check=new Intent(PayScreen.this,orderPlaced.class);
                check.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(check);
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent save =new Intent(PayScreen.this,CreditInfo.class);
                save.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(save);
            }
        });
    }
}