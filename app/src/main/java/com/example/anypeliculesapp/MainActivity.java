package com.example.anypeliculesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.OrientationHelper;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] respostes = new String[4];
        respostes[0] = "2002";
        respostes[1] = "2004";
        respostes[2] = "2012";
        respostes[3] = "2004";
        crearPregunta("Avatar", respostes);
        crearPregunta("Prueba2", respostes);
        Button button = findViewById(R.id.sendButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // to get Context
                Context context = getApplicationContext();
                // message to display
                String text = "Toast message";
                // toast time duration, can also set manual value
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                // to show the toast
                toast.show();
            }
        });
    }

    public void crearPregunta(String nomPeli, String[] respostestext){
        LinearLayout layout = findViewById(R.id.linearlayout);

        TextView peli= new TextView(this);
        peli.setText(nomPeli);
        peli.setTextSize(34);
        peli.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        layout.addView(peli);
        RadioGroup radiogroup = new RadioGroup(this);
        layout.addView(radiogroup);
        radiogroup.setGravity(Gravity.CENTER);
        RadioButton[] respuestas = new RadioButton[respostestext.length];
        for(int i =0; i<respostestext.length; i++) {
            respuestas[i] = new RadioButton(this);
            respuestas[i].setText(respostestext[i]);
            respuestas[i].setTextSize(20);
            respuestas[i].setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            radiogroup.addView(respuestas[i]);

        }
    }


}

