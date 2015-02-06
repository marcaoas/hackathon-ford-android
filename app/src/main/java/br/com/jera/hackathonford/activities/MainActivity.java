package br.com.jera.hackathonford.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import br.com.jera.hackathonford.HackathonApplication;
import br.com.jera.hackathonford.R;
import br.com.jera.hackathonford.applink.AppLinkActivity;
import br.com.jera.hackathonford.receiver.PanicoReceiver;
import br.com.jera.hackathonford.utils.Constants;
import br.com.jera.hackathonford.utils.Logger;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;


public class MainActivity extends AppLinkActivity {

    @InjectView(R.id.text)
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        HackathonApplication app = HackathonApplication.getInstance();
        if (app != null) {
            app.startSyncProxyService();
        }
        HackathonApplication.setCurrentActivity(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
