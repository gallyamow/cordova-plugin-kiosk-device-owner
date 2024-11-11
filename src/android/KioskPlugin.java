package io.github.gallyamow.kiosk_plugin;

import android.app.Activity;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KioskPlugin extends CordovaPlugin {
    private static final String TAG = "KioskMode:KioskPlugin";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, String.format("Executing action '%s' with args '%s'", action, args));

        Activity activity = cordova.getActivity();
        KioskCosuLocker locker = new KioskCosuLocker(activity);

        try {
            if ("lock".equals(action)) {
                JSONObject jsonOptions = args.optJSONObject(0);
                KioskOptions kioskOptions = null == jsonOptions ? KioskOptions.defaultOptions() : KioskOptions.parseFromJson(jsonOptions);

                // TODO: THREAD WARNING: exec() call to KioskMode.lock blocked the main thread for 161ms. Plugin should use CordovaInterface.getThreadPool().
                locker.lock(kioskOptions);
                callbackContext.success("done");

                Log.d(TAG, String.format("Successfully executed '%s'", action));

                return true;
            }

            if ("unlock".equals(action)) {
                JSONObject jsonOptions = args.optJSONObject(0);
                KioskOptions kioskOptions = null == jsonOptions ? KioskOptions.defaultOptions() : KioskOptions.parseFromJson(jsonOptions);

                // TODO: THREAD WARNING: exec() call to KioskMode.lock blocked the main thread for 161ms. Plugin should use CordovaInterface.getThreadPool().
                locker.unlock(kioskOptions);
                callbackContext.success("done");

                Log.d(TAG, String.format("Successfully executed '%s'", action));

                return true;
            }

            if ("clearDeviceOwner".equals(action)) {
                // TODO: THREAD WARNING: exec() call to KioskMode.lock blocked the main thread for 161ms. Plugin should use CordovaInterface.getThreadPool().
                locker.clearDeviceOwner();
                callbackContext.success("done");

                Log.d(TAG, String.format("Successfully executed '%s'", action));

                return true;
            }
        } catch (Throwable e) {
            Log.e(TAG, "error", e);
            callbackContext.error(e.toString());

            return false;
        }

        return false;
    }
}