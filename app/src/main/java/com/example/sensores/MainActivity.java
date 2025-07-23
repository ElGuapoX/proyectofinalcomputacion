package com.example.sensores;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private Button buttonAuthenticate;
    private TextView textViewStatus;
    private RelativeLayout mainLayout;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAuthenticate = findViewById(R.id.buttonAuthenticate);
        textViewStatus = findViewById(R.id.textViewStatus);
        mainLayout = findViewById(R.id.mainLayout);

        executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                textViewStatus.setText("Error: " + errString);
                Toast.makeText(getApplicationContext(),
                        "Error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                textViewStatus.setText("Estado: ¡Autenticación exitosa!");
                Toast.makeText(getApplicationContext(), "¡Autenticación exitosa!", Toast.LENGTH_SHORT).show();

                // Opcional: cambia fondo si deseas un efecto visual
                mainLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_green_light));

                // Ir a menú principal
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                textViewStatus.setText("Estado: Falló la autenticación");
                Toast.makeText(getApplicationContext(), "Falló la autenticación", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación con huella digital")
                .setSubtitle("Toca el sensor para continuar")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK |
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();

        buttonAuthenticate.setOnClickListener(v -> {
            checkBiometricSupportAndAuthenticate();
        });
    }

    private void checkBiometricSupportAndAuthenticate() {
        BiometricManager biometricManager = BiometricManager.from(this);
        String message;

        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK |
                BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                message = "Biometría disponible. Iniciando autenticación...";
                textViewStatus.setText("Estado: " + message);
                biometricPrompt.authenticate(promptInfo);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                message = "Hardware biométrico no disponible.";
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                message = "Dispositivo sin sensor biométrico.";
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                message = "No hay huellas registradas.";
                break;
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                message = "Se necesita actualizar seguridad.";
                break;
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                message = "Biometría no soportada.";
                break;
            default:
                message = "Error desconocido.";
                break;
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
