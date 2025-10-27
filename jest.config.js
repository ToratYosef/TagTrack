module.exports = {
  preset: 'jest-expo',
  setupFilesAfterEnv: ['@testing-library/jest-native/extend-expect'],
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/$1'
  },
  transformIgnorePatterns: [
    'node_modules/(?!(react-native|@react-native|expo(nent)?|@expo|expo-.*|@expo-.*|@unimodules|unimodules-.*|sentry-expo)/)'
  ],
  testPathIgnorePatterns: ['/node_modules/', '/e2e/']
};
