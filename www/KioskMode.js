/*global cordova, module*/
var exec = require('cordova/exec')

/**
 * @typedef {object} KioskOptions
 * @property {boolean} safeBootManaged
 * @property {boolean} addUserManaged
 * @property {boolean} bluetoothManaged
 * @property {boolean} adjustVolumeManaged
 * @property {boolean} createWindowsManaged
 * @property {boolean} createWindowsManaged
 * @property {boolean} keyguardManaged
 * @property {boolean} statusBarManaged
 * @property {boolean} packagesLockingManaged
 * @property {boolean} allowedThirdPackages
 * @property {boolean} homeReceiverManaged
 */

module.exports = {
  /**
   * @param {?KioskOptions} kioskOptions
   * @returns {Promise<unknown>}
   */
  lock: function (kioskOptions = null) {
    return new Promise((resolve, reject) => {
      exec(resolve, reject, 'KioskMode', 'lock', [kioskOptions])
    })
  },

  /**
   * @param {?KioskOptions} kioskOptions
   * @returns {Promise<unknown>}
   */
  unlock: function (kioskOptions = null) {
    return new Promise((resolve, reject) => {
      exec(resolve, reject, 'KioskMode', 'unlock', [kioskOptions])
    })
  },

  /**
   * @returns {Promise<unknown>}
   */
  clearDeviceOwner: function () {
    return new Promise((resolve, reject) => {
      exec(resolve, reject, 'KioskMode', 'clearDeviceOwner', [])
    })
  },
}