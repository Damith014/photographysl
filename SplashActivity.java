package com.example.echonlabs.photographysrilanka;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.echonlabs.photographysrilanka.Constant.Constant;
import com.example.echonlabs.photographysrilanka.Service.ServiceSuspention;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 5000;

    Constant constant=new Constant();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                if (checkInternetConenction()) {
                    checkServiceSusention();
                }else{
                    Intent intent = new Intent(getApplicationContext(), OfflineActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
    /*
        *
        * Check service suspantion
        * */
    private void checkServiceSusention() {
        ServiceSuspention serviceSuspention=new ServiceSuspention() {
            @Override
            public void displayResult(JSONObject jsonObject) {
                displayRe(jsonObject);
            }

            @Override
            public void displayError() {

                displayErr();
            }
        };
        serviceSuspention.execute();

    }
    /*
        *
        * Display Result
        *
        * */
    private void displayRe(JSONObject jsonObject) {

        try {
            if (jsonObject.getInt("status") == constant.ERR_BAD_REQUEST) {
                Toast.makeText(getApplicationContext(), "BAD REQUEST", Toast.LENGTH_SHORT).show();

            } else if (jsonObject.getInt("status") == constant.ERR_SERVER_ERROR) {
                Toast.makeText(getApplicationContext(), "SERVER ERROR", Toast.LENGTH_SHORT).show();

            } else if (jsonObject.getInt("status") == constant.ERR_NOT_FOUND) {
                Toast.makeText(getApplicationContext(), "NOT FOUND", Toast.LENGTH_SHORT).show();

            } else if (jsonObject.getInt("status") == constant.ERR_ALREADY_EXISTS) {
                Toast.makeText(getApplicationContext(), "ALREADY EXISTS", Toast.LENGTH_SHORT).show();

            } else if (jsonObject.getInt("status") == constant.ERR_INCORRECT_ENDPOINT) {
                Toast.makeText(getApplicationContext(), "INCORRECT ENDPOINT", Toast.LENGTH_SHORT).show();

            } else if (jsonObject.getInt("status") == constant.ERR_NOT_AUTHENTICATED) {
                Toast.makeText(getApplicationContext(), "NOT AUTHENTICATED", Toast.LENGTH_SHORT).show();

            } else if (jsonObject.getInt("status") == constant.SUCCESS) {

                JSONObject data=jsonObject.getJSONObject("data");
                if(data.getBoolean("alive")){

                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                    finish();
                }else{
                    Intent intent = new Intent(getApplicationContext(), ServiceSuspentionActivity.class);
                    startActivity(intent);
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*
    *
    * Display Request error
    *
    * */
    private void displayErr() {
        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();

    }


    /*
    *
    * Check Internet Connections
    *
    * */
    private boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec
                = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() ==
                android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            // Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();

            return true;
        } else if (
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() ==
                                android.net.NetworkInfo.State.DISCONNECTED) {

            return false;
        }
        return false;
    }
}
