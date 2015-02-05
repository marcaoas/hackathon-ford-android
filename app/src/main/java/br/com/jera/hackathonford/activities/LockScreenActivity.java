package br.com.jera.hackathonford.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ford.syncV4.proxy.SyncProxyALM;

import br.com.jera.hackathonford.R;
import br.com.jera.hackathonford.applink.AppLinkService;

/**
 * Created by marco on 05/02/15.
 */
public class LockScreenActivity extends BaseActivity {
    private static LockScreenActivity instance;

    static {
        LockScreenActivity.instance = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockscreen);
        LockScreenActivity.instance = this;

        // Optional button on the lockscreen to reset the SYNC proxy
        final Button resetSYNCButton = (Button) findViewById(R.id.reset_button);
        resetSYNCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset proxy; do not shut down service
                AppLinkService serviceInstance = AppLinkService.getInstance();
                if (serviceInstance != null) {
                    SyncProxyALM proxyInstance = serviceInstance.getProxy();
                    if (proxyInstance != null) {
                        serviceInstance.reset();
                    } else {
                        serviceInstance.startProxy();
                    }
                }
            }
        });
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
