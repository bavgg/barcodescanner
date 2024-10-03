package com.jg.barcodescanner;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {
    ClientSocket clientSocket;
    private Button scanButton;
    private EditText ipAddressInput;
    private EditText portInput;
    private Button connectButton;
    private Button disconnectButton;
    private TextView statusTextView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ipAddressInput = findViewById(R.id.ipAddressInput);
        portInput = findViewById(R.id.portInput);
        connectButton = findViewById(R.id.connectButton);
        disconnectButton = findViewById(R.id.disconnectButton);
        statusTextView = findViewById(R.id.statusTextView);
        scanButton = findViewById(R.id.scanButton);

        connectButton.setOnClickListener(v -> {
            statusTextView.setText("Connecting...");

            String IP = String.valueOf(ipAddressInput.getText());
            int port = Integer.parseInt(String.valueOf(portInput.getText()));

            clientSocket = new ClientSocket();
            clientSocket.connect(IP, port).thenAccept(success -> {
                runOnUiThread(() -> { // Ensure UI updates are on the main thread
                    if (!success) {
                        statusTextView.setText("Failed to connect");
                    } else {
                        connectButton.setEnabled(false);
                        disconnectButton.setEnabled(true);
                        statusTextView.setText("Connected to server");
                        scanButton.setEnabled(true);
                    }
                });
            });


        });

        disconnectButton.setOnClickListener(v -> {
            clientSocket.close();
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            scanButton.setEnabled(false);
            statusTextView.setText("Disconnected");
        });

        scanButton.setOnClickListener(v -> {
            scanCode();
        });

    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Scan Result");

            clientSocket.sendMessage(result.getContents());

            builder.setMessage(result.getContents());

            builder.setPositiveButton("Scan Again", (dialog, which) -> {
                // Relaunch the scanner
                scanCode();
                dialog.dismiss();
            });
            builder.setNegativeButton("Close", (dialog, which) -> {
                dialog.dismiss();
            });
            builder.show();
        }
    });

}