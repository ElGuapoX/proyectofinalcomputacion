package com.example.sensores;

import android.graphics.Color;
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
    // hola amigo
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
                textViewStatus.setText("Error de autenticación: " + errString + " (Código: " + errorCode + ")");
                Toast.makeText(getApplicationContext(),
                                "Error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                textViewStatus.setText("Estado: ¡Autenticación exitosa!");
                Toast.makeText(getApplicationContext(),
                                "¡Autenticación exitosa!", Toast.LENGTH_SHORT)
                        .show();

                // Cambia el color de fondo a morado
                mainLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.my_custom_purple));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                textViewStatus.setText("Estado: Autenticación fallida. Intenta de nuevo.");
                Toast.makeText(getApplicationContext(), "Autenticación fallida",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación con huella digital")
                .setSubtitle("Toca el sensor de huella digital para continuar")
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
            // ¡ATENCIÓN! La línea que causaba el error se ha corregido aquí:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE: // Usar el nombre completo de la constante
                message = "Hardware biométrico no disponible.";
                textViewStatus.setText("Estado: " + message);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                message = "No hay hardware biométrico disponible.";
                textViewStatus.setText("Estado: " + message);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                message = "No hay biometría registrada. Por favor, registra una en la configuración del dispositivo.";
                textViewStatus.setText("Estado: " + message);
                break;
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                message = "Actualización de seguridad requerida para biometría.";
                textViewStatus.setText("Estado: " + message);
                break;
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                message = "Tipo de autenticación biométrica no compatible.";
                textViewStatus.setText("Estado: " + message);
                break;
            default:
                message = "Error desconocido al verificar biometría.";
                textViewStatus.setText("Estado: " + message);
                break;
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}