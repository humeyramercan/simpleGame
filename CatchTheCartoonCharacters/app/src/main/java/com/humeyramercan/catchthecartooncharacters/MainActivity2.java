package com.humeyramercan.catchthecartooncharacters;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity2 extends AppCompatActivity {

    ArrayList<Bitmap> characters = new ArrayList<>();
    Bitmap pikachu, homer, cedric, jerry, scooby, garfield;
    ImageView characterToFindImg, characterOnTheRunImg;
    TextView scoreTextView, timeTextView, bestScoreTextView;
    Handler handler;
    Runnable runnable;
    ConstraintLayout myLayout;
    Button deleteBestScoreButton;
    int score, storedScore, gameSpeed;
    SharedPreferences sharedPreferences;
    String gameMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        score = 0;

        Intent intent = getIntent();
        gameMode = intent.getStringExtra("gameMode"); //hangi oyun modunu seçtiysek bu modu aldık;

        pikachu = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pikachu); //karakterlerimizin resimlerini bitmap'e çevirdik
        homer = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.homer);
        cedric = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.cedric);
        jerry = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.jerry);
        scooby = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.scooby);
        garfield = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.garfield);

        characters.add(pikachu); //bitmalerimizi listemize ekledik
        characters.add(homer);
        characters.add(cedric);
        characters.add(jerry);
        characters.add(scooby);
        characters.add(garfield);

        characterToFindImg = findViewById(R.id.characterToFindImg);//viewlarımızı atadık
        characterOnTheRunImg = findViewById(R.id.characterOnTheRunImg);
        scoreTextView = findViewById(R.id.scoreTextView);
        timeTextView = findViewById(R.id.timeTextView);
        myLayout = findViewById(R.id.myLayout);
        bestScoreTextView = findViewById(R.id.bestScoreeTextView);
        deleteBestScoreButton = findViewById(R.id.deleteBestScoreButton);

        //best scoreları tutmak için SharedPreferences oluşturduk
        sharedPreferences = this.getSharedPreferences("com.humeyramercan.catchthecartooncharacters", Context.MODE_PRIVATE);

        if (gameMode.matches("slow")) {
            gameSpeed = 1600; //oyun moduna göre hız belirliyoruz
            storedScore = sharedPreferences.getInt("storedScoreOfSlow", 0); //oyun moduna göre best skorumuz varsa oyun başladığında o score'u bastırıyoruz
            bestScoreTextView.setText("Best Score: " + storedScore);

        } else if (gameMode.matches("normal")) {
            gameSpeed = 1000;
            storedScore = sharedPreferences.getInt("storedScoreOfNormal", 0);
            bestScoreTextView.setText("Best Score: " + storedScore);

        } else if (gameMode.matches("fast")) {
            gameSpeed = 620;
            storedScore = sharedPreferences.getInt("storedScoreOfFast", 0);
            bestScoreTextView.setText("Best Score: " + storedScore);
        }

        characterToFindSettings(); //aktivite başladığı an bulunacak karakter bastırılır
        move(); //aktivite başladığı an karakterler hareket eder

        new CountDownTimer(25000, 1000) { //aktivite başladığı an süre başlar
            @Override
            public void onTick(long l) {
                timeTextView.setText("Time: " + l / 1000);
            }
            @Override
            public void onFinish() {
                handler.removeCallbacks(runnable); //süre bitince runnable durur karakterler hareket etmez

                if(gameMode.matches("slow")) { //oyun modune göre best scorelar kontrol edilir eğer şuanki score kaydediilen best score 'dan büyükse oyun moduna uygun olan sharedPreferences'a yeni skor kaydedilir
                    scoreByGameMode("storedScoreOfSlow");
                }
                else if(gameMode.matches("normal")) {
                  scoreByGameMode("storedScoreOfNormal");
                }
                else if(gameMode.matches("fast")) {
                  scoreByGameMode("storedScoreOfFast");
                }
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity2.this);
                alert.setCancelable(false); //uyarı mesajı çıkınca mesaj dışında başka bir yere tıklanmasına izin verilmiyor
                alert.setTitle("RESTART");
                alert.setMessage("Do you want to restart game?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        restart(); //eger yeniden oynanmak isterse aktivitemizi yeniden başlatıyoruz
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainActivity2.this, MainActivity.class); //eğer ki yeniden oynanmak istenmiyorsa başlangıç ekranına geri dönüyoruz
                        startActivity(intent);
                    }
                });
                alert.show();
            }
        }.start();
    }
    public void scoreByGameMode(String sharedPreferencesKeyName){ //sharedPreferences key bilgisine göre hangi oyun modundaysak o modun best score'unu kontrol eden methodumuz
        storedScore = sharedPreferences.getInt(sharedPreferencesKeyName, 0); //kaydedilmiş bir best score varsa onu getir yoksa 0 getir
        if (score > storedScore) { //eğer şuanki skor kaydedilmiş scoredan büyükse bu yeni best skordur bu skoru kaydederiz
            sharedPreferences.edit().putInt(sharedPreferencesKeyName, score).apply();
        }
    }

    public void characterToFindSettings() { //bulunacak karakterin random karakter ataması ve tag atamasını yapan metodumuz
        Random i = new Random();
        int randomIndex = i.nextInt(6);
        characterToFindImg.setImageBitmap(characters.get(randomIndex));
        characterToFindImg.setTag(randomIndex); //karakterlerin bulunduğu listedeki kaçıncı karakter ayarlandıysa onun indeksi tag olarak verildi
    }

    //karakterlerin hareket etmesini sağlayan methodumuz
    public void move() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Random i = new Random();
                int randomIndex = i.nextInt(6); //listemizde 6 karakter olduğu için 0-6 arası random sayı ürettik
                characterOnTheRunImg.setImageBitmap(characters.get(randomIndex));
                characterOnTheRunImg.setTag(randomIndex);//yine listede hangi eleman kaçan karakter imageViewunda gösteriliyorsa o elemanın indeksi bu Imageview a tag olarak verildi
                int randomX = i.nextInt(myLayout.getWidth() - characterOnTheRunImg.getWidth()); //rastgele verilen konumlarda resim taşmasın diye
                int randomY = i.nextInt(myLayout.getHeight() - characterOnTheRunImg.getHeight());//layout genişliğinden imageView genişliğini çıkardık ve 1 ile bu değer arasında rastgele sayılar ürettik
                characterOnTheRunImg.setX(randomX); //kaçan karakterimizin konumuzunu ürettiğimiz rastgele sayılara göre ayarladık
                characterOnTheRunImg.setY(randomY);
                handler.postDelayed(runnable, gameSpeed); //oyun moduna göre gecikme süresini verdik
            }
        };
        handler.post(runnable);
    }

    //hareket eden karaktere tıkladığımızda skoru değiştirir
    public void changeScore(View view) {
        int tag1 = (int) characterOnTheRunImg.getTag(); //ImageViewlara verilen taglar inte çevrildi
        int tag2 = (int) characterToFindImg.getTag();
        if (tag1 == tag2) { //eğer ki iki imageViewun tagları eşitse ikiside listedeki aynı indeksteki karaktere sahiptir
            score++;
            scoreTextView.setText("Score: " + score);
            characterToFindSettings(); //kaçan karaktere her tıkladığımızda tekrardan bulunacak karakter değişir ve tag ataması yapılır
        } else {
            characterToFindSettings(); //yanlış karakteri yakaladığımda da bulunacak karakteri değitiriyoruz
            if (score > 0) {
                score--;
                scoreTextView.setText("Score: " + score);
            }
        }
    }

    public void deleteByGameMode(String sharedPreferencesKeyName){ // sharedpreferences'daki key değerine göre kayıtlı olan best puanı silen method

            int scoreToDeleted = sharedPreferences.getInt(sharedPreferencesKeyName, 0);
            if (scoreToDeleted > 0) { //eğer ki kayıtlı bir best skorumuz varsa ve 0 dan büyükse bunu siliyoruz
                sharedPreferences.edit().remove(sharedPreferencesKeyName).apply();
                int deletedScore = sharedPreferences.getInt(sharedPreferencesKeyName, 0);
                bestScoreTextView.setText("Best Score: " + deletedScore);
            }

    }

    public void deleteBestScore(View view) { //oyun moduna göre best score silen methodumuz
        if (gameMode.matches("slow")) { //oyun moduna göre ilgili sharedPreferences keywordumuzu veriyoruz ve ilgili oyun modunun best score unu siliyioruz
        deleteByGameMode("storedScoreOfSlow");
        }
        else if (gameMode.matches("normal")) {
           deleteByGameMode("storedScoreOfNormal");
        }
        else if (gameMode.matches("fast")) {
            deleteByGameMode("storedScoreFast");
        }
    }
    //API kontrolüne göre aktivitemizi yeniden başlatan method
    public void restart() {
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            recreate();
        } else {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }
    public void onBackPressed() { //geri tuşuna bastığımızda mod seçme aktivitesine gidip oyun aktivitesini kapatıyoruz
        super.onBackPressed();
        Intent intent=new Intent(MainActivity2.this,MainActivity3.class);
        startActivity(intent);
        finish();
    }
}