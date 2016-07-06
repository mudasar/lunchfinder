package uk.appinvent.lunchfinder.data;


import android.database.Cursor;

/**
 * Created by mudasar on 20/06/16.
 */



public class User  {


    private long Id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String uid;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getId() {
        return Id;
    }

    public User() {

    }

    public User(long id, String name, String email, String phone, String password, String uid) {
        Id = id;

        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.uid = uid;
    }

    public static User fromCursor(Cursor c) {
        long id = c.getLong(c.getColumnIndexOrThrow(LunchContract.UserEntry._ID));
        String name = c.getString(c.getColumnIndexOrThrow(LunchContract.UserEntry.COLUMN_NAME));
        String email = c.getString(c.getColumnIndexOrThrow(LunchContract.UserEntry.COLUMN_EMAIL));
        String phone = c.getString(c.getColumnIndexOrThrow(LunchContract.UserEntry.COLUMN_PHONE));
        String password = c.getString(c.getColumnIndexOrThrow(LunchContract.UserEntry.COLUMN_PASSWORD));
        String uid = c.getString(c.getColumnIndexOrThrow(LunchContract.UserEntry.COLUMN_UID));
        return new User(id, name, email, phone, password, uid);
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String generatePasword(){
        return  "pass.word";
    }
}
