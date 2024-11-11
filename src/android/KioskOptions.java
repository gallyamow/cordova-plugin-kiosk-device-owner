package io.github.gallyamow.kiosk_plugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

// TODO: configurable from options of js-part
public class KioskOptions {
    private static final String TAG = "KioskMode:KioskOptions";

    private boolean safeBootManaged;
    private boolean addUserManaged;
    private boolean bluetoothManaged;
    private boolean adjustVolumeManaged;
    private boolean createWindowsManaged;
    private boolean keyguardManaged;
    private boolean statusBarManaged;
    private boolean packagesLockingManaged;
    private String[] allowedThirdPackages;
    private boolean homeReceiverManaged;

    private KioskOptions() {
    }

    public static KioskOptions defaultOptions() {
        KioskOptions options = new KioskOptions();

        options.safeBootManaged = true;
        options.addUserManaged = true;
        options.bluetoothManaged = true;
        options.adjustVolumeManaged = true;
        options.createWindowsManaged = true;
        options.keyguardManaged = true;
        options.statusBarManaged = true;
        options.packagesLockingManaged = true;
        options.homeReceiverManaged = true;
        options.allowedThirdPackages = new String[]{};

        return options;
    }

    public static KioskOptions parseFromJson(JSONObject jsonObject) throws JSONException {
        KioskOptions options = defaultOptions();

        options.safeBootManaged = jsonObject.optBoolean("safeBootManaged", true);
        options.addUserManaged = jsonObject.optBoolean("addUserManaged", true);
        options.bluetoothManaged = jsonObject.optBoolean("bluetoothManaged", true);
        options.adjustVolumeManaged = jsonObject.optBoolean("adjustVolumeManaged", true);
        options.createWindowsManaged = jsonObject.optBoolean("createWindowsManaged", true);
        options.keyguardManaged = jsonObject.optBoolean("keyguardManaged", true);
        options.statusBarManaged = jsonObject.optBoolean("statusBarManaged", true);
        options.packagesLockingManaged = jsonObject.optBoolean("packagesLockingManaged", true);
        options.homeReceiverManaged = jsonObject.optBoolean("homeReceiverManaged", true);

        if (jsonObject.has("allowedThirdPackages")) {
            JSONArray jsonArray = jsonObject.getJSONArray("allowedThirdPackages");
            options.allowedThirdPackages = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                options.allowedThirdPackages[i] = jsonArray.getString(i);
            }
        }

        return options;
    }

    public boolean isSafeBootManaged() {
        return safeBootManaged;
    }

    public boolean isAddUserManaged() {
        return addUserManaged;
    }

    public boolean isBluetoothManaged() {
        return bluetoothManaged;
    }

    public boolean isAdjustVolumeManaged() {
        return adjustVolumeManaged;
    }

    public boolean isCreateWindowsManaged() {
        return createWindowsManaged;
    }

    public boolean isKeyguardManaged() {
        return keyguardManaged;
    }

    public boolean isStatusBarManaged() {
        return statusBarManaged;
    }

    public boolean isPackagesLockingManaged() {
        return packagesLockingManaged;
    }

    public boolean isHomeReceiverManaged() {
        return homeReceiverManaged;
    }

    public String[] getAllowedThirdPackages() {
        return allowedThirdPackages;
    }

    @Override
    public String toString() {
        return "KioskOptions{" +
                "safeBootManaged=" + safeBootManaged +
                ", addUserManaged=" + addUserManaged +
                ", bluetoothManaged=" + bluetoothManaged +
                ", adjustVolumeManaged=" + adjustVolumeManaged +
                ", createWindowsManaged=" + createWindowsManaged +
                ", keyguardManaged=" + keyguardManaged +
                ", statusBarManaged=" + statusBarManaged +
                ", packagesLockingManaged=" + packagesLockingManaged +
                ", allowedThirdPackages=" + Arrays.toString(allowedThirdPackages) +
                ", homeReceiverManaged=" + homeReceiverManaged +
                '}';
    }
}
