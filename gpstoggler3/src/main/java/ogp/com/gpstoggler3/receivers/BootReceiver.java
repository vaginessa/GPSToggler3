package ogp.com.gpstoggler3.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ogp.com.gpstoggler3.TogglerService;
import ogp.com.gpstoggler3.debug.Constants;
import ogp.com.gpstoggler3.su.RootCaller;


public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(Constants.TAG, "BootReceiver::onReceive. Entry...");

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.v(Constants.TAG, "BootReceiver::onReceive. Activating service...");

            RootCaller.setSecureSettings(context.getPackageName());
            TogglerService.startServiceForever(context.getApplicationContext());

            Log.v(Constants.TAG, "BootReceiver::onReceive. Activating service finished.");
        }

        Log.v(Constants.TAG, "BootReceiver::onReceive. Exit.");
    }
}
