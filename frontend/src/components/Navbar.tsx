'use client';

import React from 'react';
import { Sparkles, Cpu, Layers, Github } from 'lucide-react';

export const Navbar: React.FC = () => {
  return (
    <header className="sticky top-0 z-50 backdrop-blur-md bg-slate-900/80 border-b border-slate-800 text-slate-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Brand Logo */}
        <div className="flex items-center space-x-3">
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
        </div>

        {/* Status Indicators & Links */}
        <div className="flex items-center space-x-4">
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

          <a
            href="https://github.com"
            target="_blank"
            rel="noopener noreferrer"
            className="p-2 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white transition-colors"
            title="GitHub Repository"
          >
            <Github className="h-5 w-5" />
          </a>
        </div>
      </div>
    </header>
  );
};
