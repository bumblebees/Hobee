package bumblebees.hobee.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import bumblebees.hobee.utilities.MQTTService;
import bumblebees.hobee.utilities.SessionManager;

public class MQTTBootReceiver extends BroadcastReceiver {
    public MQTTBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //check if the user is logged in first before doing anything
        SessionManager session = new SessionManager(context);
        if (session.isLoggedIn()) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean notifications = preferences.getBoolean("notification_general", false);
            //check if notifications are turned on
            //if not, do not start the service on boot and wait for the activity to start it itself
            if (notifications) {
                Intent mqttServiceIntent = new Intent(context, MQTTService.class);
                context.startService(mqttServiceIntent);
            }
        }
    }
}
