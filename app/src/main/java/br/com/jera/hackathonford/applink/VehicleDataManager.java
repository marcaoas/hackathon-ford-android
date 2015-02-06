package br.com.jera.hackathonford.applink;

import android.content.Context;
import android.content.Intent;

import com.ford.syncV4.proxy.rpc.AirbagStatus;
import com.ford.syncV4.proxy.rpc.GPSData;
import com.ford.syncV4.proxy.rpc.GetVehicleDataResponse;
import com.ford.syncV4.proxy.rpc.OnVehicleData;

import br.com.jera.hackathonford.utils.Constants;

/**
 * Created by marco on 05/02/15.
 */
public class VehicleDataManager {

    private Context context;
    private OnVehicleData data;
    private Double lat;
    private Double lng;
    private Double speed;

    public VehicleDataManager(Context context, OnVehicleData vehicleData){
        this.context = context;
        this.data = vehicleData;
    }

    public void handleGpsData(){
        if(data!=null){
            GPSData coordinates = data.getGps();
            speed = data.getSpeed();
            AirbagStatus airbagStatus = data.getAirbagStatus();
            if(coordinates!=null){
                lat = coordinates.getLatitudeDegrees();
                lng = coordinates.getLongitudeDegrees();
            } else {
                lat = 0d;
                lng = 0d;
            }

            Intent intent = new Intent(Constants.Receivers.COORDINATES_RECEIVED);
            intent.putExtra(Constants.Extras.LAT, String.valueOf(lat));
            intent.putExtra(Constants.Extras.LNG, String.valueOf(lng));
            context.sendBroadcast(intent);
        }
    }

    public static void handleVehicleData(Context context, OnVehicleData vehicleData){
        VehicleDataManager vehicleDataManager = new VehicleDataManager(context, vehicleData);
        vehicleDataManager.handleGpsData();
    }

}
