package bumblebees.hobee;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import bumblebees.hobee.objects.Event;
import bumblebees.hobee.utilities.*;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.Calendar;

import com.squareup.picasso.Picasso;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class HomeActivity extends AppCompatActivity {

    SessionManager session;
    Button btnNewEvent;
    Button btnGetEvents;
    TextView textView;
    LinearLayout eventList;
    RelativeLayout drawerPane;
    DrawerLayout drawerLayout;
    DrawerListAdapter adapter;
    TextView user;
    ImageView avatar;
    ArrayList<NavItem> navItems = new ArrayList<>();
    ListView drawerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        MQTT.getInstance().connect(this.getApplicationContext());

        session = new SessionManager(getApplicationContext());

        // for testing
        textView = (TextView) findViewById(R.id.textView);
        textView.setText(session.getId() + " " + session.getOrigin());

        btnNewEvent = (Button) findViewById(R.id.btnCreateEvent);
        btnNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newEventIntent = new Intent(HomeActivity.this, NewEventActivity.class);
                HomeActivity.this.startActivity(newEventIntent);
            }
        });


        btnGetEvents = (Button) findViewById(R.id.btnGetEvents);
        btnGetEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subscribeTopics();
            }
        });

        eventList = (LinearLayout) findViewById(R.id.listParticipatingEvents);


        // Add options to the menu (empty strings can be replaced with some additional info)
        navItems.add(new NavItem("Profile", R.drawable.profile));
        navItems.add(new NavItem("Settings", 0));
        navItems.add(new NavItem("Logout", R.drawable.logout));

        // DrawerLayout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Display user name in menu
        user = (TextView) findViewById(R.id.firstName_lastName);
        user.setText(Profile.getInstance().getFirstName() + " " + Profile.getInstance().getLastName());

        // Display avatar
        avatar = (ImageView) findViewById(R.id.avatar);
        Picasso.with(this).load(Profile.getInstance().getPicUrl()).into(avatar);

        // Populate the Navigation Drawer with options
        drawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        drawerList = (ListView) findViewById(R.id.navList);
        adapter = new DrawerListAdapter(this, navItems);
        drawerList.setAdapter(adapter);

        // Drawer Item click listeners
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

    }

    /**
     *  When back button is pressed this checks if menu is opened and closes if true.
     *  Otherwise instead of going back one step it exits the app.
     */
    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
        }
        else{

            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            System.exit(0);
                        }
                    }).create().show();
        }
    }


    /**
     *  Do something when one of the options is pressed (counting from up)
     */
    private void selectItemFromDrawer(int position) {
        switch(position) {
            case 0:
                drawerLayout.closeDrawers();
                Intent profile = new Intent(HomeActivity.this, LocalUsersProfileActivity.class);
                startActivity(profile);
                break;
            case 1:
                drawerLayout.closeDrawers();
                Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case 2:
                if (session.getOrigin().equals("facebook")){
                    // if facebook user
                    LoginManager.getInstance().logOut();
                }
                // clear user from preferences
                session.logoutUser();
                // go back to login screen
                drawerLayout.closeDrawers();
                Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
                HomeActivity.this.startActivity(logoutIntent);
                break;
            default:
        }
    }


    /**
     *  Inner class to create custom menu list options
     */
    class NavItem {
        String mTitle;
        int mIcon;

        public NavItem(String title, int icon) {
            mTitle = title;
            mIcon = icon;
        }
    }

    /**
     *  ListView binder
     */

    class DrawerListAdapter extends BaseAdapter {

        Context context;
        ArrayList<NavItem> navItems;

        public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
            this.context = context;
            this.navItems = navItems;
        }

        @Override
        public int getCount() {
            return navItems.size();
        }

        @Override
        public Object getItem(int position) {
            return navItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.drawer_item, null);
            }
            else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(R.id.title);
            ImageView iconView = (ImageView) view.findViewById(R.id.icon);
            titleView.setText( navItems.get(position).mTitle );
            iconView.setImageResource(navItems.get(position).mIcon);

            return view;
        }
    }

    public void subscribeTopics(){
        //String topic = "hobby/event/football/51d5446d-a27b-44bb-a6eb-fccb70176914";
        //TODO: get the hobbies from the user preferences
        //we pretend these are the hobbies for now
        String[] hobbies = {"basketball", "football", "fishing", "cooking"};
        for (int i = 0; i < hobbies.length; i++) {
            String topic = "hobby/event/" + hobbies[i] + "/#";
            MQTT.getInstance().subscribe(topic, 1, new MQTTMessageReceiver() {
                @Override
                public void onMessageReceive(MqttMessage message) {
                    Log.d("mqtt", "received message");
                    try {
                        final Gson g = new Gson();
                        final Event event = g.fromJson(message.toString(), Event.class);
                        sendNotification(event);
                        final Button btn = new Button(HomeActivity.this);
                        btn.setText(event.getType() + ": " + event.getEvent_details().getEvent_name());

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    Intent viewEventIntent = new Intent(HomeActivity.this, EventViewActivity.class);
                                    viewEventIntent.putExtra("event", g.toJson(event));
                                    HomeActivity.this.startActivity(viewEventIntent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                eventList.addView(btn);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onResume(){

        //TODO: refresh event list somehow
        super.onResume();
    }

    /**
     * Send notification for an event.
     * @param event - event that is going to be sent
     */
    public void sendNotification(Event event){
        //check if the notification should be sent or not
        if(matchesPreferences(event)){
            Gson g = new Gson();
            //send notification
            NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.bee)
                    .setContentTitle("New event: "+event.getEvent_details().getEvent_name())
                    .setContentText(event.getEvent_details().getDescription())
                    .setAutoCancel(true);

            //set light, sound and vibration if they have been enabled in the preferences
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            if(preferences.getBoolean("notification_light", true)){
                notificationBuilder.setLights(0xffffff00, 2000, 2000);
            }
            if(preferences.getBoolean("notification_sound", false)){
                notificationBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            }
            if(preferences.getBoolean("notification_vibration", true)){
                notificationBuilder.setVibrate(new long[]{2000, 2000});
            }

            Intent eventIntent = new Intent(this, EventViewActivity.class);
            eventIntent.putExtra("event", g.toJson(event));

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(EventViewActivity.class);
            stackBuilder.addNextIntent(eventIntent);

            PendingIntent eventPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
            notificationBuilder.setContentIntent(eventPendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(Integer.parseInt(event.getTimestamp()), notificationBuilder.build());

        }
        else{
            Log.d("event", "notification not sent");
        }
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
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



}
