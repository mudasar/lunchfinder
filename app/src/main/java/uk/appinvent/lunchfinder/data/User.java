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

    public User(long id, String name, String email, String phone) {
        Id = id;

        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public static User fromCursor(Cursor c) {
        long id = c.getLong(c.getColumnIndexOrThrow(LunchContract.UserEntry._ID));
        String name = c.getString(c.getColumnIndexOrThrow(LunchContract.UserEntry.COLUMN_NAME));
        String email = c.getString(c.getColumnIndexOrThrow(LunchContract.UserEntry.COLUMN_EMAIL));
        String phone = c.getString(c.getColumnIndexOrThrow(LunchContract.UserEntry.COLUMN_PHONE));

        return new User(id, name, email, phone);
    }


}
