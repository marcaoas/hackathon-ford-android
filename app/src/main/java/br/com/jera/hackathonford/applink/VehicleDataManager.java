package br.com.jera.hackathonford.applink;

import android.content.Context;
import android.content.Intent;

import com.ford.syncV4.proxy.rpc.AirbagStatus;
import com.ford.syncV4.proxy.rpc.GPSData;
import com.ford.syncV4.proxy.rpc.GetVehicleDataResponse;
import com.ford.syncV4.proxy.rpc.OnVehicleData;
import com.ford.syncV4.proxy.rpc.enums.VehicleDataEventStatus;

import br.com.jera.hackathonford.model.AirbagSituation;
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
    private String airbag;

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
            if(isAirbagDeployed(airbagStatus)){
                airbag = AirbagSituation.DEPLOYED.name();
            } else {
                airbag = AirbagSituation.NOT_DEPLOYED.name();
            }

            Intent intent = new Intent(Constants.Receivers.COORDINATES_RECEIVED);
            intent.putExtra(Constants.Extras.LAT, String.valueOf(lat));
            intent.putExtra(Constants.Extras.LNG, String.valueOf(lng));
            intent.putExtra(Constants.Extras.SPEED, String.valueOf(lng));
            intent.putExtra(Constants.Extras.AIRBAG_STATUS, airbag);
            context.sendBroadcast(intent);
        }
    }

    private boolean isAirbagDeployed(AirbagStatus airbagStatus) {
        if(airbagStatus==null){
            return false;
        } else if(isDeployed(airbagStatus.getDriverAirbagDeployed())        ||
            isDeployed(airbagStatus.getDriverCurtainAirbagDeployed())       ||
            isDeployed(airbagStatus.getDriverKneeAirbagDeployed())          ||
            isDeployed(airbagStatus.getDriverSideAirbagDeployed())          ||
            isDeployed(airbagStatus.getPassengerAirbagDeployed())           ||
            isDeployed(airbagStatus.getPassengerCurtainAirbagDeployed())    ||
            isDeployed(airbagStatus.getPassengerKneeAirbagDeployed())       ||
            isDeployed(airbagStatus.getPassengerSideAirbagDeployed())){
            return true;
        }
        return false;
    }

    public boolean isDeployed(VehicleDataEventStatus status){
        return status == VehicleDataEventStatus.YES;
    }

    public static void handleVehicleData(Context context, OnVehicleData vehicleData){
        VehicleDataManager vehicleDataManager = new VehicleDataManager(context, vehicleData);
        vehicleDataManager.handleGpsData();
    }

}
