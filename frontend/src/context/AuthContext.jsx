import React, { createContext, useState, useEffect, useContext } from 'react';
import API from '../api/client';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem('token') || null);
  const [user, setUser] = useState(localStorage.getItem('username') || null);
  const [roles, setRoles] = useState(() => {
    const saved = localStorage.getItem('roles');
    return saved ? JSON.parse(saved) : [];
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Synchronize initial state with localStorage
    const savedToken = localStorage.getItem('token');
    const savedUser = localStorage.getItem('username');
    const savedRoles = localStorage.getItem('roles');

    if (savedToken && savedUser && savedRoles) {
      setToken(savedToken);
      setUser(savedUser);
      setRoles(JSON.parse(savedRoles));
    }
    setLoading(false);
  }, []);

  const login = async (username, password) => {
    try {
      const response = await API.post('/api/auth/login', { username, password });
      const { token: jwt, username: userObj, roles: userRoles } = response.data;

      localStorage.setItem('token', jwt);
      localStorage.setItem('username', userObj);
      localStorage.setItem('roles', JSON.stringify(userRoles));

      setToken(jwt);
      setUser(userObj);
      setRoles(userRoles);
      return { success: true };
    } catch (error) {
      console.error("Login failed:", error);
      const message = error.response?.data?.message || 'Invalid username or password';
      return { success: false, error: message };
    }
  };

  const register = async (username, password, rolesList) => {
    try {
      await API.post('/api/auth/register', {
        username,
        password,
        roles: rolesList
      });
      return { success: true };
    } catch (error) {
      console.error("Registration failed:", error);
      const message = error.response?.data?.message || 'Registration failed';
      return { success: false, error: message };
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('roles');
    setToken(null);
    setUser(null);
    setRoles([]);
  };

  const hasRole = (role) => roles.includes(role);
  const isAdmin = () => hasRole('ROLE_ADMIN');
  const isAuthenticated = () => !!token;

  return (
    <AuthContext.Provider value={{ token, user, roles, loading, login, register, logout, hasRole, isAdmin, isAuthenticated }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
export default AuthContext;
