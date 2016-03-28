package car.ameba.ridelele.utills;

/**
 * Created by ameba on 11/17/15.
 */

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class BroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            Log.e("GcmBroadcastReceiver ","pohnch gaya on Receive ch...");
// Explicitly specify that GcmIntentService will handle the intent.
            ComponentName comp = new ComponentName(context.getPackageName(),GCMIntentService.class.getName());
// Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
          // setResultCode(Activity.RESULT_OK);
        }
        catch(Exception ex){
            Log.e("Exception in GcmBrdRec ", ex.getMessage());
        }
    }
}