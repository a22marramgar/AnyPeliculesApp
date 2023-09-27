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

    List<Pregunta> preguntas = new ArrayList<>();
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
                ArrayList<Integer> respostes_buides = new ArrayList();
                Log.e("data",""+preguntas.size());
                for(Pregunta pregunta : preguntas){
                    int id = pregunta.getRespostes_radiogroup().getCheckedRadioButtonId();
                    if (id == -1){
                        respostes_buides.add(pregunta.getId());
                    }
                }
                String text = "Falten les respostes: " + respostes_buides.toString();
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

        Pregunta pregunta = new Pregunta(nomPeli,respostestext,this);
        preguntas.add(pregunta);
        layout.addView(pregunta.getNom_textview());
        layout.addView(pregunta.getRespostes_radiogroup());

    }
}

