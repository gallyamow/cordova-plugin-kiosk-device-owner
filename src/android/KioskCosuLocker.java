package io.github.gallyamow.kiosk_plugin;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.UserManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KioskCosuLocker {
    private static final String TAG = "KioskMode:KioskCosuLocker";

    private final String appPackageName;
    private final Activity activity;
    private final DevicePolicyManager devicePolicyManager;
    private final ComponentName adminComponentName;
    private final PackageManager packageManager;
    private final ActivityManager activityManager;

    public KioskCosuLocker(Activity activity) {
        this.activity = activity;
        this.appPackageName = activity.getPackageName();
        this.devicePolicyManager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        this.adminComponentName = KioskDeviceAdminReceiver.getComponentName(activity);
        this.packageManager = activity.getPackageManager();
        this.activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public void lock(KioskOptions kioskOptions) {
        this.assertDeviceSupportedVersion();
        this.assertDeviceOwner();

        enableComponent();
        manageUserRestrictions(true, kioskOptions);
        manageActivityAsHomeReceiver(true);
        manageActivityLocking(true);
    }

    public void unlock(KioskOptions kioskOptions) {
        this.assertDeviceSupportedVersion();
        this.assertDeviceOwner();

        if (activityManager.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_LOCKED) {
            activity.stopLockTask();
        }

        manageUserRestrictions(false, kioskOptions);
        manageActivityAsHomeReceiver(false);
        manageActivityLocking(false);
    }

    /**
     * @see https://developer.android.com/reference/android/app/admin/DevicePolicyManager#clearDeviceOwnerApp
     * @deprecated This method is expected to be used for testing purposes only.
     */
    public void clearDeviceOwner() {
        this.assertDeviceSupportedVersion();
        this.assertDeviceOwner();

        devicePolicyManager.clearDeviceOwnerApp(appPackageName);
    }

    private void manageUserRestrictions(boolean lock, KioskOptions kioskOptions) {
        if (kioskOptions.isAddUserManaged()) {
            manageUserRestriction(UserManager.DISALLOW_ADD_USER, lock);
        }
        if (kioskOptions.isSafeBootManaged()) {
            manageUserRestriction(UserManager.DISALLOW_SAFE_BOOT, lock);
        }
        if (kioskOptions.isBluetoothManaged()) {
            manageUserRestriction(UserManager.DISALLOW_BLUETOOTH, lock);
        }
        if (kioskOptions.isAdjustVolumeManaged()) {
            manageUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, lock);
        }
        if (kioskOptions.isCreateWindowsManaged()) {
            manageUserRestriction(UserManager.DISALLOW_CREATE_WINDOWS, lock);
        }
        if (kioskOptions.isKeyguardManaged()) {
            manageKeyguard(lock);
        }
        if (kioskOptions.isStatusBarManaged()) {
            manageStatusBar(lock);
        }
        if (kioskOptions.isPackagesLockingManaged()) {
            managePackagesLocking(lock, kioskOptions.getAllowedThirdPackages());
        }

        /*
         to implement in future
         * setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, active);
         * setUserRestriction(UserManager.DISALLOW_APPS_CONTROL, active);
         * setUserRestriction(UserManager.DISALLOW_INSTALL_APPS, active);
         * setUserRestriction(UserManager.DISALLOW_AIRPLANE_MODE, active);
         * setUserRestriction(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES_GLOBALLY, active);
         * setUserRestriction(UserManager.DISALLOW_CONFIG_DATE_TIME, active);
         * setUserRestriction(UserManager.DISALLOW_SMS, active);
         */
    }

    private void manageActivityAsHomeReceiver(boolean lock) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        if (lock) {
            devicePolicyManager.addPersistentPreferredActivity(
                    adminComponentName,
                    intentFilter,
                    new ComponentName(appPackageName, activity.getClass().getName())
            );
            Log.d(TAG, "Activity set as home intent receiver.");
        } else {
            devicePolicyManager.clearPackagePersistentPreferredActivities(adminComponentName, appPackageName);
            Log.d(TAG, "Activity removed from home intent receiver.");
        }
    }

    private void manageUserRestriction(String restriction, boolean lock) {
        if (lock) {
            devicePolicyManager.addUserRestriction(adminComponentName, restriction);
            Log.d(TAG, "User restriction added: " + restriction);
        } else {
            devicePolicyManager.clearUserRestriction(adminComponentName, restriction);
            Log.d(TAG, "User restriction cleared: " + restriction);
        }
    }

    private void manageKeyguard(boolean lock) {
        devicePolicyManager.setKeyguardDisabled(adminComponentName, lock);
        Log.d(TAG, "Keyguard disabled: " + lock);
    }

    private void manageStatusBar(boolean lock) {
        devicePolicyManager.setStatusBarDisabled(adminComponentName, lock);
        Log.d(TAG, "Status bar disabled: " + lock);
    }

    private void managePackagesLocking(boolean lock, String[] allowedPackages) {
        if (lock) {
            List<String> a = new ArrayList<>(Arrays.asList(allowedPackages));
            if (!a.contains(appPackageName)) {
                a.add(appPackageName);
            }
            setLockTaskPackages(a.toArray(new String[0]));
            Log.d(TAG, "Locking packages set: " + Arrays.toString(a.toArray()));
        } else {
            clearLockTaskPackages();
            Log.d(TAG, "Locking packages cleared.");
        }
    }

    private void setLockTaskPackages(String[] allowedPackages) {
        devicePolicyManager.setLockTaskPackages(adminComponentName, allowedPackages);
    }

    private void clearLockTaskPackages() {
        devicePolicyManager.setLockTaskPackages(adminComponentName, new String[]{});
    }

    private void assertDeviceOwner() {
        if (!isDeviceOwner()) {
            String message = String.format("Application '%s' is not the owner of the device.", appPackageName);
            Log.e(TAG, message);
            throw new RuntimeException(message);
        }

    }

    private void assertDeviceSupportedVersion() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            String message = String.format("Your device with version '%d' does not support COSU.", Build.VERSION.SDK_INT);
            Log.e(TAG, message);
            throw new RuntimeException(message);
        }
    }

    private boolean isDeviceOwner() {
        return devicePolicyManager.isDeviceOwnerApp(appPackageName);
    }

    private void enableComponent() {
        packageManager.setComponentEnabledSetting(
                new ComponentName(activity.getApplicationContext(), activity.getClass()),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
        );
        Log.d(TAG, "Component enabled.");
    }

    //TODO: остается как неактивное и затем не получается переустановить
    private void disableComponent() {
        packageManager.setComponentEnabledSetting(
                new ComponentName(activity.getApplicationContext(), activity.getClass()),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
        );
        Log.d(TAG, "Component disabled.");
    }

    private void manageActivityLocking(boolean lock) {
        if (!devicePolicyManager.isLockTaskPermitted(appPackageName)) {
            Log.e(TAG, "No permission to lock task.");
            return;
        }

        if (lock && activityManager.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE) {
            // In Android versions before 9.0, an app starts its own activities in lock task mode by calling Activity.startLockTask().
            Log.d(TAG, "Activity start lock task mode.");
            activity.startLockTask();
        }

        if (!lock && activityManager.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_LOCKED) {
            Log.d(TAG, "Activity start lock task mode.");
            activity.stopLockTask();
        }
    }
}