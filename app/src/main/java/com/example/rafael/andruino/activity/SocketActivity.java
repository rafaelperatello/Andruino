package com.example.rafael.andruino.activity;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.rafael.andruino.R;
import com.example.rafael.andruino.util.TCPClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SocketActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, TCPClient.OnMessageReceived, TCPClient.OnConnectionListener {
    private TCPClient client;

    private boolean isConnected;

    private boolean isButtonLed1Active = false;
    private boolean isButtonLed2Active = false;

    private CoordinatorLayout coordinator;

    private Button buttonConnect;
    private Button buttonLed1;
    private Button buttonLed2;
    private SeekBar seekBarLed3;

    private EditText editButton1;
    private EditText editButton2;
    private EditText editButton3;
    private EditText editIp;

    private TextView textInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);

        client = new TCPClient(this, this);
        isConnected = false;

        coordinator = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        buttonLed1 = (Button) findViewById(R.id.button_Led1);
        buttonLed2 = (Button) findViewById(R.id.button_Led2);

        buttonLed1.setOnClickListener(this);
        buttonLed2.setOnClickListener(this);

        seekBarLed3 = (SeekBar) findViewById(R.id.seekBarLed3);
        seekBarLed3.setOnSeekBarChangeListener(this);
        seekBarLed3.setMax(255);

        editButton1 = (EditText) findViewById(R.id.edit_button1);
        editButton2 = (EditText) findViewById(R.id.edit_button2);
        editButton3 = (EditText) findViewById(R.id.edit_button3);

        editIp = (EditText) findViewById(R.id.edit_ip);
        buttonConnect = (Button) findViewById(R.id.button_connect);
        buttonConnect.setOnClickListener(this);

        textInfo = (TextView) findViewById(R.id.text_info);
    }

    @Override
    public void onStop() {
        super.onStop();
        disconnect();
    }

    private void connect() {
        textInfo.setText("Conectando");

        client.setIp(editIp.getText().toString());
        client.startClient();
    }

    private void disconnect() {
        textInfo.setText("Disconectando");

        client.stopClient();
    }

    private void send(String key, int value) {
        try {
            JSONObject json = new JSONObject();
            json.put(key, value);
            client.sendMessage(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onReceive(Object object) {
        Log.d("ANDRUINO", object.toString());

        final Object message = object;

        this.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    if (message instanceof JSONObject) {
                        JSONObject json = (JSONObject) message;
                        processJson(json);

                    } else if (message instanceof String) {
                        String jsonString = (String) message;
                        try {
                            JSONObject json = new JSONObject(jsonString);
                            processJson(json);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (message instanceof JSONArray) {
                        final JSONArray jsonArray = (JSONArray) message;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                processJson(jsonArray.getJSONObject(i));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void processJson(JSONObject json) {
        try {
            if (json.has("bt1")) {
                processButtonStatus(editButton1, json.getString("bt1"));
            }
            if (json.has("bt2")) {
                processButtonStatus(editButton2, json.getString("bt2"));
            }
            if (json.has("bt3")) {
                processButtonStatus(editButton3, json.getString("bt3"));
            }

        } catch (Exception e) {
            Log.d("ANDRUINO", e.getMessage());
        }
    }

    private void processButtonStatus(EditText editText, String status) {
        switch (status) {
            case "0":
                editText.setText("Desligado");
                break;
            case "1":
                editText.setText("Ligado");
                break;
            default:
                editText.setText("Erro");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_Led1) {
            if (isButtonLed1Active) {
                isButtonLed1Active = false;
                send("led1", 0);
            } else {
                isButtonLed1Active = true;
                send("led1", 255);
            }
        } else if (v.getId() == R.id.button_Led2) {
            if (isButtonLed2Active) {
                isButtonLed2Active = false;
                send("led2", 0);
            } else {
                isButtonLed2Active = true;
                send("led2", 255);
            }
        } else if (v.getId() == R.id.button_connect) {
            if (isConnected)
                disconnect();
            else
                connect();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        send("led3", seekBar.getProgress());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void messageReceived(String message) {
        onReceive(message);
    }

    @Override
    public void onConnect() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                textInfo.setText("Conectado");
                buttonConnect.setText("Desconectar");
                isConnected = true;
            }
        });
    }

    @Override
    public void onError() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                textInfo.setText("Erro");
                buttonConnect.setText("Conectar");
                isConnected = false;
            }
        });
    }

    @Override
    public void onDisconnect() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                textInfo.setText("Disconectado");
                buttonConnect.setText("Conectar");
                isConnected = false;
            }
        });
    }
}
