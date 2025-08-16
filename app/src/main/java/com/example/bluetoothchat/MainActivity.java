package com.example.bluetoothchat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<String> deviceListAdapter;
    ListView listView;
    Button btnScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        btnScan = findViewById(R.id.btnScan);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(deviceListAdapter);

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Request enabling Bluetooth if off
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBt, 1);
        }

        // Show paired devices initially
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            deviceListAdapter.add("Paired: " + device.getName() + "\n" + device.getAddress());
        }

        // Start discovery on button
        btnScan.setOnClickListener(v -> {
            deviceListAdapter.clear();
            // Re-add paired
            Set<BluetoothDevice> p = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice d : p) {
                deviceListAdapter.add("Paired: " + d.getName() + "\n" + d.getAddress());
            }
            bluetoothAdapter.startDiscovery();
            Toast.makeText(this, "Scanning for devices…", Toast.LENGTH_SHORT).show();
        });

        // Register discovery receiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String item = deviceListAdapter.getItem(position);
            if (item != null && item.contains("\n")) {
                String address = item.substring(item.indexOf("\n") + 1);
                Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
                chatIntent.putExtra("DEVICE_ADDRESS", address);
                startActivity(chatIntent);
            }
        });
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && device.getName() != null) {
                    deviceListAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        } catch (Exception ignored) {}
    }
}
