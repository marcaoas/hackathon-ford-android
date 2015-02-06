package br.com.jera.hackathonford.applink;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.ford.syncV4.exception.SyncException;
import com.ford.syncV4.exception.SyncExceptionCause;
import com.ford.syncV4.proxy.SyncProxyALM;
import com.ford.syncV4.proxy.interfaces.IProxyListenerALM;
import com.ford.syncV4.proxy.rpc.AddCommandResponse;
import com.ford.syncV4.proxy.rpc.AddSubMenuResponse;
import com.ford.syncV4.proxy.rpc.AlertResponse;
import com.ford.syncV4.proxy.rpc.ChangeRegistrationResponse;
import com.ford.syncV4.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.ford.syncV4.proxy.rpc.DeleteCommandResponse;
import com.ford.syncV4.proxy.rpc.DeleteFileResponse;
import com.ford.syncV4.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.ford.syncV4.proxy.rpc.DeleteSubMenuResponse;
import com.ford.syncV4.proxy.rpc.DiagnosticMessageResponse;
import com.ford.syncV4.proxy.rpc.EndAudioPassThruResponse;
import com.ford.syncV4.proxy.rpc.GPSData;
import com.ford.syncV4.proxy.rpc.GenericResponse;
import com.ford.syncV4.proxy.rpc.GetDTCsResponse;
import com.ford.syncV4.proxy.rpc.GetVehicleDataResponse;
import com.ford.syncV4.proxy.rpc.ListFilesResponse;
import com.ford.syncV4.proxy.rpc.OnAudioPassThru;
import com.ford.syncV4.proxy.rpc.OnButtonEvent;
import com.ford.syncV4.proxy.rpc.OnButtonPress;
import com.ford.syncV4.proxy.rpc.OnCommand;
import com.ford.syncV4.proxy.rpc.OnDriverDistraction;
import com.ford.syncV4.proxy.rpc.OnHMIStatus;
import com.ford.syncV4.proxy.rpc.OnHashChange;
import com.ford.syncV4.proxy.rpc.OnKeyboardInput;
import com.ford.syncV4.proxy.rpc.OnLanguageChange;
import com.ford.syncV4.proxy.rpc.OnLockScreenStatus;
import com.ford.syncV4.proxy.rpc.OnPermissionsChange;
import com.ford.syncV4.proxy.rpc.OnSystemRequest;
import com.ford.syncV4.proxy.rpc.OnTBTClientState;
import com.ford.syncV4.proxy.rpc.OnTouchEvent;
import com.ford.syncV4.proxy.rpc.OnVehicleData;
import com.ford.syncV4.proxy.rpc.PerformAudioPassThruResponse;
import com.ford.syncV4.proxy.rpc.PerformInteractionResponse;
import com.ford.syncV4.proxy.rpc.PutFileResponse;
import com.ford.syncV4.proxy.rpc.ReadDIDResponse;
import com.ford.syncV4.proxy.rpc.ResetGlobalPropertiesResponse;
import com.ford.syncV4.proxy.rpc.ScrollableMessageResponse;
import com.ford.syncV4.proxy.rpc.SetAppIconResponse;
import com.ford.syncV4.proxy.rpc.SetDisplayLayoutResponse;
import com.ford.syncV4.proxy.rpc.SetGlobalPropertiesResponse;
import com.ford.syncV4.proxy.rpc.SetMediaClockTimerResponse;
import com.ford.syncV4.proxy.rpc.ShowResponse;
import com.ford.syncV4.proxy.rpc.SliderResponse;
import com.ford.syncV4.proxy.rpc.SpeakResponse;
import com.ford.syncV4.proxy.rpc.SubscribeButtonResponse;
import com.ford.syncV4.proxy.rpc.SubscribeVehicleDataResponse;
import com.ford.syncV4.proxy.rpc.SystemRequestResponse;
import com.ford.syncV4.proxy.rpc.UnsubscribeButtonResponse;
import com.ford.syncV4.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.ford.syncV4.proxy.rpc.enums.ButtonName;
import com.ford.syncV4.proxy.rpc.enums.LockScreenStatus;
import com.ford.syncV4.proxy.rpc.enums.Result;
import com.ford.syncV4.proxy.rpc.enums.SyncDisconnectedReason;
import com.ford.syncV4.proxy.rpc.enums.TextAlignment;
import com.ford.syncV4.util.DebugTool;

