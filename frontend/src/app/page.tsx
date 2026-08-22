'use client';

import React from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '@/store/store';
import { useTrends } from '@/services/trendApi';
import { HeroSection } from '@/components/HeroSection';
import { TrendFilters } from '@/components/TrendFilters';
import { TrendCard } from '@/components/TrendCard';
import { Pagination } from '@/components/Pagination';
import { Sparkles, AlertTriangle, RefreshCw } from 'lucide-react';

export default function Home() {
  const { selectedSource, searchKeyword, selectedLanguage, sortBy, sortDir, page, viewMode } =
    useSelector((state: RootState) => state.ui);

  const { data, isLoading, isError, error, refetch, isFetching } = useTrends({
    source: selectedSource,
    language: selectedLanguage,
    q: searchKeyword,
    page,
    size: 9,
    sortBy,
    sortDir,
  });

  const trendList = data?.content || [];

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
                {(error as Error)?.message || 'Make sure api-gateway (port 8080) and trend-service (port 8081) are running.'}
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
