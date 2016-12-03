package bumblebees.hobee.utilities;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import bumblebees.hobee.R;
import bumblebees.hobee.objects.User;


public class userRankAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> userStringList;
    private ArrayList<User> userList = new ArrayList<>();
    private Gson gson = new Gson();

    public userRankAdapter(Context context, ArrayList<String> userStringList) {
        this.userStringList = userStringList;
        this.context = context;
        for(String str:userStringList){
            Log.d("user",str);
            userList.add(gson.fromJson(str,User.class));
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.user_rank_item,viewGroup,false);

        ////TODO implement images
        ImageView userImage = (ImageView) row.findViewById(R.id.userImage);


        TextView userName = (TextView) row.findViewById(R.id.userName);

        final TextView numberView = (TextView) row.findViewById(R.id.numberView);

        SeekBar seekBar = (SeekBar) row.findViewById(R.id.seekBar);

        userName.setText(userList.get(i).getFirstName() + " " + userList.get(i).getLastName());
        numberView.setText("0");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                    numberView.setText(String.valueOf( progresValue - 3));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override

                public void onStopTrackingTouch(SeekBar seekBar) {   }
            });

        ////TODO Implement onClickListeners so that you can see the user's profile

       /** userImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open user profile
                Intent intent = new Intent();
            }
        });;
        */

        return row;
    }
}

