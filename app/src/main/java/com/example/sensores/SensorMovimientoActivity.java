package com.example.sensores;

import android.animation.AnimatorSet;
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

import androidx.appcompat.app.AppCompatActivity;

public class SensorMovimientoActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor acelerometro;
    private Sensor sensorSignificativo;
    private TriggerEventListener triggerListener;

    private TextView textoMovimiento;
    private ImageView pelotita;
    private Button btnVolver;

    private static final float UMBRAL_QUIETO = 0.5f; // Umbral para considerar el dispositivo quieto
    private static final long TIEMPO_QUIETO = 1000; // 1 segundo para confirmar estado quieto
    private long ultimoMovimiento = 0;
    private boolean estaQuieto = true;
    private float[] ultimosValores = new float[3];

    // Nuevo factor para controlar la sensibilidad del movimiento de la pelotita
    private static final float MOVIMIENTO_PELOTITA_FACTOR = 50.0f; // Aumentado de 10.0f a 50.0f (o incluso más)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_movimiento);

        textoMovimiento = findViewById(R.id.txtMovimiento);
        pelotita = findViewById(R.id.imgPelotita);
        btnVolver = findViewById(R.id.btnVolverMenu);

        // Botón para volver al menú
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(SensorMovimientoActivity.this, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Registrar el acelerómetro
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (acelerometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_GAME);
        } else {
            Toast.makeText(this, "Acelerómetro no disponible", Toast.LENGTH_SHORT).show();
        }

        // Registrar sensor de movimiento significativo
        sensorSignificativo = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        if (sensorSignificativo != null) {
            triggerListener = new TriggerEventListener() {
                @Override
                public void onTrigger(TriggerEvent event) {
                    mostrarMovimiento("¡Movimiento significativo detectado!");
                    // Ajusta este valor si quieres un rebote específico del sensor de movimiento significativo
                    animarPelotita(0, -100f); // Animación de rebote más pronunciada
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
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (acelerometro != null) {
            sensorManager.unregisterListener(this);
        }
        if (triggerListener != null && sensorSignificativo != null) {
            sensorManager.cancelTriggerSensor(triggerListener, sensorSignificativo);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Calcular diferencia con los últimos valores
            float deltaX = Math.abs(x - ultimosValores[0]);
            float deltaY = Math.abs(y - ultimosValores[1]);
            float deltaZ = Math.abs(z - ultimosValores[2]);

            // Guardar valores actuales para la próxima comparación
            System.arraycopy(event.values, 0, ultimosValores, 0, 3);

            // Calcular fuerza del movimiento
            float fuerza = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

            // Verificar si el dispositivo está quieto
            long tiempoActual = System.currentTimeMillis();
            if (fuerza > UMBRAL_QUIETO) {
                ultimoMovimiento = tiempoActual;
                if (estaQuieto) {
                    estaQuieto = false;
                    mostrarEstado("Dispositivo en movimiento");
                }

                // Mostrar dirección y animar
                String direccion = determinarDireccion(x, y, z);
                mostrarMovimiento("Movimiento: " + direccion);
                // Multiplicamos por MOVIMIENTO_PELOTITA_FACTOR para un desplazamiento más grande
                animarPelotita(-x * MOVIMIENTO_PELOTITA_FACTOR, y * MOVIMIENTO_PELOTITA_FACTOR);
            } else if (!estaQuieto && (tiempoActual - ultimoMovimiento) > TIEMPO_QUIETO) {
                estaQuieto = true;
                mostrarEstado("Dispositivo quieto");
                // Centrar la pelotita cuando está quieto
                pelotita.post(() -> {
                    pelotita.setTranslationX(0);
                    pelotita.setTranslationY(0);
                });
            }
        }
    }

    private String determinarDireccion(float x, float y, float z) {
        if (Math.abs(x) > Math.abs(y) && Math.abs(x) > Math.abs(z)) {
            return x > 0 ? "Izquierda" : "Derecha";
        } else if (Math.abs(y) > Math.abs(z)) {
            return y > 0 ? "Abajo" : "Arriba";
        } else {
            return z > 0 ? "Hacia ti" : "Lejos de ti";
        }
    }

    private void mostrarEstado(String mensaje) {
        runOnUiThread(() -> {
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        });
    }

    private void mostrarMovimiento(String mensaje) {
        runOnUiThread(() -> {
            textoMovimiento.setText(mensaje);
        });
    }

    private void animarPelotita(final float deltaX, final float deltaY) {
        if (estaQuieto) return; // No animar si está quieto

        runOnUiThread(() -> {
            // Cancelar animaciones previas
            pelotita.clearAnimation();

            // Animación de movimiento
            ObjectAnimator animX = ObjectAnimator.ofFloat(pelotita, "translationX",
                    pelotita.getTranslationX(), deltaX);
            ObjectAnimator animY = ObjectAnimator.ofFloat(pelotita, "translationY",
                    pelotita.getTranslationY(), deltaY);

            // Configurar animaciones
            AnimatorSet moveSet = new AnimatorSet();
            moveSet.playTogether(animX, animY);
            moveSet.setDuration(200); // Puedes ajustar la duración para que la animación sea más rápida o lenta
            moveSet.start();
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No necesario
    }
}