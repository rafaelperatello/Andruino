package com.example.rafael.andruino;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{
    private Pubnub pubnub;

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

        buttonLed1 = (Button) findViewById(R.id.button_Led1);
        buttonLed2 = (Button) findViewById(R.id.button_Led2);

        buttonLed1.setOnClickListener(this);
        buttonLed2.setOnClickListener(this);

        seekBarLed3 = (SeekBar) findViewById(R.id.seekBarLed3);
        seekBarLed3.setOnSeekBarChangeListener(this);

        editButton1 = (EditText) findViewById(R.id.edit_button1);
        editButton2 = (EditText) findViewById(R.id.edit_button2);
        editButton3 = (EditText) findViewById(R.id.edit_button3);

        initPubNub();
    }

    private void initPubNub() {
        try {
            pubnub.subscribe("ANDRUINO", new Callback() {
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

                            onReceive(message.toString());
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

    private void publish(String message) {
        pubnub.publish("ANDRUINO", message, new Callback() {
            public void successCallback(String channel, Object response) {

            }

            public void errorCallback(String channel, PubnubError error) {

            }
        });
    }

    private void onReceive(String jsonString) {
        JSONObject json;

        try {
            json = new JSONObject(jsonString);

            if (json.has("bt1")) {
                Log.d("ANDRUINO", json.getString("bt1"));
            }

            if (json.has("bt2")) {
                Log.d("ANDRUINO", json.getString("bt2"));
            }

            if (json.has("bt3")) {
                Log.d("ANDRUINO", json.getString("bt3"));
            }

            if (json.has("bt4")) {
                Log.d("ANDRUINO", json.getString("bt4"));
            }

        } catch (Exception e) {
            Log.d("ANDRUINO", e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d("ANDRUINO", "" + seekBar.getMax());
    }
}
