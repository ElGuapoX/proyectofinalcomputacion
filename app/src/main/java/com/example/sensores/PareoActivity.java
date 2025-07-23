package com.example.sensores;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class PareoActivity extends AppCompatActivity {

    private static final int NUM_PREGUNTAS = 6;
    private final Spinner[] spinners = new Spinner[NUM_PREGUNTAS];
    private final ImageView[] checks = new ImageView[NUM_PREGUNTAS];
    private final String[] respuestasCorrectas = {"Vertebrado", "Invertebrado", "Vertebrado", "Invertebrado", "Vertebrado", "Invertebrado"};
    private final String[] opciones = {"Selecciona", "Vertebrado", "Invertebrado"};
    private final boolean[] respuestas = new boolean[NUM_PREGUNTAS];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pareo);  // Cambiado a activity_pareo

        // Inicializar referencias
        for (int i = 0; i < NUM_PREGUNTAS; i++) {
            int spinnerId = getResources().getIdentifier("spinner" + (i + 1), "id", getPackageName());
            int checkId = getResources().getIdentifier("check" + (i + 1), "id", getPackageName());
            spinners[i] = findViewById(spinnerId);
            checks[i] = findViewById(checkId);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinners[i].setAdapter(adapter);

            final int finalI = i;
            spinners[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    if (position > 0) {
                        String seleccion = opciones[position];
                        boolean esCorrecto = seleccion.equals(respuestasCorrectas[finalI]);
                        checks[finalI].setImageResource(esCorrecto ? R.drawable.check : R.drawable.cross);
                        respuestas[finalI] = esCorrecto;
                        checks[finalI].setVisibility(View.VISIBLE);
                    } else {
                        checks[finalI].setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    checks[finalI].setVisibility(View.INVISIBLE);
                }
            });
        }

        Button btnSiguiente = findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(v -> {
            // Verificar que todos los spinners tengan selección válida
            boolean todosSeleccionados = true;
            for (Spinner spinner : spinners) {
                if (spinner.getSelectedItemPosition() == 0) {
                    todosSeleccionados = false;
                    break;
                }
            }

            if (todosSeleccionados) {
                int puntaje = 0;
                for (boolean r : respuestas) {
                    puntaje += r ? 1 : 0;
                }

                Intent intent = new Intent(PareoActivity.this, PreguntasActivity.class);  // Cambiado a PareoActivity
                intent.putExtra("puntajePareo", puntaje);
                startActivity(intent);
            } else {
                Toast.makeText(PareoActivity.this, "Por favor responde todas las preguntas", Toast.LENGTH_SHORT).show();  // Cambiado a PareoActivity
            }
        });
    }
}
