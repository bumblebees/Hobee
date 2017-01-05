package bumblebees.hobee;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.*;

import bumblebees.hobee.objects.Hobby;
import bumblebees.hobee.objects.Rank;
import bumblebees.hobee.objects.User;
import bumblebees.hobee.utilities.CropSquareTransformation;
import bumblebees.hobee.utilities.EventManager;
import bumblebees.hobee.utilities.SessionManager;
import bumblebees.hobee.utilities.SocketIO;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class RegisterUserActivity extends AppCompatActivity {

    private String userGender;
    private String userBirthday;

    private EditText firstName;
    private EditText lastName;
    private TextView birthday;
    private EditText email;
    private RadioGroup gender;
    private RadioButton genderMale;
    private RadioButton genderFemale;
    private RadioButton selectedGender;
    private CheckBox termsOfServiceCheckBox;
    private TextView termsOfServiceBtn;
    private EditText bio;
    private ImageView userImage;
    private Button submitBtn;
    private Button setBirthdayBtn;
    private Button chooseImageBtn;
    private String source;
    private Bundle userData;
    private User user;
    private Gson gson;
    private TextView toolbarText;

    private SessionManager session;

    private static final int SELECT_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        session = new SessionManager(getApplicationContext());
        gson = new Gson();

        Intent intent = getIntent();
        userData = intent.getBundleExtra("userData");
        source   = intent.getStringExtra("Source");


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
        submitBtn = (Button) findViewById(R.id.submitBtn);
        setBirthdayBtn = (Button) findViewById(R.id.setBirthdayBtn);
        chooseImageBtn = (Button) findViewById(R.id.chooseImageBtn);
        termsOfServiceBtn = (TextView) findViewById(R.id.termsOfServiceBtn);
        termsOfServiceCheckBox = (CheckBox) findViewById(R.id.termsOfServiceCheckBox);
        toolbarText = (TextView) findViewById(R.id.registerUserToolbarText);

        if (userData == null) {
            toolbarText.setText("Edit profile");
            termsOfServiceBtn.setVisibility(View.INVISIBLE);
            termsOfServiceCheckBox.setVisibility(View.INVISIBLE);
            submitBtn.setEnabled(true);
        } else {
            toolbarText.setText("Register");
            submitBtn.setEnabled(false);
            termsOfServiceCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (termsOfServiceCheckBox.isChecked()) {
                        submitBtn.setEnabled(true);
                    } else {
                        submitBtn.setEnabled(false);
                    }
                }

            });
        }

        if (userData == null) {
            User loggedInUser = session.getUser();
            firstName.setText(loggedInUser.getFirstName());
            lastName.setText(loggedInUser.getLastName());
            birthday.setText(loggedInUser.getBirthday());
            termsOfServiceCheckBox.setChecked(true);
            if (loggedInUser.getGender().equals("male")) {
                genderMale.setChecked(true);
            } else {
                genderFemale.setChecked(true);
            }
            email.setText(loggedInUser.getEmail());
            bio.setText(loggedInUser.getBio());
            Picasso.with(this).load(loggedInUser.getPicUrl()).transform(new CropSquareTransformation()).into(userImage);

        } else {
            // Set fields with extracted user data
            firstName.setText(userData.getString("firstName"));
            lastName.setText(userData.getString("lastName"));
            email.setText(userData.getString("email"));
            if (userGender == null) {
                genderMale.setChecked(true);
            } else if (userGender.equals("gender_male")) {
                genderMale.setChecked(true);
            } else if (userGender.equals("gender_female")) {
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
        }


        termsOfServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tOSIntent = new Intent(RegisterUserActivity.this, TermsOfServiceActivity.class);
                startActivity(tOSIntent);
            }
        });

        // submit button does magic?
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    //Updating

                if (userData == null) {

                    user = createUser();
                    if (user.getAge() > 15 && user.getAge() < 97) {
                        SocketIO.getInstance().updateProfile(user, getImageBase64(), RegisterUserActivity.this);
                        session.saveUser(user);
                    }
                    else{

                    }
                } else {
                    //Registering

                    // Set shared preferences

                    // Set user instance
                    user = createUser();
                    if (user.getAge() > 15 && user.getAge() < 96) {
                        session.setPreferences(userData.getString("loginId"), userData.getString("origin"));
                        session.saveDataAndEvents(user, new EventManager());
                        // Save user in database
                        SocketIO.getInstance().register(user, getImageBase64(), RegisterUserActivity.this);
                    }
                    if(user.getAge() < 15)
                        Toast.makeText(getApplicationContext(), "You are too young to use the application", Toast.LENGTH_SHORT).show();
                    if(user.getAge() > 96)
                        Toast.makeText(getApplicationContext(), "Please enter a valid age", Toast.LENGTH_SHORT).show();

                }

            }
        });

        setBirthdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogfragment = new DatePickerDialogFragment();
                dialogfragment.show(getFragmentManager(), "dialog");

            }
        });

        /**
         *  In-built image chooser, should not crash with different API versions
         */
        chooseImageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    writeStoragePermission();
                }
                else {
                    Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(gallery, SELECT_IMAGE);
                }
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

    private User createUser(){

        selectedGender = (RadioButton) findViewById(gender.getCheckedRadioButtonId());
        if (userData == null) { //updating profile
            User currentUser = session.getUser();
            return new User(session.getUserID(), session.getId(), session.getOrigin(), firstName.getText().toString(), lastName.getText().toString(),
                    birthday.getText().toString(), email.getText().toString(),
                    selectedGender.getText().toString(),
                    bio.getText().toString(), currentUser.getDateCreated(),
                    currentUser.getRank(), currentUser.getHobbies());
        } else { //registering user
            UUID uuid = UUID.randomUUID();
            Calendar cal = Calendar.getInstance();
            String createdTimestamp = String.valueOf(cal.getTimeInMillis()/1000L);
            return new User(uuid.toString(), userData.getString("loginId"), userData.getString("origin"), firstName.getText().toString(), lastName.getText().toString(),
                    birthday.getText().toString(), email.getText().toString(),
                    selectedGender.getText().toString(),
                    bio.getText().toString(), createdTimestamp,
                    new Rank(), new ArrayList<Hobby>());
        }
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

            int mnth = month +1;
            TextView birthday = (TextView) getActivity().findViewById(R.id.birthday);

            DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            try {
                Date date = formatter.parse(year + "/" + mnth + "/" + day);
                formatter = new SimpleDateFormat("yyyy/MM/dd");
                birthday.setText(formatter.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }


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

    private String getPath(Uri uri){
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
    private String getImageBase64(){
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) userImage.getDrawable());
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageInByte = stream.toByteArray();

        return Base64.encodeToString(imageInByte, Base64.NO_WRAP);

    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @TargetApi(Build.VERSION_CODES.M)
    private void writeStoragePermission() {
        int hasWriteStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, SELECT_IMAGE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(gallery, SELECT_IMAGE);
                } else {
                    // Permission Denied
                    Toast.makeText(RegisterUserActivity.this, "WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}