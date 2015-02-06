package br.com.jera.hackathonford.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import br.com.jera.hackathonford.utils.Constants;
import br.com.jera.hackathonford.utils.Logger;

/**
 * Created by marco on 06/02/15.
 */
public class DrivingEventReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null) {
            String lat = intent.getStringExtra(Constants.Extras.LAT);
            String lng = intent.getStringExtra(Constants.Extras.LNG);
            String location = String.valueOf(lat) + "  --- " + String.valueOf(lng);
            Logger.d(location);
        }
    }
}
