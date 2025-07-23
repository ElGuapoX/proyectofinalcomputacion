package com.example.sensores;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class PreguntasActivity extends AppCompatActivity {

    private static final int NUM_PREGUNTAS = 6;
    private final RadioGroup[] grupos = new RadioGroup[NUM_PREGUNTAS];
    private final int[] respuestasCorrectas = {R.id.rb1b, R.id.rb2b, R.id.rb3b, R.id.rb4c, R.id.rb5a, R.id.rb6b};
    private final ImageView[] checks = new ImageView[NUM_PREGUNTAS];
    private int puntajePareo = 0;
    private int puntajePreguntas = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas);

        // Obtener puntaje de PareoActivity con valor por defecto 0
        puntajePareo = getIntent().getIntExtra("puntajePareo", 0);

        // Inicializar referencias
        for (int i = 0; i < NUM_PREGUNTAS; i++) {
            int groupId = getResources().getIdentifier("radioGroup" + (i + 1), "id", getPackageName());
            int checkId = getResources().getIdentifier("checkPregunta" + (i + 1), "id", getPackageName());
            grupos[i] = findViewById(groupId);
            checks[i] = findViewById(checkId);
        }

        Button btnFinalizar = findViewById(R.id.btnFinalizar);
        Button btnVolver = findViewById(R.id.btnVolver);
        TextView resultado = findViewById(R.id.resultado);
        TextView titulo = findViewById(R.id.titulo);

        // Configurar el título
        titulo.setText("Evaluación de Vertebrados e Invertebrados");

        btnFinalizar.setOnClickListener(v -> {
            boolean todasRespondidas = true;
            puntajePreguntas = 0;

            for (int i = 0; i < NUM_PREGUNTAS; i++) {
                int seleccion = grupos[i].getCheckedRadioButtonId();
                if (seleccion == -1) {
                    todasRespondidas = false;
                    checks[i].setVisibility(View.INVISIBLE);
                } else {
                    boolean esCorrecto = (seleccion == respuestasCorrectas[i]);
                    checks[i].setImageResource(esCorrecto ? R.drawable.check : R.drawable.cross);
                    checks[i].setVisibility(View.VISIBLE);
                    if (esCorrecto) puntajePreguntas++;
                }
            }

            if (todasRespondidas) {
                int puntajeTotal = puntajePareo + puntajePreguntas;
                String mensajeResultado = String.format("Tu puntaje final es: %d/12\n(Pareo: %d/6 + Preguntas: %d/6)",
                        puntajeTotal, puntajePareo, puntajePreguntas);
                resultado.setText(mensajeResultado);
            } else {
                Toast.makeText(PreguntasActivity.this,
                        "Por favor responde todas las preguntas",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar el botón Volver para ir a MenuActivity
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(PreguntasActivity.this, MenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Limpiar el stack de actividades
            startActivity(intent);
            finish(); // Finalizar la actividad actual
        });
    }
}