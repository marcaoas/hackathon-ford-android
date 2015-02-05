package br.com.jera.hackathonford.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Created by Diego on 05/02/2015.
 */

@Table(name = "Users")
public class User extends Model {

    @Column(name = "userName")
    public String userName;


    public User(){
        super();
    }
    public User(String name){
        super();
        this.userName = name;
    }

    public static User getRandom() {
        return new Select().from(User.class).executeSingle();
    }

    public static int getCount(){
        return new Select().from(User.class).execute().size();
    }
}
