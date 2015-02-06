package br.com.jera.hackathonford.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by marco on 06/02/15.
 */
@Table(name="DrivingEvents")
public class DrivingEvent extends Model {

    @Column(name = "eventType")
    public String eventType; //DrivingEventType enum

    @Column(name = "lat")
    public Double lat;

    @Column(name = "lng")
    public Double lng;

    @Column(name = "datetime")
    public Date datetime;

    @Column(name = "airbagStatus")
    public String airbagStatus;
}
