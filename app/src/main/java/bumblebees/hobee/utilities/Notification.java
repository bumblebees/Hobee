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

import bumblebees.hobee.EventViewActivity;
import bumblebees.hobee.R;
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
     * Checks if the event matches the preferences of the currently logged in user
     * @param event - event to be checked
     * @return true if they match, false otherwise
     */
    public boolean matchesPreferences(Event event){
        //check if the user is already a member of the event or is the host
        if(event.getEvent_details().checkUser(Profile.getInstance().getUser().getSimpleUser()) ||
                event.getEvent_details().getHost_id().equals(Profile.getInstance().getUserID())){
            //user is in the event, does not need a notification
            return false;
        }

        //check if the age is larger than the max age, or smaller than the minimum age
        if(event.getEvent_details().getAge_max()<Profile.getInstance().getAge() || event.getEvent_details().getAge_min() > Profile.getInstance().getAge()){
            return false;
        }

        //check if there are gender restrictions to the event
        if(!event.getEvent_details().getGender().equals("everyone")){
            //check that the gender does not match the user's gender
            if(!event.getEvent_details().getGender().equals(Profile.getInstance().getGender())){
                return false;
            }
        }

        //check if the event is full
        if(event.getEvent_details().getUsers_accepted().size()==event.getEvent_details().getMaximum_people()){
            return false;
        }

        //check the user's day of the week preferences

        int dayOfTheWeek = event.getEvent_details().getDayOfTheWeek();
        switch(dayOfTheWeek){
            case Calendar.MONDAY:{
                if(!preferences.getBoolean("notification_monday", false)){
                    return false;
                }
                break;
            }
            case Calendar.TUESDAY:{
                if(!preferences.getBoolean("notification_tuesday", false)){
                    return false;
                }
                break;
            }
            case Calendar.WEDNESDAY:{
                if(!preferences.getBoolean("notification_wednesday", false)){
                    return false;
                }
                break;
            }
            case Calendar.THURSDAY:{
                if(!preferences.getBoolean("notification_thursday", false)){
                    return false;
                }
                break;
            }
            case Calendar.FRIDAY:{
                if(!preferences.getBoolean("notification_friday", false)){
                    return false;
                }
                break;
            }
            case Calendar.SATURDAY:{
                if(!preferences.getBoolean("notification_saturday", false)){
                    return false;
                }
                break;
            }
            case Calendar.SUNDAY:{
                if(!preferences.getBoolean("notification_sunday", false)){
                    return false;
                }
                break;
            }
        }
        //none of the preferences are contradicted, the user can receive the notification
        return true;
    }

    /**
     * Inform the host that there are users pending to join an event.
     * @param event - event that has been joined
     */
    public void sendPendingUsers(Event event){
        notificationBuilder.setContentTitle("Pending users: "+event.getEvent_details().getEvent_name());
        notificationBuilder.setContentText(event.getEvent_details().getUsers_pending().size()+" people want to join the event.");
        sendEventNotification(notificationBuilder, event);
    }

    /**
     * Send a notification that a user's request to join an event has been accepted by the host;
     * @param user - user to be notified
     * @param event
     */
    public void sendNewUserAccepted(User user, Event event){



    }


}
