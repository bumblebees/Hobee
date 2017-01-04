package bumblebees.hobee.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import bumblebees.hobee.utilities.Notification;

public class PendingNotificationReceiver extends BroadcastReceiver {
    public PendingNotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        new Notification(context).sendPendingUsersTotal();
    }
}
