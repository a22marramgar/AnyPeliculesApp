package com.example.anypeliculesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL = "https://apiservice-u435.onrender.com";
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

    private final int segundosCountdown = 30;

    ValueAnimator colorAnimation;

    Date startDate;
    Date endDate;

    RadioGroup.OnCheckedChangeListener radioGroupListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            storeCheckedIndex();
            if (checkedId != -1) {
                enableRadiobuttons(false);
                nextButton.setEnabled(true);
                RadioButton button = ((RadioButton) radioGroup.getChildAt(preguntas.get(currentPregunta).getRespostaCorrecta()-1));
                button.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.radio_selected_green));
                if (selectedRespostes[currentPregunta] != -1
                        && selectedRespostes[currentPregunta] + 1 == preguntas.get(currentPregunta).getRespostaCorrecta()) {

                    Log.e("check", "Respuesta correcta");
                } else if (selectedRespostes[currentPregunta] != -1) {
                    Log.e("check", "respuesta incorrecta");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        configurarApi();
        llamarApi();

    }

    public void llamarApi() {
        bindViews();
        // Realizar la llamada a getPreguntes
        Call<JsonResponseModel> call = getApiService().getPreguntes();
        Log.e("api","espero respuesta");
        call.enqueue(new Callback<JsonResponseModel>() {


            @Override
            public void onResponse(@NonNull Call<JsonResponseModel> call, @NonNull Response<JsonResponseModel> response) {
                if (response.isSuccessful()) {
                    // Procesar la respuesta exitosa aquí
                    preguntas = new ArrayList<>();
                    currentPregunta = 0;
                    JsonResponseModel jsonResponse = response.body();
                    if (jsonResponse != null) {
                        for (Pregunta1 pregunta : jsonResponse.getPreguntes()) {
                            List<String> respostes = new ArrayList<>();
                            for(int i = 0; i<pregunta.getRespostes().size();i++){
                                respostes.add(pregunta.getRespostes().get(i).getAny());
                            }
                            Pregunta addPregunta = new Pregunta(pregunta.getId(),pregunta.getPregunta(), respostes,pregunta.getResposta_correcta(),pregunta.getImatge());
                            preguntas.add(addPregunta);
                        }
                    }else{
                        CustomDialogFragment dialog = new CustomDialogFragment(
                                "Parece que ha habido un error en el servidor" +
                                        "\nPrueba más tarde o contacta con el desarrollador",
                                "Reintentar",
                                "Cancelar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Acción positiva
                                        llamarApi();
                                    }
                                },
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Acción negativa
                                        finishAffinity();
                                    }
                                });
                        dialog.setCancelable(false);
                        dialog.show(getSupportFragmentManager(), "CUSTOM_DIALOG");
                    }
                    startGame();
                } else {
                    // Manejar error en la respuesta
                    Log.e("api", "error en la respuesta");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonResponseModel> call, @NonNull Throwable t) {
                // Manejar error en la solicitud
                Log.e("api", "error: "+t);
                CustomDialogFragment dialog = new CustomDialogFragment(
                        "No es posible conectar con el server\nVuelve a probar en 15-30 segundos",
                        "Reintentar",
                        "Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Acción positiva
                                llamarApi();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Acción negativa
                                finishAffinity();
                            }
                        });
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), "CUSTOM_DIALOG");
            }
        });
    }

    private void configurarApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
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
        Arrays.fill(selectedRespostes, -1);
        updateView();
        radioGroup.setOnCheckedChangeListener(radioGroupListener);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {



                if(currentPregunta<preguntas.size()-1){
                    //Log.e("data", selectedRespostes[0]+" "+selectedRespostes[1]+" "+selectedRespostes[2]+" "+selectedRespostes[3]+" "+selectedRespostes[4]+" "+selectedRespostes[5]+" "+selectedRespostes[6]+" "+selectedRespostes[7]+" "+selectedRespostes[8]+" "+selectedRespostes[9]);
                    RadioButton button = ((RadioButton) radioGroup.getChildAt(preguntas.get(currentPregunta).getRespostaCorrecta()-1));
                    button.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.radio_selector));
                    currentPregunta++;
                    if(currentPregunta==preguntas.size()-1) nextButton.setText(R.string.Send);
                    updateView();
                    nextButton.setEnabled(false);
                    selectStoredCheck();

                }else{
                    checkRespuestas();
                }
            }

        });
        startAnimation();

    }

    private void selectStoredCheck() {
        radioGroup.setOnCheckedChangeListener(null);
        radioGroup.clearCheck();
        radioGroup.setOnCheckedChangeListener(radioGroupListener);
        enableRadiobuttons(true);
        if(selectedRespostes[currentPregunta] != -1) {
            RadioButton radiobutton = (RadioButton) radioGroup.getChildAt(selectedRespostes[currentPregunta]);
            radiobutton.setChecked(true);
        }
    }

    private void enableRadiobuttons(boolean b) {
        radioButton1.setEnabled(b);
        radioButton2.setEnabled(b);
        radioButton3.setEnabled(b);
        radioButton4.setEnabled(b);
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
        StringBuilder respostes_buides = new StringBuilder();
        for(int i = 0; i<selectedRespostes.length;i++){
            if(selectedRespostes[i] == -1){
                respostes_buides.append(i + 1).append(" ");
            }
        }
        if(!(respostes_buides.toString().equals(""))){
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
        if(colorAnimation != null){
            colorAnimation.end();
        }
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
        Call<Void> call = getApiService().postRespuestas(respostesmodel);

        int finalContadorCorrectas = contadorCorrectas;
        call.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // Procesar la respuesta exitosa aquí
                    Log.e("Respostes", "Respostes enviades");
                    String mensaje = "Respuestas correctas: "+ finalContadorCorrectas +"/"+selectedRespostes.length;
                    CustomDialogFragment dialog = new CustomDialogFragment(
                            mensaje,
                            "Jugar de nuevo",
                            "Salir",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Acción positiva
                                    llamarApi();
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Acción negativa
                                    finishAffinity();
                                }
                            });
                    dialog.setCancelable(false);
                    dialog.show(getSupportFragmentManager(), "CUSTOM_DIALOG");
                } else {
                    // Manejar error en la respuesta
                    Log.e("api", "error en la respuesta");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Manejar error en la solicitud
                Log.e("api", "error: "+t);
                // Crear una instancia de CustomDialogFragment con mensajes y botones personalizados
                CustomDialogFragment dialog = new CustomDialogFragment(
                        "No es posible conectar con el server\nEspera unos segundos y vuelve a probar",
                        "Reiniciar",
                        "Salir",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Acción positiva personalizada
                                // Por ejemplo, realizar alguna acción o cerrar la actividad
                                finishGame();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Acción negativa
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
        radioButton1.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.radio_selector));
        radioButton2.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.radio_selector));
        radioButton3.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.radio_selector));
        radioButton4.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.radio_selector));
        radioButton1.setEnabled(true);
        radioButton2.setEnabled(true);
        radioButton3.setEnabled(true);
        radioButton4.setEnabled(true);

        //Image
        new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                .execute(pregunta.getURLImage());
    }

    private void bindViews() {
        Typeface font = Typeface.createFromAsset(getAssets(), "Montserrat-Bold.otf");
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
        nomPeliTextView.setText(R.string.Loading);
        radioButton1.setText("...");
        radioButton2.setText("...");
        radioButton3.setText("...");
        radioButton4.setText("...");

        imageView = findViewById(R.id.imageView);
        imageView.setShapeAppearanceModel(imageView.getShapeAppearanceModel().toBuilder()
                .setAllCorners(cornerFamily,40).build());
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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
        progressAnimator.setDuration(segundosCountdown* 1000L);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.start();

        int colorFrom = ContextCompat.getColor(this,R.color.green);
        int colorTo = ContextCompat.getColor(this,R.color.strong_red);
        if(colorAnimation != null){
            colorAnimation.end();
        }
        colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(segundosCountdown* 1000L); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mProgressBar.setProgressTintList(ColorStateList.valueOf((int) animator.getAnimatedValue()));
                if(mProgressBar.getProgress() == mProgressBar.getMax()){
                    Log.e("Time","se ha acabado el tiempo");
                    CustomDialogFragment dialog = new CustomDialogFragment(
                            "Se ha acabdo el tiempo!",
                            "Reintentar",
                            "Salir",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Acción positiva
                                    llamarApi();
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Acción negativa
                                    finishAffinity();
                                }
                            });
                    dialog.setCancelable(false);
                    dialog.show(getSupportFragmentManager(), "CUSTOM_DIALOG");
                }
            }

        });
        colorAnimation.start();
        startDate = new Date();
    }

}


