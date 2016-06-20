package uk.appinvent.lunchfinder.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by mudasar on 20/06/16.
 */


@Table(name = "Users")
public class User extends Model {
    @Column(name = "Name")
    public String name;
    @Column(name = "Email")
    public String email;
    @Column(name = "Phone")
    public String phone;

    public User() {
        super();
    }

    public User(String name, String email, String phone) {
        super();
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
