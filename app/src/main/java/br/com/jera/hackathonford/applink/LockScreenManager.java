package br.com.jera.hackathonford.applink;

import android.content.Intent;

import br.com.jera.hackathonford.HackathonApplication;
import br.com.jera.hackathonford.activities.LockScreenActivity;

/**
 * Created by marco on 05/02/15.
 */
public class LockScreenManager {

    private static boolean lockScreenUp = false;

    public static synchronized void showLockScreen() {
        // only show the lockscreen if main activity is currently on top
        // else, wait until onResume() to show the lockscreen so it doesn't
        // pop-up while a user is using another app on the phone
        if (HackathonApplication.getCurrentActivity() != null) {
            if (((AppLinkActivity) HackathonApplication.getCurrentActivity()).isActivityonTop() == true) {
                Intent i = new Intent(HackathonApplication.getInstance(), LockScreenActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                HackathonApplication.getInstance().startActivity(i);
            }
        }
        lockScreenUp = true;
    }

    public static synchronized void clearLockScreen() {
        if (LockScreenActivity.getInstance() != null) {
            LockScreenActivity.getInstance().exit();
        }
        lockScreenUp = false;
    }

    public static synchronized boolean getLockScreenStatus() {
        return lockScreenUp;
    }
}
