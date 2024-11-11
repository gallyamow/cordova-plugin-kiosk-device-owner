package io.github.gallyamow.kiosk_plugin;

import android.content.ComponentName;
import android.content.Context;

public class KioskDeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver {
    private static final String TAG = "KioskMode:KioskDeviceAdminReceiver";

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), KioskDeviceAdminReceiver.class);
    }
}
