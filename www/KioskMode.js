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
   * @param {function} resolve
   * @param {function} reject
   * @param {KioskOptions} kioskOptions
   */
  lock: function (resolve, reject, kioskOptions = null) {
    exec(resolve, reject, 'KioskMode', 'lock', [kioskOptions])
  },

  /**
   * @param {function} resolve
   * @param {function} reject
   * @param {KioskOptions} kioskOptions
   */
  unlock: function (resolve, reject, kioskOptions = null) {
    exec(resolve, reject, 'KioskMode', 'unlock', [kioskOptions])
  },

  clearDeviceOwner: function (resolve, reject, kioskOptions = null) {
    exec(resolve, reject, 'KioskMode', 'clearDeviceOwner', [])
  },
}