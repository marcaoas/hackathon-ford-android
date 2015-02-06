package br.com.jera.hackathonford.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompat;

import br.com.jera.hackathonford.R;
import br.com.jera.hackathonford.activities.MainActivity;

/**
 * Created by marco on 05/02/15.
 */
public class PanicoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent i = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText("SOCORROOOO!!!!!");
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("SOCORROOOO!!!!!"));
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(1, mBuilder.build());
    }
}
