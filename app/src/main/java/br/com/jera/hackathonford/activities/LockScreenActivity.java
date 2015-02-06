package br.com.jera.hackathonford.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ford.syncV4.proxy.SyncProxyALM;

import br.com.jera.hackathonford.R;
import br.com.jera.hackathonford.applink.AppLinkService;
import br.com.jera.hackathonford.utils.Constants;
import br.com.jera.hackathonford.utils.Logger;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by marco on 05/02/15.
 */
public class LockScreenActivity extends BaseActivity {
    private static LockScreenActivity instance;

    @InjectView(R.id.text)
    TextView text;

    static {
        LockScreenActivity.instance = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockscreen);
        ButterKnife.inject(this);

        LockScreenActivity.instance = this;
        getSupportActionBar().hide();
    }

    final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent i) {
            if(i!=null){
                String lat = i.getStringExtra(Constants.Extras.LAT);
                String lng = i.getStringExtra(Constants.Extras.LNG);
                text.setText(String.valueOf(lat) + "  --- " + String.valueOf(lng));
                Logger.d(String.valueOf(lat) + "  --- " + String.valueOf(lng));
            }
        }
    };

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter(
                Constants.Receivers.COORDINATES_RECEIVED);
        registerReceiver(receiver, filter);
        super.onResume();
    }

    // Disable back button on lockscreen
    @Override
    public void onBackPressed() {
    }

    @Override
    public void onDestroy() {
        LockScreenActivity.instance = null;
        super.onDestroy();
    }

    public void exit() {
        super.finish();
    }

    public static LockScreenActivity getInstance() {
        return instance;
    }
}
