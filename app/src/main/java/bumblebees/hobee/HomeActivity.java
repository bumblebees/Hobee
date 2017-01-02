package bumblebees.hobee;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import bumblebees.hobee.fragments.FragmentAdapter;
import bumblebees.hobee.hobbycategories.HobbiesChoiceActivity;
import bumblebees.hobee.hobbycategories.HobbyCategoryListActivity;
import bumblebees.hobee.objects.Deal;
import bumblebees.hobee.objects.Event;
import bumblebees.hobee.objects.PublicUser;
import bumblebees.hobee.objects.User;
import bumblebees.hobee.utilities.*;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

    SessionManager session;
    RelativeLayout drawerPane;
    DrawerLayout drawerLayout;
    DrawerListAdapter adapter;
    TextView user;
    ImageView avatar, hamburger;
    ArrayList<NavItem> navItems = new ArrayList<>();
    ListView drawerList;
    TabLayout tabLayout;
    ViewPager viewPager;
    Gson gson;
    SharedPreferences preferences;
    MQTTService service;
    User loggedInUser;

    View dealContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent mqttServiceIntent = new Intent(this, MQTTService.class);
        startService(mqttServiceIntent);

        setContentView(R.layout.activity_home);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        hamburger = (ImageView) findViewById(R.id.hamburger);

        dealContainer = findViewById(R.id.dealContainer);

        session = new SessionManager(getApplicationContext());
        gson = new Gson();
        loggedInUser = session.getUser();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

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

        // Add options to the menu (empty strings can be replaced with some additional info)
        navItems.add(new NavItem("Profile", R.drawable.profile_img));
        navItems.add(new NavItem("Host event", R.drawable.add_img));
        navItems.add(new NavItem("Settings", R.drawable.settings_img));
        navItems.add(new NavItem("Logout", R.drawable.logout_img));


        // DrawerLayout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Display user name in menu
        user = (TextView) findViewById(R.id.firstName_lastName);
        user.setText(loggedInUser.getFirstName() + " " + loggedInUser.getLastName());

        // Display avatar
        avatar = (ImageView) findViewById(R.id.avatar);
        Picasso.with(this).load(loggedInUser.getPicUrl()).into(avatar);

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

        hamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                else {
                    drawerLayout.closeDrawers();
                }
            }
        });

        Intent intent = new Intent(this, MQTTService.class);
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                MQTTService.MQTTBinder binder = (MQTTService.MQTTBinder) iBinder;
                service = binder.getInstance();

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


        rankUsers();

        //show a snackbar if the user has no location set or no hobbies
        //the user cannot see any events unless both location and hobby are set
        Set<String> emptyLocation = new HashSet<>(); //to prevent null pointer exception
        Set<String> preferencesStringSet = preferences.getStringSet("location_topics", emptyLocation);

        //check if the location preferences have been set
        if (preferencesStringSet.isEmpty()) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No location selected in preferences", Snackbar.LENGTH_INDEFINITE)
                    .setAction("go", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                            startActivity(settingsIntent);
                        }
                    });
            snackbar.show();
        }
        //check if the user has hobbies selected and show a message if there are none
        if(loggedInUser.getHobbyNames().size() == 0){
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No hobby added to profile", Snackbar.LENGTH_INDEFINITE)
                    .setAction("go", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent hobbyIntent = new Intent(HomeActivity.this, HobbiesChoiceActivity.class);
                            startActivity(hobbyIntent);
                        }
                    });
            snackbar.show();
        }

    }


    /**
     * Checks if the user can rank an event and prompts him to do so.
     */

    public void rankUsers(){
        final ArrayList<Event> unRankedEvents = new ArrayList<>();
        final ArrayList<Event> hostedUnrankedEvents = new ArrayList<>();
        SessionManager sessionManager = new SessionManager(this);

        for(Event event:sessionManager.getEventManager().getHistoryHostedEvents()){
            if(event.getEvent_details().getUsers_accepted().size()>1){
                if (!event.checkRanked(loggedInUser)) {
                hostedUnrankedEvents.add(event);
                }
            }
        }
        for(Event event:sessionManager.getEventManager().getHistoryJoinedEvents()){
            if (event.checkHostranked()) {
                if (!event.checkRanked(loggedInUser)) {
                    unRankedEvents.add(event);
                }
            }
        }

        if(!hostedUnrankedEvents.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You have " + hostedUnrankedEvents.size() + " hosted events pending ranking. Would you like to rank them now?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            for(Event event:hostedUnrankedEvents)
                            SocketIO.getInstance().sendUserIDArrayAndOpenRankActivity(gson.toJson(event), event.getEvent_details().getUsers_unrankedJson(), getApplicationContext());
                        }
                    })
                    .setNeutralButton("Later", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        if(!unRankedEvents.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You have " + unRankedEvents.size() + " attended events pending ranking. Would you like to rank them now?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            for(Event event: unRankedEvents)
                            SocketIO.getInstance().sendUserIDArrayAndOpenRankActivity(gson.toJson(event), event.getEvent_details().getUsers_unrankedJson(), getApplicationContext());
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            for(Event event:unRankedEvents){
                                sendEmptyRank(event);
                            }
                        }
                    })
                    .setNeutralButton("Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }


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
                Intent newEventIntent = new Intent(HomeActivity.this, NewEventActivity.class);
                startActivity(newEventIntent);
                break;
            case 2:
                drawerLayout.closeDrawers();
                Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
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


    @Override
    protected void onResume() {
        super.onResume();
        //refresh the user data in case something has changed
        SocketIO.getInstance().updateUserData(loggedInUser.getUserID(), this);
        loggedInUser = session.getUser();
        boolean seeDeals = preferences.getBoolean("deals_preference", false);
        if(seeDeals){
            if(service!=null){
                try {
                    Deal deal = service.getRandomDeal();
                    setDeal(deal);
                    dealContainer.setVisibility(View.VISIBLE);
                }
                catch(NullPointerException e){
                    //hide the deals container, something is not working properly
                    dealContainer.setVisibility(View.GONE);
                }
            }
        }
        else{
            dealContainer.setVisibility(View.GONE);
        }
    }

    public void setDeal(Deal deal){
        TextView dealDescription = (TextView) dealContainer.findViewById(R.id.dealDetails);
        TextView dealName = (TextView) dealContainer.findViewById(R.id.dealName);
        Button btnDeal = (Button) dealContainer.findViewById(R.id.dealGo);
        TextView dealCount = (TextView) dealContainer.findViewById(R.id.dealCount);

        dealName.setText(deal.getName());
        dealDescription.setText(deal.getPrice()+" SEK");
        dealCount.setText(deal.getCount()+"\nleft!");

        btnDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = getResources().getString(R.string.gogodeals_url);
                Intent dealIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(dealIntent);
            }
        });

    }

    public void sendEmptyRank(Event event){
        Boolean hasranked = false;
        JSONObject rankingMessage = new JSONObject();
        JSONArray parent = new JSONArray();
        List<PublicUser> users = event.getEvent_details().getUsers_accepted();

        //Create two dimmentional array with JSON objects
        try {
            for (int j = 0; j < users.size(); j++) {
                JSONArray child = new JSONArray();
                for (int i = 0; i < 3; i++) {
                    if(i==0) child.put(i, users.get(j));
                    if (i == 1){
                        child.put(i, 0);
                    }
                    if(i==2) child.put(i,false);
                }
                parent.put(j, child);
            }


            rankingMessage.put("hasRanked", true);
            rankingMessage.put("userID", loggedInUser.getUserID());
            rankingMessage.put("eventID", event.getEventID());
            rankingMessage.put("hostRep", 0);
            rankingMessage.put("userReps", parent);
        }
        catch (JSONException e){
            Log.d("Json" , e.toString());}
        SocketIO.getInstance().sendRanking(rankingMessage);
    }

}
