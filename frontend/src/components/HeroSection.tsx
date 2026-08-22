'use client';

import React from 'react';
import { TrendingUp, Bot, Database, Zap } from 'lucide-react';

export const HeroSection: React.FC = () => {
  return (
    <div className="relative overflow-hidden bg-slate-900 border-b border-slate-800 text-white py-12 px-4 sm:px-6 lg:px-8">
      <div className="absolute inset-0 bg-gradient-to-r from-cyan-500/10 via-indigo-500/5 to-purple-500/10 pointer-events-none" />
      <div className="max-w-7xl mx-auto relative z-10">
        <div className="text-center max-w-3xl mx-auto space-y-4">
          <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-cyan-500/10 border border-cyan-500/30 text-cyan-400 text-xs font-semibold uppercase tracking-wider">
            <Zap className="h-3.5 w-3.5" />
            <span>Real-time AI Trend Intelligence</span>
          </div>

          <h1 className="text-4xl sm:text-5xl font-extrabold tracking-tight bg-gradient-to-r from-white via-slate-100 to-slate-300 bg-clip-text text-transparent">
            Discover What&apos;s Next in Artificial Intelligence
          </h1>

          <p className="text-lg text-slate-400">
            Aggregated trends across <span className="text-cyan-400 font-semibold">GitHub</span> and{' '}
            <span className="text-yellow-400 font-semibold">Hugging Face</span> powered by Java 21 Hexagonal Architecture microservices.
          </p>

          {/* Quick Metrics Badges */}
          <div className="pt-4 flex flex-wrap justify-center gap-6 text-sm text-slate-300">
            <div className="flex items-center space-x-2 bg-slate-800/60 px-4 py-2 rounded-xl border border-slate-700/60">
              <TrendingUp className="h-4 w-4 text-cyan-400" />
              <span>Multi-Source Ingestion</span>
            </div>
            <div className="flex items-center space-x-2 bg-slate-800/60 px-4 py-2 rounded-xl border border-slate-700/60">
              <Bot className="h-4 w-4 text-purple-400" />
              <span>AI Insights & Categorization</span>
            </div>
            <div className="flex items-center space-x-2 bg-slate-800/60 px-4 py-2 rounded-xl border border-slate-700/60">
              <Database className="h-4 w-4 text-emerald-400" />
              <span>Liquibase & PostgreSQL</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
