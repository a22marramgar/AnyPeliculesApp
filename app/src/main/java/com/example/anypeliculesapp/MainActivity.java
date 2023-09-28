package com.example.anypeliculesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Pregunta> preguntas = new ArrayList<>();

    private int[] respostesCorrectes = new int[10];

    private int currentPregunta = 0;

    int[] selectedRespostes = new int[10];
    Button backButton;
    Button nextButton;
    TextView nomPeliTextView;
    RadioGroup radioGroup;

    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    RadioButton radioButton4;

    private static final int RB1_ID = 2131231222;//first radio button id
    private static final int RB2_ID = 2131231065;//second radio button id
    private static final int RB3_ID = 2131231066;//third radio button id
    private static final int RB4_ID = 2131231067;//fourth radio button id
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

                crearPregunta(pregunta.getString("pregunta"),
                        list,
                        Integer.parseInt(pregunta.getString("resposta_correcta")),
                        pregunta.getString("imatge"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        bindViews();
        startGame();
    }

    private void startGame() {
        for(int i = 0; i<selectedRespostes.length; i++){
            selectedRespostes[i] = -1;
        }
        updateView();
        backButton.setEnabled(false);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedRespostes[currentPregunta] = radioGroup.getCheckedRadioButtonId();
                if(currentPregunta<preguntas.size()-1){
                    //Log.e("data", selectedRespostes[0]+" "+selectedRespostes[1]+" "+selectedRespostes[2]+" "+selectedRespostes[3]+" "+selectedRespostes[4]+" "+selectedRespostes[5]+" "+selectedRespostes[6]+" "+selectedRespostes[7]+" "+selectedRespostes[8]+" "+selectedRespostes[9]);
                    currentPregunta++;
                    if(currentPregunta==preguntas.size()-1) nextButton.setText("Enviar");
                    radioGroup.check(selectedRespostes[currentPregunta]);

                    updateView();
                }else{
                    checkRespuestas();

                }
                if(currentPregunta!= 0){
                    backButton.setEnabled(true);
                }else{
                    backButton.setEnabled(false);
                }


            }

        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedRespostes[currentPregunta] = radioGroup.getCheckedRadioButtonId();
                //Log.e("data", selectedRespostes[0]+" "+selectedRespostes[1]+" "+selectedRespostes[2]+" "+selectedRespostes[3]+" "+selectedRespostes[4]+" "+selectedRespostes[5]+" "+selectedRespostes[6]+" "+selectedRespostes[7]+" "+selectedRespostes[8]+" "+selectedRespostes[9]);
                currentPregunta--;
                radioGroup.check(selectedRespostes[currentPregunta]);
                nextButton.setText("Siguiente");
                updateView();
                if(currentPregunta==0){
                    backButton.setEnabled(false);
                }

            }
        });

    }

    private void checkRespuestas() {
        String respostes_buides = "";
        for(int i = 0; i<selectedRespostes.length;i++){
            if(selectedRespostes[i] == -1){
                respostes_buides = respostes_buides + (i+1) + " ";
            }
        }
        if(!(respostes_buides.equals(""))){
            Snackbar snackbar = Snackbar.make((RelativeLayout)findViewById(R.id.mainlayout),
                    "Falten les respostes:\n"+respostes_buides,
                    Snackbar.LENGTH_LONG);
            snackbar.show();
        }else{
            finishGame();
        }


    }

    private void finishGame() {
        //TODO end the game
        int contadorCorrectas = 0;
        int respuestaCorrecta = 0;
        for(int i = 0; i<selectedRespostes.length; i++){
            switch (preguntas.get(i).getRespostaCorrecta()){
                case 1:
                    respuestaCorrecta = RB1_ID;
                    break;
                case 2:
                    respuestaCorrecta = RB2_ID;
                    break;
                case 3:
                    respuestaCorrecta = RB3_ID;
                    break;
                case 4:
                    respuestaCorrecta = RB4_ID;
                    break;
            }
            if(selectedRespostes[i] == respuestaCorrecta){
                contadorCorrectas++;
            }else{
                Log.e("Mal", preguntas.get(i).getNom()
                        +", se esperaba "+respuestaCorrecta
                        +", "+preguntas.get(i).getRespostaCorrecta()
                        +", seleccionado "+selectedRespostes[i]);
            }
        }
        Log.e("Correctas", String.valueOf(contadorCorrectas));
    }

    private void updateView() {
        Pregunta pregunta = preguntas.get(currentPregunta);
        nomPeliTextView.setText((currentPregunta+1)+". "+pregunta.getNom());
        radioButton1.setText(pregunta.getRespostes().get(0).toString());
        radioButton2.setText(pregunta.getRespostes().get(1).toString());
        radioButton3.setText(pregunta.getRespostes().get(2).toString());
        radioButton4.setText(pregunta.getRespostes().get(3).toString());
    }

    private void bindViews() {
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        nomPeliTextView = findViewById(R.id.nomPeliTextView);
        radioGroup = findViewById(R.id.radiogroup);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        radioButton4 = findViewById(R.id.radioButton4);
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

    public void crearPregunta(String nomPeli, List<String> respostestext, int resposta_correcta,String URLImage){

        Pregunta pregunta = new Pregunta(nomPeli,respostestext, resposta_correcta,URLImage);
        preguntas.add(pregunta);
    }

}

