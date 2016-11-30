package bumblebees.hobee;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
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

import bumblebees.hobee.fragments.FragmentAdapter;
import bumblebees.hobee.objects.Event;
import bumblebees.hobee.utilities.*;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
    TabLayout tabLayout;
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        FragmentAdapter tabAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager, true);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        session = new SessionManager(getApplicationContext());

        // Add options to the menu (empty strings can be replaced with some additional info)
        navItems.add(new NavItem("Profile", R.drawable.profile));
        navItems.add(new NavItem("Settings", R.drawable.settings));
        navItems.add(new NavItem("New Event", 0));
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

        subscribeTopics();
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
                Intent profile = new Intent(HomeActivity.this, UserProfileActivity.class);
                startActivity(profile);
                break;
            case 1:
                drawerLayout.closeDrawers();
                Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case 2:
                //TODO: move this somewhere else
                Intent newEventIntent = new Intent(HomeActivity.this, NewEventActivity.class);
                HomeActivity.this.startActivity(newEventIntent);
                break;
            case 3:
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

    /**
     * Subscribe to the MQTT topics and fill in the list of events that the user is participating in.
     */
    public void subscribeTopics(){
        final Gson gson = new Gson();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> emptyLocation = new HashSet<>(); //to prevent null pointer exception
        Set<String> preferencesStringSet = preferences.getStringSet("location_topics", emptyLocation);

        //we pretend these are the hobbies for now
        String[] hobbies = {"basketball", "football", "fishing", "cooking"};

        if(!preferencesStringSet.isEmpty()){
            for(String location:preferencesStringSet){
                for(final String hobby : hobbies){
                    //subscribe to all topics that match the location and the hobby
                    String topic = "geo/"+location+"/event/hobby/"+hobby+"/#";
                    MQTT.getInstance().subscribe(topic, 1, new MQTTMessageReceiver() {
                        @Override
                        public void onMessageReceive(MqttMessage message) {
                            try {
                                Log.d("eventFrag", "got an event");
                                final Event event = gson.fromJson(message.toString(), Event.class);
                                //check if the user's preferences match the event and if the user is not already a member of it
                                if(event.getEvent_details().getHost_id().equals(Profile.getInstance().getUserID())){
                                    //user is the host
                                    Log.d("evFrag", "we are here");
                                    Profile.getInstance().addHostedEvent(event);
                                }
                                else if(event.getEvent_details().getUsers_pending().contains(Profile.getInstance().getUser().getSimpleUser())){
                                    //user is in the pending list
                                    Profile.getInstance().addPendingEvent(event);
                                }
                                else if(event.getEvent_details().getUsers_accepted().contains(Profile.getInstance().getUser().getSimpleUser())){
                                    //user is in the accepted list
                                    if(Profile.getInstance().getPendingEvents().contains(event)){
                                        Profile.getInstance().removePendingEvent(event);
                                        new Notification(HomeActivity.this).sendUserEventAccepted(event);
                                    }
                                    Profile.getInstance().addAcceptedEvent(event);
                                }
                                else if(Profile.getInstance().matchesPreferences(event)) {
                                    //check if user had been pending on the event
                                    if(Profile.getInstance().getPendingEvents().contains(event)){
                                        Profile.getInstance().removePendingEvent(event);
                                        new Notification(HomeActivity.this).sendUserEventRejected(event);
                                    }
                                    Profile.getInstance().addEligibleEvent(hobby, event);
                                    new Notification(HomeActivity.this).sendNewEvent(event);
                                }
                                else{
                                    //drop it
                                    Log.d("eventFrag", "event dropped");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    }

}
