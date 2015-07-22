package com.storm.connectiontest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity {

    ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = ((activeNetwork != null) && activeNetwork.isConnectedOrConnecting());
        final TextView connStatus = (TextView) findViewById(R.id.net_state_textview);
        if (isConnected) {
            connStatus.setText(getString(R.string.net_state) + "Connected");
        } else {
            connStatus.setText(getString(R.string.net_state) + "Not connected");
        }
        registerReceiver(connChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(wifiStateReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));

    }

    private final BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            TextView wifiLevel = (TextView) findViewById(R.id.wifi_signal_level_view);
            wifiLevel.setText(getString(R.string.wifi_signal_level)
                    + String.valueOf(intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0)) +
                    "Speed: " + ((WifiManager) getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getLinkSpeed() + WifiInfo.LINK_SPEED_UNITS);
        }
    };

    private final BroadcastReceiver connChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TextView connStatus = (TextView) findViewById(R.id.net_state_textview);
            if (intent.hasExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY)) {

                if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, true)) {
                    connStatus.setText(getString(R.string.net_state) + "Not connected");
                }

            } else {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo.isConnected()) {
                    connStatus.setText(getString(R.string.net_state) + "Connected over " + networkInfo.getTypeName());
                }
            }

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
       unregisterReceiver(connChangeReceiver);
        unregisterReceiver(wifiStateReceiver);
        super.onDestroy();
    }

    public void onButtonClick(View view) {
        Intent intent =  new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }
}
