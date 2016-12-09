package bumblebees.hobee.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import bumblebees.hobee.utilities.MQTTService;

public class MQTTBootReceiver extends BroadcastReceiver {
    public MQTTBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent mqttServiceIntent = new Intent(context, MQTTService.class);
        context.startService(mqttServiceIntent);
    }
}