import java.util.Vector;

import br.com.jera.hackathonford.HackathonApplication;
import br.com.jera.hackathonford.utils.Constants;
import br.com.jera.hackathonford.utils.Logger;

/**
 * Created by marco on 05/02/15.
 */
public class AppLinkService extends Service implements IProxyListenerALM {
    // variable used to increment correlation ID for every request sent to SYNC
    public int autoIncCorrId = 0;
    // variable to contain the current state of the service
    private static AppLinkService instance = null;
    // variable to access the BluetoothAdapter
    private BluetoothAdapter mBtAdapter;
    // variable to create and call functions of the SyncProxy
    private SyncProxyALM proxy = null;

    // Service shutdown timing constants
    private static final int CONNECTION_TIMEOUT = 60000;
    private static final int STOP_SERVICE_DELAY = 5000;

    /**
     * Runnable that stops this service if there hasn't been a connection to SYNC
     * within a reasonable amount of time since ACL_CONNECT.
     */
    private Runnable mCheckConnectionRunnable = new Runnable() {
        @Override
        public void run() {
            Boolean stopService = true;
            // If the proxy has connected to SYNC, do NOT stop the service
            if (proxy != null && proxy.getIsConnected()) {
                stopService = false;
            }
            if (stopService) {
                mHandler.removeCallbacks(mCheckConnectionRunnable);
                mHandler.removeCallbacks(mStopServiceRunnable);
                stopSelf();
            }
        }
    };

    /**
     * Runnable that stops this service on ACL_DISCONNECT after a short time delay.
     * This is a workaround until some synchronization issues are fixed within the proxy.
     */
    private Runnable mStopServiceRunnable = new Runnable() {
        @Override
        public void run() {
            // As long as the proxy is null or not connected to SYNC, stop the service
            if (proxy == null || !proxy.getIsConnected()) {
                mHandler.removeCallbacks(mCheckConnectionRunnable);
                mHandler.removeCallbacks(mStopServiceRunnable);
                stopSelf();
            }
        }
    };

    private Handler mHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Remove any previous stop service runnables that could be from a recent ACL Disconnect
        Logger.d("Service created");
        mHandler.removeCallbacks(mStopServiceRunnable);


