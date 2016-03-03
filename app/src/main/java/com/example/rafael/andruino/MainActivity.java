package com.example.rafael.andruino;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

public class MainActivity extends AppCompatActivity {
    Pubnub pubnub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pubnub = new Pubnub("pub-c-24a86643-ecbc-40b3-ad4f-18f4b24f568e", "sub-c-27383722-e188-11e5-ba64-0619f8945a4f");

        try {
            pubnub.subscribe("ANDRUINO", new Callback() {
                        @Override
                        public void connectCallback(String channel, Object message) {
                            publish("APP on");
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
        pubnub.publish("ANDRUINO", "Hello from the PubNub Java SDK", new Callback() {
            public void successCallback(String channel, Object response) {

            }

            public void errorCallback(String channel, PubnubError error) {

            }
        });
    }
}
