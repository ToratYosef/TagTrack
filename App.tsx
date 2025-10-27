import React from 'react';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { StatusBar } from 'expo-status-bar';
import './src/i18n';
import HomeScreen from '@/screens/HomeScreen';
import AddItemScreen from '@/screens/AddItemScreen';
import ItemDetailScreen from '@/screens/ItemDetailScreen';
import OnboardingScreen from '@/screens/OnboardingScreen';
import SettingsScreen from '@/screens/SettingsScreen';
import ScanScreen from '@/screens/ScanScreen';
import { AuthProvider, useAuth } from '@/state/AuthContext';
import { SettingsProvider } from '@/state/SettingsContext';

export type RootStackParamList = {
  Onboarding: undefined;
  Home: undefined;
  AddItem: undefined;
  ItemDetail: { itemId: string };
  Settings: undefined;
  Scan: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();
const queryClient = new QueryClient();

const AppNavigator = () => {
  const { user, loading } = useAuth();

  if (loading) {
    return null;
  }

  return (
    <NavigationContainer>
      <Stack.Navigator screenOptions={{ headerShown: false }}>
        {!user ? (
          <Stack.Screen name="Onboarding" component={OnboardingScreen} />
        ) : (
          <>
            <Stack.Screen name="Home" component={HomeScreen} />
            <Stack.Screen name="AddItem" component={AddItemScreen} />
            <Stack.Screen name="ItemDetail" component={ItemDetailScreen} />
            <Stack.Screen name="Settings" component={SettingsScreen} />
            <Stack.Screen name="Scan" component={ScanScreen} />
          </>
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
};

const App = () => (
  <AuthProvider>
    <SettingsProvider>
      <QueryClientProvider client={queryClient}>
        <SafeAreaProvider>
          <StatusBar style="light" />
          <AppNavigator />
        </SafeAreaProvider>
      </QueryClientProvider>
    </SettingsProvider>
  </AuthProvider>
);

export default App;
