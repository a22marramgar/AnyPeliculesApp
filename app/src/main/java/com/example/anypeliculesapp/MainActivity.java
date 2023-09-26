package com.example.anypeliculesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray preguntes = obj.getJSONArray("preguntes");
            for(int i = 0; i< preguntes.length(); i++){
                JSONObject pregunta = preguntes.getJSONObject(i);
                JSONArray respostes = pregunta.getJSONArray("respostes");
                List<String> list = new ArrayList<String>();
                for(int j = 0; j < respostes.length(); j++){
                    JSONObject resposta = respostes.getJSONObject(j);

                    list.add(resposta.getString("any"));

                }

                crearPregunta(pregunta.getString("pregunta"),list);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        Button button = new Button(this);
        LinearLayout layout = findViewById(R.id.linearlayout);
        button.setText("ENVIAR");
        button.setTextSize(34);
        layout.addView(button);
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

    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream inputStream = this.getAssets().open("preguntas.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer,"UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Log.e("data",json);
        return json;
    }

    public void crearPregunta(String nomPeli, List respostestext){
        LinearLayout layout = findViewById(R.id.linearlayout);

        TextView peli= new TextView(this);
        peli.setText(nomPeli);
        peli.setTextSize(34);
        peli.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        layout.addView(peli);

        RadioGroup radiogroup = new RadioGroup(this);
        radiogroup.setGravity(Gravity.CENTER);
        layout.addView(radiogroup);

        RadioButton[] respuestas = new RadioButton[respostestext.size()];
        for(int i =0; i<respostestext.size(); i++) {
            respuestas[i] = new RadioButton(this);
            respuestas[i].setText(respostestext.get(i).toString());
            respuestas[i].setTextSize(20);
            respuestas[i].setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            radiogroup.addView(respuestas[i]);

        }
    }
}

