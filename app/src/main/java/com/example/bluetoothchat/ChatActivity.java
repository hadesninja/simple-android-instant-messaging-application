package com.example.bluetoothchat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private static final String APP_NAME = "BTChatApp";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // SPP

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private ConnectedThread connectedThread;

    private TextView chatBox;
    private EditText messageInput;
    private Button sendBtn;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatBox = findViewById(R.id.chatBox);
        messageInput = findViewById(R.id.messageInput);
        sendBtn = findViewById(R.id.sendBtn);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String deviceAddress = getIntent().getStringExtra("DEVICE_ADDRESS");

        // Start server to accept connections
        new ServerThread().start();

        // If user selected a device, try to connect as client too
        if (deviceAddress != null) {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            new ClientThread(device).start();
        }

        sendBtn.setOnClickListener(v -> {
            String msg = messageInput.getText().toString().trim();
            if (!msg.isEmpty() && connectedThread != null) {
                connectedThread.write((msg).getBytes());
                chatBox.append("\nMe: " + msg);
                messageInput.setText("");
            }
        });
    }

    private class ServerThread extends Thread {
        private BluetoothServerSocket serverSocket;

        public void run() {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
                BluetoothSocket incoming = serverSocket.accept(); // blocks
                if (incoming != null) {
                    manageConnection(incoming);
                    serverSocket.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    private class ClientThread extends Thread {
        private final BluetoothDevice device;

        ClientThread(BluetoothDevice device) {
            this.device = device;
        }

        public void run() {
            BluetoothSocket tmp;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothAdapter.cancelDiscovery();
                tmp.connect();
                manageConnection(tmp);
            } catch (IOException ignored) {
            }
        }
    }

    private synchronized void manageConnection(BluetoothSocket s) {
        if (socket != null) {
            try { socket.close(); } catch (IOException ignored) {}
        }
        socket = s;
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
        handler.post(() -> chatBox.append("\n✅ Connected!"));
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream inStream;
        private final OutputStream outStream;

        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException ignored) {}
            inStream = tmpIn;
            outStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = inStream.read(buffer);
                    if (bytes > 0) {
                        String incoming = new String(buffer, 0, bytes);
                        handler.post(() -> chatBox.append("\nFriend: " + incoming));
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }

        void write(byte[] bytes) {
            try {
                outStream.write(bytes);
            } catch (IOException ignored) {}
        }

        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException ignored) {}
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectedThread != null) {
            connectedThread.cancel();
        }
    }
}
