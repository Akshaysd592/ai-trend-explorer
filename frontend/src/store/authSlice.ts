import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { UserInfo } from '@/types/auth';

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  user: UserInfo | null;
  isAuthenticated: boolean;
}

// Hydrate from localStorage on first load (client-side only)
const getInitialState = (): AuthState => {
  if (typeof window === 'undefined') {
    return { accessToken: null, refreshToken: null, user: null, isAuthenticated: false };
  }
  try {
    const token = localStorage.getItem('accessToken');
    const user = localStorage.getItem('authUser');
    if (token && user) {
      return {
        accessToken: token,
        refreshToken: localStorage.getItem('refreshToken'),
        user: JSON.parse(user),
        isAuthenticated: true,
      };
    }
  } catch {
    // Corrupt localStorage — clear and start fresh
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('authUser');
  }
  return { accessToken: null, refreshToken: null, user: null, isAuthenticated: false };
};

export const authSlice = createSlice({
  name: 'auth',
  initialState: getInitialState(),
  reducers: {
    setCredentials: (
      state,
      action: PayloadAction<{ accessToken: string; refreshToken: string; user: UserInfo }>
    ) => {
      state.accessToken = action.payload.accessToken;
      state.refreshToken = action.payload.refreshToken;
      state.user = action.payload.user;
      state.isAuthenticated = true;

      // Persist to localStorage for page refresh survival
      if (typeof window !== 'undefined') {
        localStorage.setItem('accessToken', action.payload.accessToken);
        localStorage.setItem('refreshToken', action.payload.refreshToken);
        localStorage.setItem('authUser', JSON.stringify(action.payload.user));
      }
    },
    clearCredentials: (state) => {
      state.accessToken = null;
      state.refreshToken = null;
      state.user = null;
      state.isAuthenticated = false;

      if (typeof window !== 'undefined') {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('authUser');
      }
    },
  },
});

export const { setCredentials, clearCredentials } = authSlice.actions;
export default authSlice.reducer;
