'use client';

import React from 'react';
import Link from 'next/link';
import { useSelector } from 'react-redux';
import { RootState } from '@/store/store';
import { useTrends } from '@/services/trendApi';
import { HeroSection } from '@/components/HeroSection';
import { TrendFilters } from '@/components/TrendFilters';
import { TrendCard } from '@/components/TrendCard';
import { Pagination } from '@/components/Pagination';
import {
  Sparkles,
  AlertTriangle,
  RefreshCw,
  LogIn,
  UserPlus,
  TrendingUp,
  Bot,
  Zap,
  Lock,
  ArrowRight,
  Database,
} from 'lucide-react';

export default function Home() {
  const { isAuthenticated } = useSelector((state: RootState) => state.auth);

  const { selectedSource, searchKeyword, selectedLanguage, sortBy, sortDir, page, viewMode } =
    useSelector((state: RootState) => state.ui);

  const { data, isLoading, isError, error, refetch, isFetching } = useTrends(
    {
      source: selectedSource,
      language: selectedLanguage,
      q: searchKeyword,
      page,
      size: 9,
      sortBy,
      sortDir,
    },
    { enabled: isAuthenticated } // Only execute query when logged in
  );

  const trendList = data?.content || [];

  // ============================================================================
  // GUEST LANDING PAGE (UNAUTHENTICATED)
  // ============================================================================
  if (!isAuthenticated) {
    return (
      <div className="min-h-screen flex flex-col bg-slate-950 text-slate-100">
        <HeroSection />

        <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-12 space-y-12">
          {/* Main Call to Action Container */}
          <div className="relative overflow-hidden rounded-3xl bg-gradient-to-b from-slate-900 via-slate-900/90 to-slate-950 border border-slate-800 p-8 sm:p-12 text-center shadow-2xl">
            <div className="absolute inset-0 bg-cyan-500/5 backdrop-blur-3xl pointer-events-none" />
            
            <div className="max-w-2xl mx-auto space-y-6 relative z-10">
              <div className="inline-flex items-center space-x-2 px-4 py-1.5 rounded-full bg-cyan-500/10 border border-cyan-500/30 text-cyan-400 text-xs font-semibold uppercase tracking-wider">
                <Lock className="h-3.5 w-3.5" />
                <span>Authentication Required</span>
              </div>

              <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-white">
                Unlock Live AI Trend Intelligence
              </h2>

              <p className="text-slate-300 text-base leading-relaxed">
                Please sign in or create an account to explore the real-time AI repository index, apply advanced filters, view detailed AI scores, and trigger live data syncs.
              </p>

              {/* Action Buttons */}
              <div className="pt-4 flex flex-col sm:flex-row items-center justify-center gap-4">
                <Link
                  href="/login"
                  className="w-full sm:w-auto inline-flex items-center justify-center space-x-2 px-6 py-3.5 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-600 hover:from-cyan-400 hover:to-blue-500 text-white font-semibold text-sm transition-all shadow-lg shadow-cyan-500/25 hover:shadow-cyan-500/40"
                >
                  <LogIn className="h-4 w-4" />
                  <span>Sign In to Access</span>
                  <ArrowRight className="h-4 w-4" />
                </Link>

                <Link
                  href="/register"
                  className="w-full sm:w-auto inline-flex items-center justify-center space-x-2 px-6 py-3.5 rounded-xl bg-slate-800 hover:bg-slate-700 border border-slate-700 text-slate-200 hover:text-white font-medium text-sm transition-colors"
                >
                  <UserPlus className="h-4 w-4 text-cyan-400" />
                  <span>Create Free Account</span>
                </Link>
              </div>
            </div>
          </div>

          {/* Feature Showcase Grid */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-6 space-y-3">
              <div className="h-10 w-10 rounded-xl bg-cyan-500/10 border border-cyan-500/30 flex items-center justify-center text-cyan-400">
                <TrendingUp className="h-5 w-5" />
              </div>
              <h3 className="text-lg font-bold text-slate-100">Multi-Source Ingestion</h3>
              <p className="text-sm text-slate-400 leading-relaxed">
                Automatically aggregates trending repositories from GitHub and open-source models from Hugging Face.
              </p>
            </div>

            <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-6 space-y-3">
              <div className="h-10 w-10 rounded-xl bg-purple-500/10 border border-purple-500/30 flex items-center justify-center text-purple-400">
                <Bot className="h-5 w-5" />
              </div>
              <h3 className="text-lg font-bold text-slate-100">AI-Powered Analytics</h3>
              <p className="text-sm text-slate-400 leading-relaxed">
                Evaluates codebases using Gemini AI to extract key insights, tech stack summaries, and trend scores.
              </p>
            </div>

            <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-6 space-y-3">
              <div className="h-10 w-10 rounded-xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center text-emerald-400">
                <Zap className="h-5 w-5" />
              </div>
              <h3 className="text-lg font-bold text-slate-100">Instant Filters & Search</h3>
              <p className="text-sm text-slate-400 leading-relaxed">
                Filter by programming language, star counts, model downloads, or topic keywords in real time.
              </p>
            </div>
          </div>
        </main>
      </div>
    );
  }

  // ============================================================================
  // AUTHENTICATED DASHBOARD VIEW
  // ============================================================================
  return (
    <div className="min-h-screen flex flex-col bg-slate-950">
      <HeroSection />
      <TrendFilters />

      {/* Main Content Grid */}
      <section className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
        {/* Active Refreshing Indicator */}
        <div className="flex justify-between items-center text-xs text-slate-400">
          <span>
            {trendList.length > 0
              ? `Displaying ${trendList.length} AI trends`
              : 'Searching repository...'}
          </span>
          <button
            onClick={() => refetch()}
            disabled={isFetching}
            className="flex items-center space-x-1 px-3 py-1 rounded-lg bg-slate-800 hover:bg-slate-700 text-cyan-400 disabled:opacity-50 transition-colors border border-slate-700"
          >
            <RefreshCw className={`h-3.5 w-3.5 ${isFetching ? 'animate-spin' : ''}`} />
            <span>{isFetching ? 'Refreshing...' : 'Refresh Data'}</span>
          </button>
        </div>

        {/* Loading Skeletons */}
        {isLoading && (
          <div
            className={
              viewMode === 'grid'
                ? 'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6'
                : 'space-y-4'
            }
          >
            {Array.from({ length: 6 }).map((_, i) => (
              <div
                key={i}
                className="bg-slate-900 border border-slate-800 rounded-2xl p-6 h-64 animate-pulse space-y-4"
              >
                <div className="flex justify-between items-center">
                  <div className="h-5 w-24 bg-slate-800 rounded-full" />
                  <div className="h-5 w-20 bg-slate-800 rounded-full" />
                </div>
                <div className="h-6 w-3/4 bg-slate-800 rounded" />
                <div className="space-y-2">
                  <div className="h-4 w-full bg-slate-800/60 rounded" />
                  <div className="h-4 w-5/6 bg-slate-800/60 rounded" />
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Error State */}
        {isError && (
          <div className="bg-red-950/30 border border-red-900/50 rounded-2xl p-8 text-center space-y-4 max-w-lg mx-auto">
            <AlertTriangle className="h-10 w-10 text-red-400 mx-auto" />
            <div>
              <h3 className="text-lg font-bold text-red-200">Unable to Connect to API Gateway</h3>
              <p className="text-sm text-red-300/80 mt-1">
                {(error as Error)?.message || 'The API Gateway is temporarily unreachable. Please try again later.'}
              </p>
            </div>
            <button
              onClick={() => refetch()}
              className="inline-flex items-center space-x-2 px-4 py-2 bg-red-900/50 hover:bg-red-800/60 border border-red-700/60 rounded-xl text-xs font-semibold text-red-100 transition-colors"
            >
              <RefreshCw className="h-3.5 w-3.5" />
              <span>Retry Request</span>
            </button>
          </div>
        )}

        {/* Empty State */}
        {!isLoading && !isError && trendList.length === 0 && (
          <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-12 text-center space-y-3 max-w-md mx-auto">
            <Sparkles className="h-10 w-10 text-cyan-400 mx-auto" />
            <h3 className="text-lg font-bold text-slate-200">No AI Trends Found</h3>
            <p className="text-sm text-slate-400">
              No trends matched your selected filters or search query. Try resetting your search filters.
            </p>
          </div>
        )}

        {/* Results Grid / List */}
        {!isLoading && !isError && trendList.length > 0 && (
          <div className="space-y-8">
            <div
              className={
                viewMode === 'grid'
                  ? 'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6'
                  : 'space-y-4'
              }
            >
              {trendList.map((trend) => (
                <TrendCard key={trend.id} trend={trend} viewMode={viewMode || 'grid'} />
              ))}
            </div>

            {/* Pagination Controls */}
            {data && (
              <Pagination
                totalPages={data.totalPages}
                totalElements={data.totalElements}
                isLast={data.last}
              />
            )}
          </div>
        )}
      </section>
    </div>
  );
}
