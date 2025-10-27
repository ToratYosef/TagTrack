/** @type {Detox.DetoxConfig} */
module.exports = {
  testRunner: {
    args: {
      $0: 'jest',
      config: 'e2e/jest.config.js'
    },
    jest: {
      setupTimeout: 120000
    }
  },
  apps: {
    'ios.release': {
      type: 'ios.app',
      build: 'eas build --platform ios --profile preview',
      binaryPath: 'bin/TagTrack.app'
    },
    'android.release': {
      type: 'android.apk',
      build: 'eas build --platform android --profile preview',
      binaryPath: 'bin/TagTrack.apk'
    }
  },
  devices: {
    simulator: {
      type: 'ios.simulator',
      device: { type: 'iPhone 14' }
    },
    emulator: {
      type: 'android.emulator',
      device: { avdName: 'Pixel_6' }
    }
  },
  configurations: {
    ios: {
      device: 'simulator',
      app: 'ios.release'
    },
    android: {
      device: 'emulator',
      app: 'android.release'
    }
  }
};
