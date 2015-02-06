package br.com.jera.hackathonford.applink;

import android.content.Context;
import android.content.Intent;

import com.ford.syncV4.proxy.rpc.GPSData;
import com.ford.syncV4.proxy.rpc.GetVehicleDataResponse;

import br.com.jera.hackathonford.utils.Constants;

/**
 * Created by marco on 05/02/15.
 */
public class VehicleDataManager {

    private Context context;
    private GetVehicleDataResponse data;
    public VehicleDataManager(Context context, GetVehicleDataResponse vehicleData){
        this.context = context;
        this.data = vehicleData;
    }

    public void handleGpsData(){
        if(data!=null){
            GPSData coordinates = data.getGps();
            if(coordinates!=null){
                Double lat = coordinates.getLatitudeDegrees();
                Double lng = coordinates.getLongitudeDegrees();
                Intent intent = new Intent(Constants.Receivers.COORDINATES_RECEIVED);
                intent.putExtra(Constants.Extras.LAT, String.valueOf(lat));
                intent.putExtra(Constants.Extras.LNG, String.valueOf(lng));
                context.sendBroadcast(intent);
            }
        }
    }

    public static void handleVehicleData(Context context, GetVehicleDataResponse vehicleData){
        VehicleDataManager vehicleDataManager = new VehicleDataManager(context, vehicleData);
        vehicleDataManager.handleGpsData();
    }

}
