package com.example.anypeliculesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] respostes = new String[4];
        respostes[0] = "2022";
        respostes[1] = "2004";
        respostes[2] = "2022";
        respostes[3] = "2004";
        crearPregunta("Prueba", respostes);
        Button button = findViewById(R.id.testButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setText("Holaa");
            }
        });
    }

    public void crearPregunta(String nomPeli, String[] respostestext){
        LinearLayout layout = findViewById(R.id.mainlayout);

        TextView peli= new TextView(this);
        peli.setText(nomPeli);
        peli.setTextSize(34);
        peli.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        layout.addView(peli);

        RadioGroup radiogroup = new RadioGroup(this);
        RadioButton[] respuestas = new RadioButton[4];
        for(int i =0; i<respostestext.length; i++) {
            respuestas[i] = new RadioButton(this);
            respuestas[i].setText(respostestext[i]);
            respuestas[i].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            radiogroup.addView(respuestas[i]);
        }
        layout.addView(radiogroup);

    }


}

