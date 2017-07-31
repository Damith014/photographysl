package com.example.echonlabs.photographysrilanka;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class OfflineActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView txtTryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        setActionBar();
        initialized();
        clickEvent();

    }

    /*
    *
    * ToolBar
    *
    *
    * */
    private void setActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    }

    /*
    *
    * Initialized Text view
    *
    * */
    private void initialized() {
        txtTryAgain = (TextView) findViewById(R.id.txtTryAgain);
    }

    /*
    *
    * Click event
    *
    * */
    private void clickEvent() {
        txtTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(checkInternetConenction()){
                   Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                   startActivity(intent);
                   overridePendingTransition(R.anim.right_in, R.anim.left_out);
               }else{

                   Toast.makeText(getApplicationContext(), "You are Offline!", Toast.LENGTH_SHORT).show();
               }
            }
        });
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
