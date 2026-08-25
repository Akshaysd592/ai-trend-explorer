'use client';

import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { RootState } from '@/store/store';
import { clearCredentials } from '@/store/authSlice';
import { Sparkles, Cpu, Layers, Github, LogIn, UserPlus, LogOut, User } from 'lucide-react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';

export const Navbar: React.FC = () => {
  const dispatch = useDispatch();
  const router = useRouter();
  const { isAuthenticated, user } = useSelector((state: RootState) => state.auth);

  const handleLogout = () => {
    dispatch(clearCredentials());
    router.push('/');
  };

  return (
    <header className="sticky top-0 z-50 backdrop-blur-md bg-slate-900/80 border-b border-slate-800 text-slate-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Brand Logo */}
        <Link href="/" className="flex items-center space-x-3">
          <div className="p-2 bg-gradient-to-tr from-cyan-500 to-blue-600 rounded-xl shadow-lg shadow-cyan-500/20">
            <Sparkles className="h-6 w-6 text-white" />
          </div>
          <div>
            <span className="font-bold text-xl tracking-tight bg-gradient-to-r from-white via-cyan-200 to-cyan-400 bg-clip-text text-transparent">
              AI Trend Explorer
            </span>
            <span className="ml-2 text-xs font-semibold px-2 py-0.5 rounded-full bg-cyan-950 text-cyan-400 border border-cyan-800/50">
              v1.0 Microservices
            </span>
          </div>
        </Link>

        {/* Right side */}
        <div className="flex items-center space-x-4">
          {/* Status Indicators */}
          <div className="hidden md:flex items-center space-x-3 text-xs text-slate-400">
            <span className="inline-flex items-center space-x-1 px-2.5 py-1 rounded-md bg-slate-800/80 border border-slate-700">
              <Cpu className="h-3.5 w-3.5 text-emerald-400 mr-1" />
              <span>Gateway :8080</span>
            </span>
            <span className="inline-flex items-center space-x-1 px-2.5 py-1 rounded-md bg-slate-800/80 border border-slate-700">
              <Layers className="h-3.5 w-3.5 text-blue-400 mr-1" />
              <span>Hexagonal Arch</span>
            </span>
          </div>

          {/* GitHub Link */}
          <a
            href="https://github.com/Akshaysd592/ai-trend-explorer"
            target="_blank"
            rel="noopener noreferrer"
            className="p-2 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white transition-colors"
            title="GitHub Repository"
          >
            <Github className="h-5 w-5" />
          </a>

          {/* Auth Buttons */}
          {isAuthenticated && user ? (
            <div className="flex items-center space-x-2">
              {/* User Avatar */}
              <div className="hidden sm:flex items-center space-x-2 px-3 py-1.5 rounded-lg bg-slate-800 border border-slate-700">
                <div className="h-6 w-6 rounded-full bg-gradient-to-tr from-cyan-500 to-blue-600 flex items-center justify-center">
                  <User className="h-3.5 w-3.5 text-white" />
                </div>
                <span className="text-xs font-medium text-slate-200">
                  {user.firstName} {user.lastName}
                </span>
              </div>
              {/* Logout */}
              <button
                onClick={handleLogout}
                className="flex items-center space-x-1.5 px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-red-900/40 border border-slate-700 hover:border-red-700/50 text-slate-300 hover:text-red-300 text-xs font-medium transition-colors"
                title="Logout"
              >
                <LogOut className="h-3.5 w-3.5" />
                <span className="hidden sm:inline">Logout</span>
              </button>
            </div>
          ) : (
            <div className="flex items-center space-x-2">
              <Link
                href="/login"
                className="flex items-center space-x-1.5 px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 border border-slate-700 text-slate-300 hover:text-white text-xs font-medium transition-colors"
              >
                <LogIn className="h-3.5 w-3.5" />
                <span>Login</span>
              </Link>
              <Link
                href="/register"
                className="flex items-center space-x-1.5 px-3 py-1.5 rounded-lg bg-gradient-to-r from-cyan-500 to-blue-600 hover:from-cyan-400 hover:to-blue-500 text-white text-xs font-semibold transition-all shadow-lg shadow-cyan-500/20"
              >
                <UserPlus className="h-3.5 w-3.5" />
                <span>Sign Up</span>
              </Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
