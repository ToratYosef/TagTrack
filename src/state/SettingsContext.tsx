import React, { createContext, useContext, useMemo, useReducer } from 'react';
import { UserSettings } from '@/types/item';

type Action =
  | { type: 'SET_SETTINGS'; payload: UserSettings }
  | { type: 'TOGGLE_CLOUD_BACKUP' }
  | { type: 'TOGGLE_ANALYTICS' };

const defaultSettings: UserSettings = {
  cloudBackup: false,
  analytics: false,
};

interface SettingsContextValue {
  settings: UserSettings;
  dispatch: React.Dispatch<Action>;
}

function reducer(state: UserSettings, action: Action): UserSettings {
  switch (action.type) {
    case 'SET_SETTINGS':
      return { ...state, ...action.payload };
    case 'TOGGLE_CLOUD_BACKUP':
      return { ...state, cloudBackup: !state.cloudBackup };
    case 'TOGGLE_ANALYTICS':
      return { ...state, analytics: !state.analytics };
    default:
      return state;
  }
}

const SettingsContext = createContext<SettingsContextValue>({ settings: defaultSettings, dispatch: () => {} });

export const SettingsProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [settings, dispatch] = useReducer(reducer, defaultSettings);
  const value = useMemo(() => ({ settings, dispatch }), [settings]);
  return <SettingsContext.Provider value={value}>{children}</SettingsContext.Provider>;
};

export const useSettings = () => useContext(SettingsContext);
