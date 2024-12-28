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

        // Step 1: Enable Edge-to-Edge (fullscreen) UI
        EdgeToEdge.enable(this);

        // Step 2: Set the layout using the XML file
        setContentView(R.layout.activity_main);

        // Step 3: Initialize UI components (input fields, buttons, text views)
        ipAddressInput = findViewById(R.id.ipAddressInput);
        portInput = findViewById(R.id.portInput);
        connectButton = findViewById(R.id.connectButton);
        disconnectButton = findViewById(R.id.disconnectButton);
        statusTextView = findViewById(R.id.statusTextView);
        scanButton = findViewById(R.id.scanButton);

        // Step 4: Handle the "Connect" button click
        connectButton.setOnClickListener(v -> {
            // Step 4.1: Show a "Connecting..." message
            statusTextView.setText("Connecting...");

            // Step 4.2: Get the IP address and port from the input fields
            String IP = String.valueOf(ipAddressInput.getText());
            int port = Integer.parseInt(String.valueOf(portInput.getText()));

            // Step 4.3: Create a new ClientSocket and connect
            clientSocket = new ClientSocket();
            clientSocket.connect(IP, port).thenAccept(success -> {
                // Step 4.4: Update UI based on the connection result
                runOnUiThread(() -> {
                    if (!success) {
                        // Connection failed
                        statusTextView.setText("Failed to connect");
                    } else {
                        // Connection successful
                        connectButton.setEnabled(false); // Disable the connect button
                        disconnectButton.setEnabled(true); // Enable the disconnect button
                        statusTextView.setText("Connected to server"); // Update status text
                        scanButton.setEnabled(true); // Enable the scan button
                    }
                });
            });
        });

        // Step 5: Handle the "Disconnect" button click
        disconnectButton.setOnClickListener(v -> {
            // Step 5.1: Close the connection
            clientSocket.close();
            // Step 5.2: Update the UI
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            scanButton.setEnabled(false);
            statusTextView.setText("Disconnected");
        });

        // Step 6: Handle the "Scan" button click
        scanButton.setOnClickListener(v -> {
            // Step 6.1: Call the method to initiate barcode scanning
            scanCode();
        });
    }

    // Step 7: Method to initiate barcode scanning
    private void scanCode() {
        // Step 7.1: Set up the scanning options
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on"); // Set the prompt message
        options.setBeepEnabled(true); // Enable the beep sound after scanning
        options.setOrientationLocked(true); // Lock the orientation to portrait mode
        options.setCaptureActivity(CaptureAct.class); // Specify the activity to launch for scanning

        // Step 7.2: Launch the barcode scanner activity
        barLauncher.launch(options);
    }

    // Step 8: Define the ActivityResultLauncher to handle the scanning result
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) { // If scanning result is not null
            // Step 8.1: Display the result in an alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Scan Result");

            // Step 8.2: Send the scanned message to the server
            clientSocket.sendMessage(result.getContents());

            // Step 8.3: Show the result in the dialog
            builder.setMessage(result.getContents());

            // Step 8.4: Handle the dialog buttons
            builder.setPositiveButton("Scan Again", (dialog, which) -> {
                // Relaunch the scanner to scan again
                scanCode();
                dialog.dismiss(); // Dismiss the dialog
            });
            builder.setNegativeButton("Close", (dialog, which) -> {
                dialog.dismiss(); // Close the dialog
            });

            // Step 8.5: Show the dialog
            builder.show();
        }
    });
}
