package br.com.jera.hackathonford.applink;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import br.com.jera.hackathonford.HackathonApplication;
import br.com.jera.hackathonford.activities.BaseActivity;

/**
 * Created by marco on 05/02/15.
 */
public class AppLinkActivity extends BaseActivity {
    private boolean activityOnTop;

    /**
     * Activity is moving to the foreground.
     * Set this activity as the current activity and that it is on top.
     * Update the lockscreen.
     */
    @Override
    protected void onResume() {
        super.onResume();
        HackathonApplication.setCurrentActivity(this);
        activityOnTop = true;
        if (LockScreenManager.getLockScreenStatus()) {
            LockScreenManager.showLockScreen();
        }
    }

    /**
     * Activity becoming partially visible (obstructed by another).
     * Activity is no longer on top.
     */
    @Override
    protected void onPause() {
        activityOnTop = false;
        super.onPause();
    }

    /**
     * Activity is no longer visible on the screen.
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        // Set the current activity to null if no other activity has taken the foreground.
        if (HackathonApplication.getCurrentActivity() == this) {
            HackathonApplication.setCurrentActivity(null);
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    public boolean isActivityonTop(){
        return activityOnTop;
    }
}
