package bumblebees.hobee.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;

import bumblebees.hobee.EventViewActivity;
import bumblebees.hobee.HomeActivity;
import bumblebees.hobee.R;
import bumblebees.hobee.objects.CancelledEvent;
import bumblebees.hobee.objects.Event;

/**
 * Class to create and send notifications notifications.
 */
public class Notification {
    private SharedPreferences preferences;
    private Context context;
    private NotificationCompat.Builder notificationBuilder;
    private Gson g = new Gson();
    private boolean notificationsActive;


    public Notification(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
        notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.bee)
                .setAutoCancel(true);

        notificationsActive = preferences.getBoolean("notification_general", false);
        if(notificationsActive) {
            //set light, sound and vibration if they have been enabled in the preferences
            if (preferences.getBoolean("notification_light", true)) {
                notificationBuilder.setLights(0xffffff00, 2000, 2000);
            }
            if (preferences.getBoolean("notification_sound", false)) {
                notificationBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            }
            if (preferences.getBoolean("notification_vibration", true)) {
                notificationBuilder.setVibrate(new long[]{2000, 2000});
            }
        }
    }

    /**
     * Send notification that a new event that matches the user's preferences has appeared.
     * @param event - event that is going to be sent
     */
    public void sendNewEvent(Event event){
        //check if the notification should be sent or not
        if(!event.isFull() && notificationsActive){

            notificationBuilder.setContentTitle("New event: "+event.getEvent_details().getEvent_name());
            notificationBuilder.setContentText(event.getEvent_details().getDescription());

            sendEventNotification(notificationBuilder, event);

        }
        else{
            Log.d("event", "notification not sent");
        }
    }

    /**
     * Sends a notification that opens an EventViewActivity.
     * @param notificationBuilder - notification to be sent
     * @param event - notification opened when the notification is selected
     */

    private void sendEventNotification(NotificationCompat.Builder notificationBuilder, Event event){
        Intent eventIntent = new Intent(context, EventViewActivity.class);
        eventIntent.putExtra("event", g.toJson(event));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(EventViewActivity.class);
        stackBuilder.addNextIntent(eventIntent);

        PendingIntent eventPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder.setContentIntent(eventPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(event.hashCode(), notificationBuilder.build());
    }

    /**
     * Send a notification that opens the HomeActivity.
     * @param notificationBuilder - notification to be sent
     */
    private void sendGeneralNotification(NotificationCompat.Builder notificationBuilder){
        Intent eventIntent = new Intent(context, HomeActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(eventIntent);

        PendingIntent eventPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder.setContentIntent(eventPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, notificationBuilder.build());

    }

    /**
     * Inform the host how many users are waiting to join the events they are hosting.
     * Go to the homepage to see the events with pending people.
     */
    public void sendPendingUsersTotal(){
        if(preferences.getBoolean("notification_pending", false)) {
            SessionManager session = new SessionManager(context);
            int totalPending = 0;
            for(Event event:session.getHostedEvents()) {
               totalPending += event.getEvent_details().getUsers_pending().size();
            }
            if (totalPending > 0) {
                notificationBuilder.setContentTitle("Pending users");
                notificationBuilder.setContentText(totalPending + " people want to join events you are hosting.");
                sendGeneralNotification(notificationBuilder);
            }
        }
    }

    /**
     * Send a notification that a user's request to join an event has been accepted by the host.
     * @param event
     */
    public void sendUserEventAccepted(Event event){
        if(notificationsActive) {
            notificationBuilder.setContentTitle("Event accepted: " + event.getEvent_details().getEvent_name());
            notificationBuilder.setContentText(event.getEvent_details().getDescription());

            sendEventNotification(notificationBuilder, event);
        }
    }

    /**
     * Send a notification that a user's request to join an event has been rejected by the host.
     * @param event
     */
    public void sendUserEventRejected(Event event){
        if(notificationsActive) {
            notificationBuilder.setContentTitle("Event rejected: " + event.getEvent_details().getEvent_name());
            notificationBuilder.setContentText(event.getEvent_details().getDescription());

            sendEventNotification(notificationBuilder, event);
        }
    }

    /**
     * Send a notification that an event has been cancelled.
     * @param event
     */
    public void sendCancelledEvent(CancelledEvent event, String type){
        if(notificationsActive) {
            notificationBuilder.setContentTitle(type.toUpperCase() + " Event Cancelled");
            notificationBuilder.setContentText("Reason: " + event.getReason());

            sendGeneralNotification(notificationBuilder);
        }
    }
}
