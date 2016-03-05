package com.example.rafael.andruino;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private Pubnub pubnub;

    private boolean isButtonLed1Active = false;
    private boolean isButtonLed2Active = false;

    private CoordinatorLayout coordinator;

    private Button buttonLed1;
    private Button buttonLed2;
    private SeekBar seekBarLed3;

    private EditText editButton1;
    private EditText editButton2;
    private EditText editButton3;

    private final String pubKey = "pub-c-24a86643-ecbc-40b3-ad4f-18f4b24f568e";
    private final String subKey = "sub-c-27383722-e188-11e5-ba64-0619f8945a4f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pubnub = new Pubnub(pubKey, subKey);

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

        subscribe("ANDRUINO");
    }

    private void publish(String key, String value) {
        try {
            JSONObject json = new JSONObject();
            json.put(key, value);

            pubnub.publish("ANDRUINO", json, new Callback() {
                public void successCallback(String channel, Object response) {
                    Snackbar.make(coordinator, "Sucesso ao enviar", Snackbar.LENGTH_SHORT).show();
                }

                public void errorCallback(String channel, PubnubError error) {
                    Snackbar.make(coordinator, "Erro ao enviar", Snackbar.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void subscribe(String channel) {
        try {
            pubnub.subscribe(channel, new Callback() {
                        @Override
                        public void connectCallback(String channel, Object message) {
                        }

                        @Override
                        public void disconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        public void reconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void successCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : " + channel + " : "
                                    + message.getClass() + " : " + message.toString());

                            onReceive(message);
                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            System.out.println("SUBSCRIBE : ERROR on channel " + channel
                                    + " : " + error.toString());
                        }
                    }
            );
        } catch (PubnubException e) {
            System.out.println(e.toString());
        }
    }

    private void onReceive(Object object) {
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
                publish("led1", "0");
                isButtonLed1Active = false;
            } else {
                publish("led1", "255");
                isButtonLed1Active = true;
            }
        } else if (v.getId() == R.id.button_Led2) {
            if (isButtonLed2Active) {
                publish("led2", "0");
                isButtonLed2Active = false;
            } else {
                publish("led2", "255");
                isButtonLed2Active = true;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        publish("led3", "" + seekBar.getProgress());
    }
}
