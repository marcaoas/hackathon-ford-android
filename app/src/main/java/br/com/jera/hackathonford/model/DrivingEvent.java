package br.com.jera.hackathonford.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Date;

/**
 * Created by marco on 06/02/15.
 */
@Table(name="DrivingEvents")
public class DrivingEvent extends Model {

    public static final Double SPEED_LIMIT = 100d;

    @Column(name = "eventType")
    public String eventType; //DrivingEventType enum

    @Column(name = "lat")
    public Double lat;

    @Column(name = "lng")
    public Double lng;

    @Column(name = "speed")
    public Double speed;

    @Column(name = "datetime")
    public Date datetime;

    @Column(name = "airbagStatus")
    public String airbagStatus;

    public static DrivingEvent getLast(){
        return new Select().from(DrivingEvent.class).orderBy("id desc").executeSingle();
    }

    public boolean isOverSpeed() {
        if(speed!=null){
            return speed > (1.1 * SPEED_LIMIT);
        }
        return false;
    }
}