        // Start the proxy when the service starts
        if (intent != null) {
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBtAdapter != null) {
                if (mBtAdapter.isEnabled()) {
                    startProxy();
                }
            }
        }

        // Queue the check connection runnable to stop the service if no connection is made
        mHandler.removeCallbacks(mCheckConnectionRunnable);
        mHandler.postDelayed(mCheckConnectionRunnable, CONNECTION_TIMEOUT);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        disposeSyncProxy();
        LockScreenManager.clearLockScreen();
        Logger.d("Service destroyed");
        instance = null;
        super.onDestroy();
    }

    public static AppLinkService getInstance() {
        return instance;
    }

    public SyncProxyALM getProxy() {
        return proxy;
    }

    /**
     * Queue's a runnable that stops the service after a small delay,
     * unless the proxy manages to reconnects to SYNC.
     */
    public void stopService() {
        mHandler.removeCallbacks(mStopServiceRunnable);
        mHandler.postDelayed(mStopServiceRunnable, STOP_SERVICE_DELAY);
    }

    public void startProxy() {
        if (proxy == null) {
            try {
                proxy = new SyncProxyALM(this, Constants.Ford.APP_NAME, true, Constants.Ford.APP_ID);
            } catch (SyncException e) {
                e.printStackTrace();
                // error creating proxy, returned proxy = null
                if (proxy == null) {
                    stopSelf();
                }
            }
        }
    }

    public void disposeSyncProxy() {
        if (proxy != null) {
            try {
                proxy.dispose();
            } catch (SyncException e) {
                e.printStackTrace();
            }
            proxy = null;
            LockScreenManager.clearLockScreen();
        }
    }

    public void reset() {
        if (proxy != null) {
            try {
                proxy.resetProxy();
            } catch (SyncException e1) {
                e1.printStackTrace();
                //something goes wrong, & the proxy returns as null, stop the service.
                // do not want a running service with a null proxy
                if (proxy == null) {
                    stopSelf();
                }
            }
        } else {
            startProxy();
        }
    }

    public void subButtons() {
        try {
            proxy.subscribeButton(ButtonName.OK, autoIncCorrId++);
            proxy.subscribeButton(ButtonName.SEEKLEFT, autoIncCorrId++);
            proxy.subscribeButton(ButtonName.SEEKRIGHT, autoIncCorrId++);
            proxy.subscribeButton(ButtonName.TUNEUP, autoIncCorrId++);
            proxy.subscribeButton(ButtonName.TUNEDOWN, autoIncCorrId++);
            proxy.subscribeButton(ButtonName.PRESET_1, autoIncCorrId++);
            proxy.subscribeButton(ButtonName.PRESET_2, autoIncCorrId++);
            proxy.subscribeButton(ButtonName.PRESET_3, autoIncCorrId++);
            proxy.subscribeButton(ButtonName.PRESET_4, autoIncCorrId++);
            proxy.subscribeButton(ButtonName.PRESET_5, autoIncCorrId++);
            proxy.subscribeButton(ButtonName.PRESET_6, autoIncCorrId++);
            proxy.subscribeButton(ButtonName.PRESET_7, autoIncCorrId++);
            proxy.subscribeButton(ButtonName.PRESET_8, autoIncCorrId++);
            proxy.subscribeButton(ButtonName.PRESET_9, autoIncCorrId++);
            proxy.subscribeButton(ButtonName.PRESET_0, autoIncCorrId++);
        } catch (SyncException e) {
        }
    }

    @Override
    public void onProxyClosed(String info, Exception e, SyncDisconnectedReason reason) {
        LockScreenManager.clearLockScreen();

        if ((((SyncException) e).getSyncExceptionCause() != SyncExceptionCause.SYNC_PROXY_CYCLED)) {
            if (((SyncException) e).getSyncExceptionCause() != SyncExceptionCause.BLUETOOTH_DISABLED) {
                Logger.d("reset proxy in onproxy closed");
                reset();
            }
        }
    }

    @Override
    public void onOnHMIStatus(OnHMIStatus notification) {
        switch (notification.getSystemContext()) {
            case SYSCTXT_MAIN:
                break;
            case SYSCTXT_VRSESSION:
                break;
            case SYSCTXT_MENU:
                break;
            default:
                return;
        }

        switch (notification.getAudioStreamingState()) {
            case AUDIBLE:
                // play audio if applicable
                break;
            case NOT_AUDIBLE:
                // pause/stop/mute audio if applicable
                break;
            default:
                return;
        }

        switch (notification.getHmiLevel()) {
            case HMI_FULL:
                Logger.i("HMI_FULL");
//                try {
//                    proxy.getvehicledata(true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, autoIncCorrId++);
//                } catch (Exception e){
//                    e.printStackTrace();
//                    Logger.d("Error while subscribe to vehicle data");
//                }
                autoIncCorrId = CommandManager.addVoiceCommands(proxy, autoIncCorrId);
//                try {
//                    proxy.show("Seja bem-vindo ao ", "HEERE", TextAlignment.CENTERED, autoIncCorrId++);
//                } catch (SyncException e) {
//                    DebugTool.logError("Failed to send Show", e);
//                }
                autoIncCorrId = ScreenConfig.initialScreen(autoIncCorrId, proxy);
                break;
            case HMI_LIMITED:
                Logger.i("HMI_LIMITED");
                break;
            case HMI_BACKGROUND:
                Logger.i("HMI_BACKGROUND");
                break;
            case HMI_NONE:
                Logger.i("HMI_NONE");
                break;
            default:
                return;
        }
    }

    @Override
    public void onOnDriverDistraction(OnDriverDistraction notification) {
        Logger.d("Driver distraction: " + notification.getFunctionName());
        try {
            proxy.getvehicledata(true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, autoIncCorrId++);
        } catch (Exception e){
            e.printStackTrace();
            Logger.d("Error while subscribe to vehicle data");
        }

    }

    @Override
    public void onError(String info, Exception e) {
        // TODO Auto-generated method stub
        e.printStackTrace();
    }

    @Override
    public void onGenericResponse(GenericResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onOnCommand(OnCommand notification) {
        // TODO Auto-generated method stub
        CommandManager.handleCommand(this, notification);
    }

    @Override
    public void onAddCommandResponse(AddCommandResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAddSubMenuResponse(AddSubMenuResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onCreateInteractionChoiceSetResponse(
            CreateInteractionChoiceSetResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAlertResponse(AlertResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDeleteCommandResponse(DeleteCommandResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDeleteInteractionChoiceSetResponse(
            DeleteInteractionChoiceSetResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDeleteSubMenuResponse(DeleteSubMenuResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPerformInteractionResponse(PerformInteractionResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onResetGlobalPropertiesResponse(
            ResetGlobalPropertiesResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse response) {
    }

    @Override
    public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onShowResponse(ShowResponse response) {
        // TODO Auto-generated method stub

        Logger.d("onShowResponse" + response.toString());
    }

    @Override
    public void onSpeakResponse(SpeakResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onOnButtonEvent(OnButtonEvent notification) {
        // TODO Auto-generated method stub

        Logger.d("onOnButtonEvent" + notification.toString());
    }

    @Override
    public void onOnButtonPress(OnButtonPress notification) {
        // TODO Auto-generated method stub
        switch (notification.getButtonName()) {
            case CUSTOM_BUTTON:
                ScreenConfig.onCustomButtomPressed(this, notification);
                break;
        }
    }

    @Override
    public void onSubscribeButtonResponse(SubscribeButtonResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onOnPermissionsChange(OnPermissionsChange notification) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onOnTBTClientState(OnTBTClientState notification) {
        // TODO Auto-generated method stub
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse response) {
        // TODO Auto-generated method stub
        if (response.getResultCode() == Result.DISALLOWED)  {
            Logger.i("onGetVehicleDataResponse read disallowed");
            return;
        }
        else if (response.getResultCode() == Result.USER_DISALLOWED)  {
            Logger.i("VehicleData read user disallowed");
            return;
        }
    }

    @Override
    public void onUnsubscribeVehicleDataResponse(
            UnsubscribeVehicleDataResponse response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onGetVehicleDataResponse(GetVehicleDataResponse response) {
        // TODO Auto-generated method stub
        if (response.getResultCode() == Result.DISALLOWED)  {
            Logger.i("onGetVehicleDataResponse read disallowed");
            return;
        }
        else if (response.getResultCode() == Result.USER_DISALLOWED)  {
            Logger.i("VehicleData read user disallowed");
            return;
        }
        //VehicleDataManager.handleVehicleData(this, response);
    }

    @Override
    public void onReadDIDResponse(ReadDIDResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetDTCsResponse(GetDTCsResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOnVehicleData(OnVehicleData notification) {
        // TODO Auto-generated method stub
        VehicleDataManager.handleVehicleData(this, notification);
    }

    @Override
    public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEndAudioPassThruResponse(EndAudioPassThruResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOnAudioPassThru(OnAudioPassThru notification) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPutFileResponse(PutFileResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDeleteFileResponse(DeleteFileResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onListFilesResponse(ListFilesResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSetAppIconResponse(SetAppIconResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScrollableMessageResponse(ScrollableMessageResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onChangeRegistrationResponse(ChangeRegistrationResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOnLanguageChange(OnLanguageChange notification) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSliderResponse(SliderResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDiagnosticMessageResponse(DiagnosticMessageResponse arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOnHashChange(OnHashChange arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOnKeyboardInput(OnKeyboardInput arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOnSystemRequest(OnSystemRequest arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOnTouchEvent(OnTouchEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSystemRequestResponse(SystemRequestResponse arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOnLockScreenNotification(OnLockScreenStatus notification) {
        LockScreenStatus displayLockScreen = notification.getShowLockScreen();
        // Show lockscreen in both REQUIRED and OPTIONAL
        //if (displayLockScreen == LockScreenStatus.REQUIRED || displayLockScreen == LockScreenStatus.OPTIONAL) {
        //Show lockscreen in only REQUIRED
        if (displayLockScreen == LockScreenStatus.REQUIRED) {
            LockScreenManager.showLockScreen();
        } else {
            LockScreenManager.clearLockScreen();
        }
    }
}
