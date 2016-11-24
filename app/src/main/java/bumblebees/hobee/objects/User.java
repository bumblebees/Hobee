package bumblebees.hobee.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static java.lang.Long.parseLong;

public class User {

    private String userID, loginId;
    private String origin, firstName, lastName, birthday, email, gender, bio;
    private Date created;
    private Rank rank;
    private List<Hobby> hobbies;

    public String toString() {
        return "UserID: " + userID + ", loginId: " + loginId + ", origin" + origin + ", first name: "
                + firstName + ", last name: " + lastName + ", birthday: " + birthday + ", gender: " +
                gender + ", bio " + bio;
    }


    public int getAge() {
        int age = 0;
        try {
            Calendar calendar = new GregorianCalendar();
            Calendar today = new GregorianCalendar();
            int factor = 0; //to correctly calculate age when birthday has not been celebrated this year
            Date birthDate = new SimpleDateFormat("yyyy/MM/dd").parse(birthday);
            Date currentDate = new Date(); //today

            calendar.setTime(birthDate);
            today.setTime(currentDate);

            // check if birthday has been celebrated this year
            if (today.get(Calendar.DAY_OF_YEAR) < calendar.get(Calendar.DAY_OF_YEAR)) {
                factor = -1; //birthday not celebrated
            }
            age = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR) + factor;
        } catch (ParseException e) {
            System.out.println("Given date not in expected format dd/MM/yyyy");
        }
        return age;
    }

    public Date getDateCreated() {
        return created;
    }

    public String getUserID() {
        return userID;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getOrigin() {
        return origin;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getBio() {
        return bio;
    }

    public Date getCreated() {
        return created;
    }

    public Rank getRank() {
        return rank;
    }

    public LocalUser getSimpleUser() {
        return new LocalUser(userID, firstName, lastName);
    }

    public User(String userID, String loginId, String origin, String firstName, String lastName, String birthday, String email, String gender, String bio, Date created, Rank rank, List<Hobby> hobbies) {
        this.userID = userID;
        this.loginId = loginId;
        this.origin = origin;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.email = email;
        this.gender = gender;
        this.bio = bio;
        this.created = created;
        this.rank = rank;
        this.hobbies = hobbies;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setRank(Rank rank) {
        this.rank  = rank;
    }

}

