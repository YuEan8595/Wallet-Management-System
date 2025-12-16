import React, { createContext, useContext, useState } from 'react';

type AuthContextType = {
  walletId: number | null;
  authHeader: string | null;
  login: (walletId: number) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType>(null!);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [walletId, setWalletId] = useState<number | null>(null);
  const [authHeader, setAuthHeader] = useState<string | null>(null);

  const login = (id: number) => {
    const token = btoa(`${id}:${id}`);
    setWalletId(id);
    setAuthHeader(`Basic ${token}`);
  };

  const logout = () => {
    setWalletId(null);
    setAuthHeader(null);
  };

  return (
    <AuthContext.Provider value={{ walletId, authHeader, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);