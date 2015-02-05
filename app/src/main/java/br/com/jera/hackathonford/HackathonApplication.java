package br.com.jera.hackathonford;

import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.ford.syncV4.exception.SyncException;
import com.ford.syncV4.proxy.SyncProxyALM;

import br.com.jera.hackathonford.activities.BaseActivity;
import br.com.jera.hackathonford.applink.AppLinkService;
import br.com.jera.hackathonford.utils.Logger;

/**
 * Created by marco on 05/02/15.
 */
public class HackathonApplication extends Application{
    public static final String TAG = "Hello AppLink"; // Global TAG used in logging
    private static HackathonApplication instance;
    private static BaseActivity currentUIActivity;

    static {
        instance = null;
    }

    private static synchronized void setInstance(HackathonApplication app) {
        instance = app;
    }

    public static synchronized HackathonApplication getInstance() {
        return instance;
    }

    public static synchronized void setCurrentActivity(BaseActivity act) {
        currentUIActivity = act;
    }

    public static synchronized BaseActivity getCurrentActivity() {
        return currentUIActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HackathonApplication.setInstance(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public void startSyncProxyService() {
        // Get the local Bluetooth adapter
        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // If BT adapter exists, is enabled, and there are paired devices, start service/proxy
        if (mBtAdapter != null)
        {
            if ((mBtAdapter.isEnabled() && mBtAdapter.getBondedDevices().isEmpty() == false))
            {
                Intent startIntent = new Intent(this, AppLinkService.class);
                startService(startIntent);
            }
        }
    }

    // Recycle the proxy
    public void endSyncProxyInstance() {
        AppLinkService serviceInstance = AppLinkService.getInstance();
        if (serviceInstance != null){
            SyncProxyALM proxyInstance = serviceInstance.getProxy();
            // if proxy exists, reset it
            if(proxyInstance != null){
                serviceInstance.reset();
                // if proxy == null create proxy
            } else {
                serviceInstance.startProxy();
            }
        }
    }

    // Stop the AppLinkService
    public void endSyncProxyService() {
        AppLinkService serviceInstance = AppLinkService.getInstance();
        if (serviceInstance != null){
            serviceInstance.stopService();
        }
    }

    public void showAppVersion(Context context) {
        String appMessage = "HelloApplink Version Info not available";
        String proxyMessage = "Proxy Version Info not available";
        AppLinkService serviceInstance = AppLinkService.getInstance();
        try {
            appMessage = "HelloApplink Version: " +
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.d("Can't get package info");
            e.printStackTrace();
        }

        try {
            if (serviceInstance != null){
                SyncProxyALM syncProxy = serviceInstance.getProxy();
                if (syncProxy != null){
                    String proxyVersion = syncProxy.getProxyVersionInfo();
                    if (proxyVersion != null){
                        proxyMessage = "Proxy Version: " + proxyVersion;
                    }
                }
            }
        } catch (SyncException e) {
            Logger.d("Can't get Proxy Version");
            e.printStackTrace();
        }
        new AlertDialog.Builder(context).setTitle("App Version Information")
                .setMessage(appMessage + "\r\n" + proxyMessage)
                .setNeutralButton(android.R.string.ok, null).create().show();
    }

}
