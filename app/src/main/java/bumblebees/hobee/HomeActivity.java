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
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import bumblebees.hobee.objects.Deal;
import bumblebees.hobee.objects.Event;
import bumblebees.hobee.utilities.*;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;


import java.util.ArrayList;

import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    SessionManager session;
    RelativeLayout drawerPane;
    DrawerLayout drawerLayout;
    DrawerListAdapter adapter;
    TextView user;
    ImageView avatar;
    ArrayList<NavItem> navItems = new ArrayList<>();
    ListView drawerList;
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar appToolbar;
    Gson gson;
    SharedPreferences preferences;
    MQTTService service;

    View dealContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent mqttServiceIntent = new Intent(this, MQTTService.class);
        startService(mqttServiceIntent);

        setContentView(R.layout.activity_home);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        dealContainer = findViewById(R.id.dealContainer);

        session = new SessionManager(getApplicationContext());
        gson = new Gson();
        Profile.getInstance().setUser(session.getUser());
        appToolbar = (Toolbar) findViewById(R.id.homeToolbar);
        setSupportActionBar(appToolbar);

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



        session = new SessionManager(getApplicationContext());

        // Add options to the menu (empty strings can be replaced with some additional info)
        navItems.add(new NavItem("Profile", R.drawable.profile));
        navItems.add(new NavItem("Settings", R.drawable.settings));
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

        for(final Event event:Profile.getInstance().getHistoryEvents()){
            if(event.checkUnranked(Profile.getInstance().getUser())){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You have unranked events, Would you like to rank them now?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SocketIO.getInstance().sendUserIDArrayAndOpenRankActivity(gson.toJson(event), event.getEvent_details().getUsers_unrankedJson(), getApplicationContext());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuBtnAddEvent:
                Intent newEventIntent = new Intent(HomeActivity.this, NewEventActivity.class);
                HomeActivity.this.startActivity(newEventIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
}
