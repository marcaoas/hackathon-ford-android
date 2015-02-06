package br.com.jera.hackathonford.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import br.com.jera.hackathonford.model.AirbagSituation;
import br.com.jera.hackathonford.model.DrivingEvent;
import br.com.jera.hackathonford.model.DrivingEventType;
import br.com.jera.hackathonford.utils.Constants;
import br.com.jera.hackathonford.utils.Logger;

/**
 * Created by marco on 06/02/15.
 */
public class DrivingEventReceiver extends BroadcastReceiver {
    private boolean lastAirbagDeployed;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Double lat = intent.getDoubleExtra(Constants.Extras.LAT, 0d);
            Double lng = intent.getDoubleExtra(Constants.Extras.LNG, 0d);
            Double speed = intent.getDoubleExtra(Constants.Extras.SPEED, 0d);
            String airbagSituation = intent.getStringExtra(Constants.Extras.AIRBAG_STATUS);
            if (!lat.equals(0d) && !lng.equals(0d)) {
                DrivingEvent event = new DrivingEvent();
                event.lat = lat;
                event.lng = lng;
                event.speed = speed;
                if(AirbagSituation.DEPLOYED.name().equals(airbagSituation) && isLastAirbagDeployed()){
                    event.airbagStatus = AirbagSituation.DEPLOYED.name();
                    event.eventType = DrivingEventType.ACCIDENT.name();
                } else if(event.isOverSpeed() && !isLastSpeedOverLimit()){
                    event.eventType = DrivingEventType.OVERSPEED.name();
                } else {
                    event.eventType = DrivingEventType.LOCATION.name();
                }
                event.datetime = Calendar.getInstance().getTime();
                event.save();
            }

            String location = String.valueOf(lat) + "  --- " + String.valueOf(lng);
            Logger.d(location);
        }
    }

    public boolean isLastAirbagDeployed() {
        DrivingEvent lastDrivingEvent = DrivingEvent.getLast();
        if(lastDrivingEvent!=null && AirbagSituation.DEPLOYED.name().equals(lastDrivingEvent.airbagStatus)){
            return true;
        }
        return false;
    }

    public boolean isLastSpeedOverLimit() {
        DrivingEvent lastDrivingEvent = DrivingEvent.getLast();
        if(lastDrivingEvent!=null){
            return lastDrivingEvent.isOverSpeed();
        }
        return false;
    }
}
