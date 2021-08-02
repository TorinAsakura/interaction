package com.atls.aeterna.interaction;

import tech.gusavila92.websocketclient.WebSocketClient;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class WebService extends Service {
    private WebSocketClient webSocketClient;
    private static final String TAG = "interaction/websvc";
    private static final String RANDOM_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        createWebSocketClient();
        // webSocketClient.send("hi!");
    }

    String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);

        for(int i = 0; i < len; i++)
            sb.append(RANDOM_CHARS.charAt(rnd.nextInt(RANDOM_CHARS.length())));

        return sb.toString();
    }

    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public final String generateRandomMessage() throws JSONException {
        String rndString = randomString(rnd.nextInt(10000 - 10) + 10);
        String strHash = md5(rndString);

        String jsonString = new JSONObject()
                .put("message", rndString)
                .put("hash", strHash)
                .toString();

        return jsonString;
    }

    // separate handler (UI thread still freezes)
    private Handler handler = new Handler();

    // separate Runnable routine
    private Runnable runnable = new Runnable() {
        int msgCounter = 1;

        @Override
        public void run() {
            for (;;) {
                try {
                    String msg = generateRandomMessage();
                    Log.i(TAG, "sending message #" + msgCounter + ": " + msg);

                    // TODO: check if connection is still active before sending anything
                    webSocketClient.send(msg);

                    msgCounter++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // FIXME! it's not working, messages being sent without any delay whatsoever
                handler.postDelayed(runnable, 10000);
            }
        }
    };

    private void createWebSocketClient() {
        URI uri;

        try {
            // uri = new URI("ws://192.168.1.17:9999/");
            // uri = new URI("ws://10.0.2.2:63001/");
            uri = new URI("ws://client-provider.service.aeterna.team:63001");
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i(TAG, "onOpen");
                handler.post(runnable);
            }

            @Override
            public void onTextReceived(String message) {
                Log.i(TAG, "received message: " + message);
            }

            @Override
            public void onBinaryReceived(byte[] data) {
                Log.i(TAG, "onBinaryReceived");
            }

            @Override
            public void onPingReceived(byte[] data) {
                Log.i(TAG, "onPingReceived");
            }

            @Override
            public void onPongReceived(byte[] data) {
                Log.i(TAG, "onPongReceived");
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                Log.i(TAG, "onCloseReceived");
            }
        };

        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.addHeader("Origin", "http://developer.example.com");
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }
}
