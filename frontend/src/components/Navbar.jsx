import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Car, LogOut, LayoutDashboard, Shield, User } from 'lucide-react';

const Navbar = () => {
  const { user, logout, isAuthenticated, isAdmin } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  if (!isAuthenticated()) return null;

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path;

  return (
    <nav className="sticky top-0 z-50 backdrop-blur-md bg-slate-900/80 border-b border-slate-800 px-6 py-4 shadow-lg">
      <div className="max-w-7xl mx-auto flex items-center justify-between">
        {/* Brand/Logo */}
        <Link to="/dashboard" className="flex items-center space-x-2 group">
          <div className="bg-violet-600 p-2 rounded-lg group-hover:bg-violet-500 transition-colors duration-300">
            <Car className="h-6 w-6 text-white" />
          </div>
          <span className="text-xl font-bold tracking-tight bg-gradient-to-r from-violet-400 to-indigo-200 bg-clip-text text-transparent">
            AutoHaven
          </span>
        </Link>

        {/* Navigation Tabs */}
        <div className="flex items-center space-x-1">
          <Link
            to="/dashboard"
            className={`flex items-center space-x-1 px-4 py-2 rounded-lg text-sm font-medium transition-all duration-300 ${
              isActive('/dashboard')
                ? 'bg-violet-600/10 text-violet-400 border border-violet-500/20'
                : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/50 border border-transparent'
            }`}
          >
            <LayoutDashboard className="h-4 w-4" />
            <span>Dashboard</span>
          </Link>

          {isAdmin() && (
            <Link
              to="/admin"
              className={`flex items-center space-x-1 px-4 py-2 rounded-lg text-sm font-medium transition-all duration-300 ${
                isActive('/admin')
                  ? 'bg-violet-600/10 text-violet-400 border border-violet-500/20'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/50 border border-transparent'
              }`}
            >
              <Shield className="h-4 w-4" />
              <span>Admin Panel</span>
            </Link>
          )}
        </div>

        {/* Profile and Action Buttons */}
        <div className="flex items-center space-x-4">
          <div className="hidden sm:flex items-center space-x-2 bg-slate-800/80 px-3 py-1.5 rounded-full border border-slate-700">
            <div className="bg-slate-700 p-1 rounded-full">
              <User className="h-3.5 w-3.5 text-slate-300" />
            </div>
            <span className="text-xs font-semibold text-slate-300 max-w-[120px] truncate">
              {user}
            </span>
            <span className={`text-[10px] uppercase font-bold tracking-wider px-2 py-0.5 rounded-full ${
              isAdmin() ? 'bg-rose-500/15 text-rose-400' : 'bg-emerald-500/15 text-emerald-400'
            }`}>
              {isAdmin() ? 'Admin' : 'User'}
            </span>
          </div>

          <button
            onClick={handleLogout}
            className="flex items-center space-x-1 text-slate-400 hover:text-rose-400 hover:bg-rose-500/10 px-3 py-2 rounded-lg transition-all duration-300 border border-transparent hover:border-rose-500/20 text-sm font-medium cursor-pointer"
          >
            <LogOut className="h-4 w-4" />
            <span className="hidden md:inline">Log Out</span>
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
