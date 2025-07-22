package com.example.sensores;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    Button btnPrueba, btnSensorMovimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnPrueba = findViewById(R.id.btnPrueba);
        btnSensorMovimiento = findViewById(R.id.btnSensorMovimiento);

        // Acción al presionar el botón "Prueba"
        btnPrueba.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, PareoActivity.class); // Clase destino corregida
            startActivity(intent);
        });

        // Acción al presionar el botón "Sensor Movimiento"
        btnSensorMovimiento.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, SensorMovimientoActivity.class);
            startActivity(intent);
        });
    }
}
