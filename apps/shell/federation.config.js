const { withNativeFederation, shareAll } = require('@angular-architects/native-federation/config');

module.exports = withNativeFederation({
  name: 'shell',
  remotes: {
    'mf-auth': 'http://localhost:4201/remoteEntry.json',
    'mf-catalog': 'http://localhost:4202/remoteEntry.json',
    'mf-inventory': 'http://localhost:4203/remoteEntry.json',
    'mf-procurement': 'http://localhost:4204/remoteEntry.json',
    'mf-sales': 'http://localhost:4205/remoteEntry.json',
    'mf-operations': 'http://localhost:4206/remoteEntry.json',
    'mf-analytics': 'http://localhost:4207/remoteEntry.json',
  },
  shared: {
    ...shareAll({
      singleton: true,
      strictVersion: true,
      requiredVersion: 'auto',
    }),
  },
  sharedMappings: ['@supermarket/shared-ui'],
  skip: [
    'rxjs/ajax',
    'rxjs/fetch',
    'rxjs/testing',
    'rxjs/webSocket',
  ],
});
