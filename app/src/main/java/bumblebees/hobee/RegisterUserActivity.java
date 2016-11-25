package bumblebees.hobee;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.*;

import bumblebees.hobee.objects.Hobby;
import bumblebees.hobee.objects.Rank;
import bumblebees.hobee.objects.User;
import bumblebees.hobee.utilities.SessionManager;
import bumblebees.hobee.utilities.SocketIO;
import bumblebees.hobee.utilities.Profile;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class RegisterUserActivity extends AppCompatActivity {

    String userGender;
    String userBirthday;

    EditText firstName;
    EditText lastName;
    TextView birthday;
    EditText email;
    RadioGroup gender;
    RadioButton genderMale;
    RadioButton genderFemale;
    RadioButton selectedGender;
    EditText bio;
    ImageView userImage;
    ImageButton submitBtn;
    Button setBirthdayBtn;
    Button chooseImageBtn;
    Bundle userData;
    User user;
    Gson gson;

    SessionManager session;

    private static final int SELECT_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        session = new SessionManager(getApplicationContext());
        gson = new Gson();

        Intent intent = getIntent();
        userData = intent.getBundleExtra("userData");


        try {
            userGender = userData.getString("gender");
        } catch (NullPointerException e) {
            userGender = null;
        }
        try {
            userBirthday = userData.getString("birthday");
        } catch (NullPointerException e) {
            userBirthday = null;
        }


        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        birthday = (TextView) findViewById(R.id.birthday);
        email = (EditText) findViewById(R.id.email);
        gender = (RadioGroup) findViewById(R.id.gender);
        genderMale = (RadioButton) findViewById(R.id.radioMale);
        genderFemale = (RadioButton) findViewById(R.id.radioFemale);
        bio = (EditText) findViewById(R.id.info);
        userImage = (ImageView) findViewById(R.id.userImage);
        submitBtn = (ImageButton) findViewById(R.id.submitBtn);
        setBirthdayBtn = (Button) findViewById(R.id.setBirthdayBtn);
        chooseImageBtn = (Button) findViewById(R.id.chooseImageBtn);


        // Set fields with extracted user data
        firstName.setText(userData.getString("firstName"));
        lastName.setText(userData.getString("lastName"));
        email.setText(userData.getString("email"));
        if (userGender == null){
            // nothing
        } else if (userGender.equals("male")) {
            genderMale.setChecked(true);
        } else if (userGender.equals("female")) {
            genderFemale.setChecked(true);
        }
        if (userBirthday != null) {
            try {
                Date date = new SimpleDateFormat("MM/dd/yyyy").parse(userBirthday);
                String formattedDate = new SimpleDateFormat("yyyy/MM/dd").format(date);
                birthday.setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (userData.getString("origin").equals("facebook")) {
            Picasso.with(this)
                    .load("https://graph.facebook.com/" + userData.getString("loginId") + "/picture?width=200&height=200")
                    .into(userImage);
        }


        // submit button does magic?
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject userJSON = createJSON();
                // Set shared preferences
                session.setPreferences(userData.getString("loginId"), userData.getString("origin"));
                // Set user instance
                user = createUser();
                Profile.getInstance().setUser(user);
                // Save user in database
                SocketIO.getInstance().register(user, user.getLoginId(), getImageBase64(), RegisterUserActivity.this);
//                // Save user image on server
//                SocketIO.getInstance().sendImage(userData.getString("loginId"), getImageBase64());

            }
        });

        setBirthdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogfragment = new DatePickerDialogFragment();
                dialogfragment.show(getFragmentManager(), "dialog");

            }
        });

        chooseImageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, SELECT_IMAGE);
            }
        });
    }


    /**
     * Collects data from the screen (modified or not) and put it into JSON object
     * @return JSON object
     */
    public JSONObject createJSON() {
        JSONObject object = new JSONObject();
        selectedGender = (RadioButton) findViewById(gender.getCheckedRadioButtonId());
        try {
            object.put("loginId", userData.getString("loginId"));
            object.put("origin", userData.get("origin"));
            object.put("firstName", firstName.getText().toString());
            object.put("lastName", lastName.getText().toString());
            object.put("birthday", birthday.getText().toString());
            object.put("email", email.getText().toString());
            object.put("gender", selectedGender.getText().toString());
            object.put("bio", bio.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public User createUser(){
        UUID uuid = UUID.randomUUID();
        User user = new User(uuid.toString(), userData.getString("loginId"), userData.getString("origin"), firstName.getText().toString(), lastName.getText().toString(),
                birthday.getText().toString(), email.getText().toString(), selectedGender.getText().toString(), bio.getText().toString(), Calendar.getInstance().getTime(),
                new Rank(), new ArrayList<Hobby>());
        return user;
    }


    /**
     * Class to create a dialog for picking a date. The AlertDialog theme is deprecated but it still looks much more
     * comfortable than the default one.
     */
    public static class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year = 1990;
            int month = 0;
            int day = 1;

            return new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            TextView birthday = (TextView) getActivity().findViewById(R.id.birthday);
            birthday.setText(new StringBuilder().append(year).append("/").append(month + 1).append("/").append(day));
        }
    }

    /**
     *  onActivityResult and getPath are here to handle the users choice of the picture from the phone gallery
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == SELECT_IMAGE){
            Uri selectedImage = data.getData();
            String path = getPath(selectedImage);

            Bitmap bitmapImage = BitmapFactory.decodeFile(path);
            userImage.setImageBitmap(bitmapImage);

        }
    }

    public String getPath(Uri uri){
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        int columnIndex = 0;
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            return cursor.getString(columnIndex);
        }
        return null;
    }

    /**
     * This method takes the provided or selected picture and prepares it to be sent through socket
     * @return String base64
     */
    public String getImageBase64(){
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) userImage.getDrawable());
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageInByte = stream.toByteArray();

        return Base64.encodeToString(imageInByte, Base64.NO_WRAP);

    }


}