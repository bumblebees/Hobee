package bumblebees.hobee;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import bumblebees.hobee.utilities.SessionManager;
import com.facebook.login.LoginManager;


import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bumblebees.hobee.utilities.MQTT;
import bumblebees.hobee.utilities.MQTTMessageReceiver;
import io.socket.client.Ack;

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
    ArrayList<NavItem> navItems = new ArrayList<>();


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


        // Add options to the menu (empty strings can be replaced with some additional info)
        navItems.add(new NavItem("Profile", R.drawable.profile));
        navItems.add(new NavItem("Logout", R.drawable.logout));

        // DrawerLayout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Display user name in menu
        user = (TextView) findViewById(R.id.firstName_lastName);
        user.setText(session.getfirstName() + " " + session.getLastName());

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
                Intent profile = new Intent(HomeActivity.this, UserProfileActivity.class);
                startActivity(profile);
                break;
            case 1:
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

        eventList = (LinearLayout) findViewById(R.id.listParticipatingEvents);
        ArrayList<NavItem> navItems;

        public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
            this.context = context;
            this.navItems = navItems;
        }

        @Override
        public int getCount() {
            return navItems.size();
        }
                JSONArray array = (JSONArray) objects[0];
                for (int i=0; i<array.length(); i++){

                    try {
                        JSONObject data = array.getJSONObject(i);
                        JSONObject event = data.getJSONObject("event");
                        final Button btn = new Button(HomeActivity.this);
                        btn.setText(event.getString("name"));
                        btn.setTag(data.getString("eventID"));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                eventList.addView(btn);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                SocketIO.disconnect();
            }
        });

         */
    }


    public void subscribeTopics(){
            //we pretend these are the hobbies for now
            String[] hobbies = {"basketball", "football", "fishing", "cooking"};
            for (int i = 0; i < hobbies.length; i++) {
                String topic = "hobby/event/" + hobbies[i] + "/#";
                MQTT.getInstance().subscribe(topic, 1, new MQTTMessageReceiver() {
                    @Override
                    public void onMessageReceive(MqttMessage message) {
                        Log.d("mqtt", "received message");
                        try {
                            JSONObject data = new JSONObject(message.toString());
                            JSONObject event = data.getJSONObject("event");
                            final Button btn = new Button(HomeActivity.this);
                            btn.setText(data.getString("category")+": "+ event.getString("name"));
                            btn.setTag(data.getString("eventID"));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    eventList.addView(btn);
                                }
                            });
                        } catch (JSONException e) {
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




}
