package com.humeyramercan.catchthecartooncharacters;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity3 extends AppCompatActivity {

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        intent = new Intent(MainActivity3.this, MainActivity2.class);
    }

    public void slow(View view) {
        intent.putExtra("gameMode", "slow");
        startActivity(intent);
    }
    public  void normal(View view){
        intent.putExtra("gameMode","normal");
        startActivity(intent);
    }
    public void fast(View view){
        intent.putExtra("gameMode","fast");
        startActivity(intent);
    }
    public void onBackPressed() { //geri tuşuna basıldığında başlangıç aktivitesine gidip mod seçme aktivitesini kapatıyoruz
        super.onBackPressed();
        Intent intent=new Intent(MainActivity3.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}