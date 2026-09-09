package com.example.rollthedice;

import android.graphics.Color;
import android.graphics.fonts.FontStyle;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.annotation.RequiresApi;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ImageView[] diceImageViews;
    private TextView scoreTextView, sumScoreTextView, generalScoreTextView, attemptsTextView;
    private final int diceRandomImage = R.drawable.random;
    private int generalScore = 0;
    private int sumScore = 0;
    private int attempts = 0;
    private ToneGenerator toneGen;
    private MediaPlayer mediaPlayer;
    private final int[] diceImages = {
            R.drawable.one,
            R.drawable.two,
            R.drawable.three,
            R.drawable.four,
            R.drawable.five,
            R.drawable.six
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        diceImageViews = new ImageView[] {
                findViewById(R.id.diceImageView1),
                findViewById(R.id.diceImageView2),
                findViewById(R.id.diceImageView3),
                findViewById(R.id.diceImageView4),
                findViewById(R.id.diceImageView5)
        };

        scoreTextView = findViewById(R.id.scoreTextView);
        sumScoreTextView = findViewById(R.id.sumScoreTextView);
        generalScoreTextView = findViewById(R.id.generalScoreTextView);
        attemptsTextView = findViewById(R.id.attemptsTextView);

        Button rollBtn = findViewById(R.id.rollBtn);
        Button resetBtn = findViewById(R.id.resetBtn);

        reset();

        toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 80);

        mediaPlayer = MediaPlayer.create(this, R.raw.goodresult);
        mediaPlayer.setVolume(0.1f, 0.1f);

        rollBtn.setOnClickListener(v -> {
            if (attempts >= 5) {
                playFanfare();
                toastGeneralSum();
                reset();
            } else {
                playBeep();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                roll();
            }
        });

        resetBtn.setOnClickListener(v -> reset());
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void roll() {
        Random rand = new Random();

        int[] scores = {
                1 + rand.nextInt(6),
                1 + rand.nextInt(6),
                1 + rand.nextInt(6),
                1 + rand.nextInt(6),
                1 + rand.nextInt(6)
        };

        int i = 0;
        for (ImageView dice : diceImageViews) {
            int j = 0;
            for (int score : scores) {
                if (i == j) {
                    dice.setImageResource(diceImages[score-1]);

                }
                j++;
            }
            i++;
        }

        String scoreText = "Wylosowane liczby: ";
        for (int score : scores) {
            sumScore += score;
            scoreText += (score + ", ");
        }
        scoreText += "\b\b.";

        generalScore += sumScore;

        scoreTextView.setText(scoreText);
        sumScoreTextView.setText("Suma oczek: " + sumScore + ".");
        attemptsTextView.setText("Ilość prób: " + ++attempts + ".");

        String generalScoreString = Integer.toString(generalScore);
        String generalScoreText = "Suma ogólna: " + generalScore + ".";
        SpannableString generalScoreFormated = new SpannableString(generalScoreText);
        int start = generalScoreText.indexOf(generalScoreString);
        int end = start + generalScoreString.length();

        generalScoreFormated.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        generalScoreFormated.setSpan(new RelativeSizeSpan(1.5f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        generalScoreFormated.setSpan(FontStyle.FONT_WEIGHT_MAX, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        generalScoreTextView.setText(generalScoreFormated);
    }

    private void reset() {
        for (ImageView dice : diceImageViews) {
            dice.setImageResource(diceRandomImage);
        }

        scoreTextView.setText(R.string.score);
        sumScoreTextView.setText(R.string.sumScore);
//        generalScoreTextView.setText(R.string.generalScore);
        attemptsTextView.setText(R.string.attempts);

        sumScore = 0;
//        generalScore = 0;
        attempts = 0;
    }

    private void playBeep() {
        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 200);
    }

    private void playFanfare() {
        mediaPlayer.start();
    }

    private void toastGeneralSum() {
        Toast.makeText(this, "Koniec gry! Zdobyte punkty: " + generalScore + ".", Toast.LENGTH_LONG).show();
    }

//    private String parseIntToString(int int_) {
//        switch (int_) {
//            case 1:
//                return "one";
//            case 2:
//                return "two";
//            case 3:
//                return "three";
//            case 4:
//                return "four";
//            case 5:
//                return "five";
//        }
//        return "random";
//    }
}