import React from 'react';
import { render } from '@testing-library/react-native';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { NavigationContainer } from '@react-navigation/native';
import AddItemScreen from '@/screens/AddItemScreen';
import { AuthProvider } from '@/state/AuthContext';
import { SettingsProvider } from '@/state/SettingsContext';

describe('AddItemScreen', () => {
  it('renders the add item form', () => {
    const queryClient = new QueryClient();
    const tree = render(
      <AuthProvider>
        <SettingsProvider>
          <QueryClientProvider client={queryClient}>
            <NavigationContainer>
              {/* @ts-expect-error simplified test navigation props */}
              <AddItemScreen navigation={{ replace: jest.fn() }} route={{}} />
            </NavigationContainer>
          </QueryClientProvider>
        </SettingsProvider>
      </AuthProvider>,
    );

    expect(tree.getByText('Tap an NFC tag to start')).toBeTruthy();
  });
});
