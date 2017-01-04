package bumblebees.hobee.utilities;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import bumblebees.hobee.R;
import bumblebees.hobee.UserProfileActivity;
import bumblebees.hobee.objects.Event;
import bumblebees.hobee.objects.User;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class UserRankAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<User> userList = new ArrayList<>();
    private Gson gson = new Gson();
    private String[][] ranks;
    private int repMultiplier = 150;
    private Boolean isHost = false;
    private SessionManager session;
    private String userID;


    public UserRankAdapter(Context context, ArrayList<String> userStringList, Event event) {
        //If the user that is evaluating is the host
        ArrayList<String> userStringList1 = userStringList;
        this.context = context;
        session = new SessionManager(context);
        userID = session.getUserID();
        this.isHost = event.getEvent_details().isUserHost(userID);


        for (String str : userStringList) {
            User user = gson.fromJson(str, User.class);
            //If the user to be added to the list is not the local user and he is not the host
            if (!user.getUserID().equals(userID) && !((user.getUserID().equals(event.getEvent_details().getHost_id()))))
                userList.add(user);

            //If the user being added to the ..list is the host
            if (user.getUserID().equals(event.getEvent_details().getHost_id())) {
                User userTemp = null;
                if (userList.size() > 0) {
                    userTemp = userList.get(0);
                    userList.set(0, user);
                    userList.add(userTemp);
                } else {
                    userList.add(user);
                }

            }

            ranks = new String[userList.size()][3];
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

        ImageView userImage = (ImageView) row.findViewById(R.id.userImage);
        if (userList.get(i) != null)
            Picasso.with(context).load(userList.get(i).getPicUrl()).transform(new CropSquareTransformation()).into(userImage);

        final TextView numberView = (TextView) row.findViewById(R.id.numberView);

        final CheckBox noShow = (CheckBox) row.findViewById(R.id.noShow);

        if (!isHost) {
            noShow.setVisibility(View.GONE);
            noShow.setEnabled(false);
        }

        TextView userName = (TextView) row.findViewById(R.id.userName);
        TextView textHost = (TextView) row.findViewById(R.id.textHost);
        textHost.setVisibility(View.INVISIBLE);
        final SeekBar seekBar = (SeekBar) row.findViewById(R.id.seekBar);

        if (i == 0) {
            //row.setBackgroundColor(0xff0000ff);
            textHost.setVisibility(View.VISIBLE);
        }

        userName.setText(userList.get(i).getFirstName() + " " + userList.get(i).getLastName());
        numberView.setText("0");


        noShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (noShow.isChecked()) {
                    seekBar.setEnabled(false);
                    ranks[i][1] = "0";
                    ranks[i][2] = "true";
                    numberView.setText("0");
                } else {
                    seekBar.setEnabled(true);
                    ranks[i][1] = "0";
                    ranks[i][2] = "false";
                }
            }
        });
        //Default values for rank if user decides to not rank current view
        ranks[i][0] = userList.get(i).getUserID();
        ranks[i][1] = "0";
        ranks[i][2] = "false";

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                numberView.setText(String.valueOf(progresValue - 3));
                ranks[i][1] = String.valueOf(repMultiplier * (progresValue - 3));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ////TODO Implement onClickListeners so that you can see the user's profile_img

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open user profile_img
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("User", gson.toJson(userList.get(i)));
                context.startActivity(intent);
            }
        });

        if (userList.get(i).getUserID().equals(userID)) {
            seekBar.setEnabled(false);
            noShow.setEnabled(false);
        }

        return row;

    }

    public String[][] getRanks() {
        return ranks;
    }
}

