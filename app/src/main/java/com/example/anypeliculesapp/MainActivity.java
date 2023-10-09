package com.example.anypeliculesapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static ApiService apiService;

    List<Pregunta> preguntas = new ArrayList<>();

    private int[] respostesCorrectes;

    private int currentPregunta = 0;

    int[] selectedRespostes;
    Button backButton;
    Button nextButton;
    TextView nomPeliTextView;
    RadioGroup radioGroup;

    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    RadioButton radioButton4;

    ShapeableImageView imageView;

    int cornerFamily= CornerFamily.ROUNDED,topLeft=0,topRight=0,bottomLeft=0,bottomRight=0; //image corners

    private int segundosCountdown = 30;

    Date startDate;
    Date endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configurarApi();
        llamarApi();

    }

    public void llamarApi() {

        // Realizar la llamada a getPreguntes
        Call<JsonResponseModel> call = this.getApiService().getPreguntes();
        Log.e("api","espero respuesta");
        call.enqueue(new Callback<JsonResponseModel>() {


            @Override
            public void onResponse(Call<JsonResponseModel> call, Response<JsonResponseModel> response) {
                if (response.isSuccessful()) {
                    // Procesar la respuesta exitosa aquí
                    preguntas = new ArrayList<>();
                    currentPregunta = 0;
                    JsonResponseModel jsonResponse = response.body();
                    for (Pregunta1 pregunta : jsonResponse.getPreguntes()) {
                        List<String> respostes = new ArrayList<>();
                        for(int i = 0; i<pregunta.getRespostes().size();i++){
                            respostes.add(pregunta.getRespostes().get(i).getAny());
                        }
                        Pregunta addPregunta = new Pregunta(pregunta.getId(),pregunta.getPregunta(), respostes,pregunta.getResposta_correcta(),pregunta.getImatge());
                        preguntas.add(addPregunta);
                    }

                    bindViews();
                    startGame();
                } else {
                    // Manejar error en la respuesta
                    Log.e("api", "error en la respuesta");
                }
            }

            @Override
            public void onFailure(Call<JsonResponseModel> call, Throwable t) {
                // Manejar error en la solicitud
                Log.e("api", "error: "+t);
                CustomDialogFragment dialog = new CustomDialogFragment(
                        "No es posible conectar con el server\n Vuelve a probar en 15-30 segundos",
                        "Reintentar",
                        "Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Acción positiva personalizada
                                // Por ejemplo, realizar alguna acción o cerrar la actividad
                                llamarApi();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Acción negativa personalizada
                                // Por ejemplo, realizar alguna otra acción o cerrar la actividad
                                finishAffinity();
                            }
                        });
                dialog.show(getSupportFragmentManager(), "CUSTOM_DIALOG");
            }
        });
    }

    private void configurarApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apiservice-u435.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);


    }

    public static ApiService getApiService() {
        return apiService;
    }

    private void startGame() {
        respostesCorrectes = new int[preguntas.size()];
        selectedRespostes = new int[preguntas.size()];
        for(int i = 0; i<selectedRespostes.length; i++){
            selectedRespostes[i] = -1;
        }
        updateView();
        backButton.setEnabled(false);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //selectedRespostes[currentPregunta] = radioGroup.getCheckedRadioButtonId();
                storeCheckedIndex();


                if(currentPregunta<preguntas.size()-1){
                    //Log.e("data", selectedRespostes[0]+" "+selectedRespostes[1]+" "+selectedRespostes[2]+" "+selectedRespostes[3]+" "+selectedRespostes[4]+" "+selectedRespostes[5]+" "+selectedRespostes[6]+" "+selectedRespostes[7]+" "+selectedRespostes[8]+" "+selectedRespostes[9]);
                    currentPregunta++;
                    if(currentPregunta==preguntas.size()-1) nextButton.setText("Enviar");
                    selectStoredCheck();

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
                storeCheckedIndex();
                //Log.e("data", selectedRespostes[0]+" "+selectedRespostes[1]+" "+selectedRespostes[2]+" "+selectedRespostes[3]+" "+selectedRespostes[4]+" "+selectedRespostes[5]+" "+selectedRespostes[6]+" "+selectedRespostes[7]+" "+selectedRespostes[8]+" "+selectedRespostes[9]);
                currentPregunta--;
                selectStoredCheck();
                nextButton.setText("Siguiente");
                updateView();
                if(currentPregunta==0){
                    backButton.setEnabled(false);
                }

            }
        });
        startAnimation();

    }

    private void selectStoredCheck() {
        radioGroup.clearCheck();
        if(selectedRespostes[currentPregunta] != -1) {
            RadioButton radiobutton = (RadioButton) radioGroup.getChildAt(selectedRespostes[currentPregunta]);
            radiobutton.setChecked(true);
        }
    }

    private void storeCheckedIndex() {
        View radiobutton = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        if(radiobutton == null){
            selectedRespostes[currentPregunta] = -1;
        }else{
            selectedRespostes[currentPregunta] = radioGroup.indexOfChild(radiobutton);
        }
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
        endDate = new Date();
        List<RespostaModel> respuestas = new ArrayList<>();
        for(int i = 0; i<selectedRespostes.length; i++){

            if(selectedRespostes[i]+1 == preguntas.get(i).getRespostaCorrecta()){
                contadorCorrectas++;
                respuestas.add(new RespostaModel(preguntas.get(i).getId(),
                        preguntas.get(i).getRespostes().get(selectedRespostes[i]), true));
            }else{
                Log.e("Mal", preguntas.get(i).getNom()
                        +", "+preguntas.get(i).getRespostaCorrecta()
                        +", seleccionado "+(selectedRespostes[i]+1));
                respuestas.add(new RespostaModel(preguntas.get(i).getId(),
                        preguntas.get(i).getRespostes().get(selectedRespostes[i]), false));
            }
        }
        RespostesModel respostesmodel = new RespostesModel(respuestas,
                (endDate.getTime()-startDate.getTime())/1000.0f);
        Call<Void> call = this.getApiService().postRespuestas(respostesmodel);

        int finalContadorCorrectas = contadorCorrectas;
        call.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Procesar la respuesta exitosa aquí
                    Log.e("Respostes", "Respostes enviades");
                    String mensaje = "Respuestas correctas: "+ finalContadorCorrectas +"/"+selectedRespostes.length;
                    CustomDialogFragment dialog = new CustomDialogFragment(
                            mensaje,
                            "Jugar de nuevo",
                            "Cancelar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Acción positiva personalizada
                                    // Por ejemplo, realizar alguna acción o cerrar la actividad
                                    llamarApi();
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Acción negativa personalizada
                                    // Por ejemplo, realizar alguna otra acción o cerrar la actividad
                                    finishAffinity();
                                }
                            });
                    dialog.show(getSupportFragmentManager(), "CUSTOM_DIALOG");
                } else {
                    // Manejar error en la respuesta
                    Log.e("api", "error en la respuesta");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Manejar error en la solicitud
                Log.e("api", "error: "+t);
                // Crear una instancia de CustomDialogFragment con mensajes y botones personalizados
                CustomDialogFragment dialog = new CustomDialogFragment(
                        "No es posible conectar con el server\n Espera unos segundos y vuelve a probar",
                        "Reiniciar",
                        "Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Acción positiva personalizada
                                // Por ejemplo, realizar alguna acción o cerrar la actividad
                                finishGame();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Acción negativa personalizada
                                // Por ejemplo, realizar alguna otra acción o cerrar la actividad
                                finishAffinity();
                            }
                        });
                dialog.show(getSupportFragmentManager(), "CUSTOM_DIALOG");
            }
        });
        Log.e("Correctas", String.valueOf(contadorCorrectas));

    }

    private void updateView() {
        Pregunta pregunta = preguntas.get(currentPregunta);
        nomPeliTextView.setText((currentPregunta+1)+". "+ pregunta.getNom());
        radioButton1.setText(pregunta.getRespostes().get(0).toString());
        radioButton2.setText(pregunta.getRespostes().get(1).toString());
        radioButton3.setText(pregunta.getRespostes().get(2).toString());
        radioButton4.setText(pregunta.getRespostes().get(3).toString());

        //Image
        new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                .execute(pregunta.getURLImage());
    }

    private void bindViews() {
        Typeface font = Typeface.createFromAsset(getAssets(), "Montserrat-Bold.otf");
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        nomPeliTextView = findViewById(R.id.nomPeliTextView);
        radioGroup = findViewById(R.id.radiogroup);
        radioGroup.clearCheck();
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        radioButton4 = findViewById(R.id.radioButton4);
        nomPeliTextView.setTypeface(font);
        radioButton1.setTypeface(font);
        radioButton2.setTypeface(font);
        radioButton3.setTypeface(font);
        radioButton4.setTypeface(font);
        nextButton.setTypeface(font);
        backButton.setTypeface(font);

        imageView = findViewById(R.id.imageView);
        imageView.setShapeAppearanceModel(imageView.getShapeAppearanceModel().toBuilder()
                .setAllCorners(cornerFamily,40).build());
    }

    public void crearPregunta(int id, String nomPeli, List<String> respostestext, int resposta_correcta,String URLImage){

        Pregunta pregunta = new Pregunta(id, nomPeli,respostestext, resposta_correcta,URLImage);
        preguntas.add(pregunta);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private void startAnimation(){
        ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setMax(nomPeliTextView.getMaxWidth());
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(mProgressBar, "progress", 0, mProgressBar.getMax());
        progressAnimator.setDuration(segundosCountdown*1000);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.start();

        int colorFrom = getResources().getColor(R.color.green);
        int colorTo = getResources().getColor(R.color.strong_red);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(segundosCountdown*1000); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mProgressBar.setProgressTintList(ColorStateList.valueOf((int) animator.getAnimatedValue()));
                if(mProgressBar.getProgress() == mProgressBar.getMax()){
                    Log.e("Time","se ha acabado el tiempo");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

// 2. Chain together various setter methods to set the dialog characteristics.
                    builder.setMessage(R.string.dialog_msg)
                            .setTitle(R.string.dialog_msg);

// 3. Get the AlertDialog.
                    AlertDialog dialog = builder.create();

                }
            }

        });
        colorAnimation.start();
        startDate = new Date();
    }

    /*private String loadJSONFromAsset() {
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
        //Log.e("data",json);
        return json;
    }*/
    /*private void leerJSON() {
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
                Pregunta addPregunta = new Pregunta(Integer.parseInt(pregunta.getString("id")),
                        pregunta.getString("pregunta"),
                        list,
                        Integer.parseInt(pregunta.getString("resposta_correcta")),
                        pregunta.getString("imatge"));
                preguntas.add(addPregunta);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }*/

}


