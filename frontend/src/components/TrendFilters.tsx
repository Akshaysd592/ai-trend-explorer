'use client';

import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '@/store/store';
import {
  setSource,
  setSearchKeyword,
  setLanguage,
  setSortBy,
  toggleSortDir,
  setViewMode,
} from '@/store/uiSlice';
import { SourceType } from '@/types/trend';
import { Search, Grid, List, ArrowUpDown, Filter } from 'lucide-react';

export const TrendFilters: React.FC = () => {
  const dispatch = useDispatch();
  const { selectedSource, searchKeyword, selectedLanguage, sortBy, sortDir, viewMode } =
    useSelector((state: RootState) => state.ui);

  const sources: { label: string; value: SourceType | 'ALL'; color: string }[] = [
    { label: 'All Sources', value: 'ALL', color: 'hover:border-slate-500' },
    { label: 'GitHub', value: 'GITHUB', color: 'hover:border-cyan-500' },
    { label: 'Hugging Face', value: 'HUGGING_FACE', color: 'hover:border-yellow-500' },
  ];

  return (
    <div className="bg-slate-900 border-b border-slate-800 py-6 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto space-y-4">
        {/* Top Row: Source Tabs & Search */}
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          {/* Source Tabs */}
          <div className="flex flex-wrap items-center gap-2">
            {sources.map((src) => {
              const isActive = selectedSource === src.value;
              return (
                <button
                  key={src.value}
                  onClick={() => dispatch(setSource(src.value))}
                  className={`px-4 py-2 rounded-xl font-medium text-sm transition-all duration-200 border ${
                    isActive
                      ? 'bg-cyan-500 text-slate-950 font-semibold border-cyan-400 shadow-lg shadow-cyan-500/25'
                      : 'bg-slate-800/80 text-slate-300 border-slate-700 hover:bg-slate-800'
                  }`}
                >
                  {src.label}
                </button>
              );
            })}
          </div>

          {/* Search Bar */}
          <div className="relative flex-1 max-w-md">
            <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
            <input
              type="text"
              placeholder="Search trends by title, keyword or AI category..."
              value={searchKeyword}
              onChange={(e) => dispatch(setSearchKeyword(e.target.value))}
              className="w-full pl-10 pr-4 py-2 rounded-xl bg-slate-800 border border-slate-700 text-slate-100 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-cyan-500/50 focus:border-cyan-500 text-sm"
            />
          </div>
        </div>

        {/* Bottom Row: Language Filter, Sort Options, View Mode */}
        <div className="flex flex-wrap items-center justify-between gap-4 pt-2 border-t border-slate-800/80 text-sm">
          <div className="flex items-center space-x-3">
            <Filter className="h-4 w-4 text-slate-400" />
            <select
              value={selectedLanguage}
              onChange={(e) => dispatch(setLanguage(e.target.value))}
              className="bg-slate-800 border border-slate-700 text-slate-200 rounded-lg px-3 py-1.5 focus:outline-none focus:border-cyan-500"
            >
              <option value="">All Languages</option>
              <option value="Python">Python</option>
              <option value="Go">Go</option>
              <option value="TypeScript">TypeScript</option>
              <option value="Java">Java</option>
              <option value="C++">C++</option>
              <option value="Rust">Rust</option>
            </select>

            <select
              value={sortBy}
              onChange={(e) => dispatch(setSortBy(e.target.value))}
              className="bg-slate-800 border border-slate-700 text-slate-200 rounded-lg px-3 py-1.5 focus:outline-none focus:border-cyan-500"
            >
              <option value="stars">Sort by Stars</option>
              <option value="score">Sort by AI Score</option>
              <option value="created">Sort by Date</option>
              <option value="title">Sort by Name</option>
            </select>

            <button
              onClick={() => dispatch(toggleSortDir())}
              className="flex items-center space-x-1 bg-slate-800 hover:bg-slate-700 border border-slate-700 px-3 py-1.5 rounded-lg text-slate-300 transition-colors"
              title="Toggle sort direction"
            >
              <ArrowUpDown className="h-3.5 w-3.5" />
              <span className="uppercase text-xs font-semibold">{sortDir}</span>
            </button>
          </div>

          {/* View Mode Switcher */}
          <div className="flex items-center space-x-1 bg-slate-800 p-1 rounded-lg border border-slate-700">
            <button
              onClick={() => dispatch(setViewMode('grid'))}
              className={`p-1.5 rounded ${
                viewMode === 'grid' ? 'bg-cyan-500 text-slate-950' : 'text-slate-400 hover:text-white'
              }`}
              title="Grid View"
            >
              <Grid className="h-4 w-4" />
            </button>
            <button
              onClick={() => dispatch(setViewMode('list'))}
              className={`p-1.5 rounded ${
                viewMode === 'list' ? 'bg-cyan-500 text-slate-950' : 'text-slate-400 hover:text-white'
              }`}
              title="List View"
            >
              <List className="h-4 w-4" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
