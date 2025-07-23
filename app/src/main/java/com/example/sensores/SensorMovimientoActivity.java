package com.example.sensores;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.hardware.*;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SensorMovimientoActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor acelerometro;
    private Sensor sensorSignificativo;
    private TriggerEventListener triggerListener;

    private TextView textoMovimiento;
    private ImageView pelotita;
    private Button btnVolver;

    private long ultimaAnimacion = 0;
    private static final long INTERVALO_REBOTE = 30_000; // 30 segundos en milisegundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sensor_movimiento);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textoMovimiento = findViewById(R.id.txtMovimiento);
        pelotita = findViewById(R.id.imgPelotita);
        btnVolver = findViewById(R.id.btnVolverMenu);

        // Botón para volver al menú
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SensorMovimientoActivity.this, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // 1. Registrar el acelerómetro primero
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (acelerometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Acelerómetro no disponible", Toast.LENGTH_SHORT).show();
        }

        // 2. Registrar SIGNIFICANT_MOTION si está disponible
        sensorSignificativo = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        if (sensorSignificativo != null) {
            triggerListener = new TriggerEventListener() {
                @Override
                public void onTrigger(TriggerEvent event) {
                    mostrarMovimiento("¡Movimiento significativo detectado!");
                    intentarAnimar();
                    // volver a registrar, ya que solo lanza una vez
                    sensorManager.requestTriggerSensor(this, sensorSignificativo);
                }
            };
            sensorManager.requestTriggerSensor(triggerListener, sensorSignificativo);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (acelerometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (acelerometro != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        String direccion;

        if (Math.abs(x) > Math.abs(y) && Math.abs(x) > Math.abs(z)) {
            direccion = x > 0 ? "Izquierda" : "Derecha";
        } else if (Math.abs(y) > Math.abs(z)) {
            direccion = y > 0 ? "Abajo" : "Arriba";
        } else {
            direccion = z > 0 ? "Hacia ti" : "Lejos de ti";
        }

        mostrarMovimiento("Movimiento: " + direccion);

        // Solo animar si es un movimiento básico vertical
        if (direccion.equals("Arriba") || direccion.equals("Abajo")) {
            intentarAnimar();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // No necesario
    }

    private void mostrarMovimiento(String mensaje) {
        textoMovimiento.setText(mensaje);
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    private void intentarAnimar() {
        long tiempoActual = System.currentTimeMillis();
        if (tiempoActual - ultimaAnimacion >= INTERVALO_REBOTE) {
            ultimaAnimacion = tiempoActual;
            animarPelotita();
        }
    }

    private void animarPelotita() {
        ObjectAnimator rebote = ObjectAnimator.ofFloat(pelotita, "translationY", 0f, -300f, 0f);
        rebote.setDuration(500);
        rebote.start();
    }
}