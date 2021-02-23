package com.example.tm;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;

public class MainActivity extends AppCompatActivity {

    private Button start;
    private TextView output;
    private OkHttpClient client;

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            //webSocket.send("TM");
            //webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            if (text.contains("challenge-message")) {

                output("Receives : " + text);

                Random random = new Random();
                int randomNumber = random.nextInt(2048-0) + 0;
                String challenge = "-1";
                String response = "0";

                try {
                    JSONObject mainObject = new JSONObject(text);
                    challenge = mainObject.getString("challenge");

                    try {
                        String secret = "oqJV@kVfrzmQ";
                        String bs = "736645";

                        String message = randomNumber+challenge+bs;

                        //Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
                        //SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
                        //sha256_HMAC.init(secret_key);

                        //response = Base64.encodeToString(sha256_HMAC.doFinal(message.getBytes()),0);
                        response = "";
                    }
                    catch (Exception e){
                        // exception handler
                    }
                }
                catch (JSONException e) {
                    // exception handler
                }

                webSocket.send("{\"action\": \"response-message\", \"challenge\": \""+challenge+"\", \"response\": \""+response+"\", \"challenge2\": \""+randomNumber+"\"}");
                output("Sends response to first challenge and new challenge");
            }

            else if (text.contains("response2-message")) {

                output("Receives : " + text);

                String challenge = "-1";
                String challenge2 = "-1";
                String response2 = "0";
                String rst = "0";

                String expectedresponse2 = "0";

                try {
                    JSONObject mainObject = new JSONObject(text);
                    challenge = mainObject.getString("challenge");
                    challenge2 = mainObject.getString("challenge2");
                    response2 = mainObject.getString("response2");

                    try {
                        String secret = "oqJV@kVfrzmQ";
                        String tm = "454636";

                        String message = challenge+challenge2+tm;

                        //Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
                        //SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
                        //sha256_HMAC.init(secret_key);

                        //expectedresponse2 = Base64.encodeToString(sha256_HMAC.doFinal(message.getBytes()),0);
                        //expectedresponse2 = expectedresponse2.substring(0,expectedresponse2.length()-1);
                        expectedresponse2 = "";

                        output("Received:"+response2+":");
                        output("Expected:"+expectedresponse2+":");

                        if (expectedresponse2.equals(response2)){
                            rst = "1";
                        }
                        else{
                            rst = "0";
                        }

                        if (rst.equals("1")){
                            output("BS to TM Authentication OK");
                        }
                        else{
                            output("BS to TM Authentication Fail");
                        }
                    }
                    catch (Exception e){
                        // exception handler
                    }
                }
                catch (JSONException e) {
                    // exception handler
                }

                webSocket.send("{\"action\": \"rst\", \"value\": \""+rst+"\"}");
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            output("Closing : " + code + " / " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("Error : " + t.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start);
        output = (TextView) findViewById(R.id.output);
        client = new OkHttpClient();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
    }

    private void start() {
        //Request request = new Request.Builder().url("ws://echo.websocket.org").build();
        //Request request = new Request.Builder().url("ws://192.168.0.131:8765/").build();
        //Request request = new Request.Builder().url("ws://179.212.199.12:8765/").build();
        Request request = new Request.Builder().url("ws://localhost:8765/").build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        WebSocket ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                output.setText(output.getText().toString() + "\n\n" + txt);
            }
        });
    }
}