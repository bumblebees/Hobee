package bumblebees.hobee.utilities;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import bumblebees.hobee.R;
import bumblebees.hobee.objects.Event;
import bumblebees.hobee.objects.User;


public class UserRankAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> userStringList;
    private ArrayList<User> userList = new ArrayList<>();
    private Gson gson = new Gson();
    private String[][] ranks;
    private int repMultiplier = 150;
    private Boolean isHost = false;


    public UserRankAdapter(Context context, ArrayList<String> userStringList, Event event) {
        //If the user that is evaluating is the host
        this.userStringList = userStringList;
        this.context = context;
        this.isHost = event.isCurrentUserHost();


        for (String str : userStringList) {
            Log.d("user", str);
            User user = gson.fromJson(str, User.class);

            //If the user to be added to the list is the host, put him first
            if(user.getUserID() == event.getEvent_details().getHost_id()){
                User userTemp = userList.get(0);
                userList.set(0,user);
                user = userTemp;
            }
            userList.add(user);
            ranks = new String[userList.size()+1][3];
        }

    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.user_rank_item, viewGroup, false);

        //So you don't rank yourself
        if (!userList.get(i).getUserID().equals(Profile.getInstance().getUserID())) {


            ////TODO implement images
            ImageView userImage = (ImageView) row.findViewById(R.id.userImage);

            final TextView numberView = (TextView) row.findViewById(R.id.numberView);

            CheckBox noShow = (CheckBox) row.findViewById(R.id.noShow);

            if (!isHost) {
                noShow.setVisibility(View.INVISIBLE);
                noShow.setEnabled(false);
            }

            TextView userName = (TextView) row.findViewById(R.id.userName);
            TextView textHost = (TextView) row.findViewById(R.id.textHost);
            textHost.setVisibility(View.INVISIBLE);
            SeekBar seekBar = (SeekBar) row.findViewById(R.id.seekBar);

            if (i == 0) {
                row.setBackgroundColor(0xff0000ff);
                textHost.setVisibility(View.VISIBLE);
            }

            userName.setText(userList.get(i).getFirstName() + " " + userList.get(i).getLastName());
            numberView.setText("0");

            if (noShow.isChecked()) {
                seekBar.setEnabled(false);
                ranks[i][0] = userList.get(i).getUserID();
                ranks[i][1] = "0";
                ranks[i][2] = "true";
            }

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                    numberView.setText(String.valueOf(progresValue - 3));
                    ranks[i][0] = userList.get(i).getUserID();
                    ranks[i][1] = String.valueOf(repMultiplier * (progresValue - 3));
                    ranks[i][2] = "false";
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override

                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            ////TODO Implement onClickListeners so that you can see the user's profile

            /** userImage.setOnClickListener( new View.OnClickListener() {
            @Override public void onClick(View v) {
            //Open user profile
            Intent intent = new Intent();
            }
            });;
             */

            return row;
        }
            ranks[i][0] = userList.get(i).getUserID();
            ranks[i][1] = "0";
            ranks[i][2] = "false";
            row.setVisibility(View.GONE);
        return row;
    }
    public String[][] getRanks() {
        return ranks;
    }
}

