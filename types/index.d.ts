export interface Window {
    KioskMode: KioskModeInterface
}

export interface KioskOptions {
    safeBootManaged: boolean;
    addUserManaged: boolean;
    bluetoothManaged: boolean;
    adjustVolumeManaged: boolean;
    createWindowsManaged: boolean;
    keyguardManaged: boolean;
    statusBarManaged: boolean;
    packagesLockingManaged: boolean;
    allowedThirdPackages: String[];
    homeReceiverManaged: boolean;
}

export interface KioskModeInterface {
    lock(kioskOptions?: KioskOptions): never;

    unlock(kioskOptions?: KioskOptions): never;

    clearDeviceOwner(): never;
}
