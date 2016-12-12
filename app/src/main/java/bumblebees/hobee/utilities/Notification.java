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

import java.util.Calendar;
import java.util.Set;

import bumblebees.hobee.EventViewActivity;
import bumblebees.hobee.HomeActivity;
import bumblebees.hobee.R;
import bumblebees.hobee.objects.CancelledEvent;
import bumblebees.hobee.objects.Event;
import bumblebees.hobee.objects.User;

/**
 * Class to create and send notifications notifications.
 */
public class Notification {
    private SharedPreferences preferences;
    private Context context;
    private NotificationCompat.Builder notificationBuilder;
    private Gson g = new Gson();


    public Notification(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
        notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.bee)
                .setAutoCancel(true);

        //set light, sound and vibration if they have been enabled in the preferences
        if(preferences.getBoolean("notification_light", true)){
            notificationBuilder.setLights(0xffffff00, 2000, 2000);
        }
        if(preferences.getBoolean("notification_sound", false)){
            notificationBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        }
        if(preferences.getBoolean("notification_vibration", true)){
            notificationBuilder.setVibrate(new long[]{2000, 2000});
        }
    }

    /**
     * Send notification that a new event that matches the user's preferences has appeared.
     * @param event - event that is going to be sent
     */
    public void sendNewEvent(Event event){
        //check if the notification should be sent or not
        if(matchesPreferences(event)){

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
     * Checks if the event matches the preferences of the currently logged in user
     * @param event - event to be checked
     * @return true if they match, false otherwise
     */
    public boolean matchesPreferences(Event event){

        //check if the event is full
        if(event.getEvent_details().getUsers_accepted().size()==event.getEvent_details().getMaximum_people()){
            return false;
        }

        //check the user's day of the week preferences
        int dayOfTheWeek = event.getEvent_details().getDayOfTheWeek();
        Set<String> selectedDays = preferences.getStringSet("notification_days", null);
        if(!selectedDays.isEmpty())
            if(!selectedDays.contains(String.valueOf(dayOfTheWeek))){
                return false;
            }
        //none of the notification preferences are contradicted
        return true;
    }

    /**
     * Inform the host that there are users pending to join an event.
     * @param event - event that has been joined
     */
    public void sendPendingUsers(Event event){
        if(preferences.getBoolean("notification_pending", false)) {
            notificationBuilder.setContentTitle("Pending users: " + event.getEvent_details().getEvent_name());
            notificationBuilder.setContentText(event.getEvent_details().getUsers_pending().size() + " people want to join the event.");
            sendEventNotification(notificationBuilder, event);
        }
    }

    public void checkPendingUsers(String eventManagerString){
        EventManager eventManager = g.fromJson(eventManagerString, EventManager.class);
        sendPendingUsersTotal(eventManager);
    }

    /**
     * Inform the host how many users are waiting to join the events they are hosting.
     * Go to the homepage to see the events with pending people.
     */

    private void sendPendingUsersTotal(EventManager eventManager){
        if(preferences.getBoolean("notification_pending", false)) {
            int totalPending = 0;
            for(Event event:eventManager.getHostedEvents()) {
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
        notificationBuilder.setContentTitle("Event accepted: "+event.getEvent_details().getEvent_name());
        notificationBuilder.setContentText(event.getEvent_details().getDescription());

        sendEventNotification(notificationBuilder, event);


    }

    /**
     * Send a notification that a user's request to join an event has been rejected by the host.
     * @param event
     */
    public void sendUserEventRejected(Event event){
        notificationBuilder.setContentTitle("Event rejected: "+event.getEvent_details().getEvent_name());
        notificationBuilder.setContentText(event.getEvent_details().getDescription());

        sendEventNotification(notificationBuilder, event);

    }

    /**
     * Send a notification that an event has been cancelled.
     * @param event
     */
    public void sendCancelledEvent(CancelledEvent event, String type){
        notificationBuilder.setContentTitle("One of your "+ type +" events has been cancelled.");
        notificationBuilder.setContentText("Reason :"+event.getReason());

        sendGeneralNotification(notificationBuilder);
    }


}
