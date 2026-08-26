'use client';

import React from 'react';
import { useRouter } from 'next/navigation';
import { Trend } from '@/types/trend';
import { Star, GitFork, ExternalLink, Sparkles, Code, Cpu } from 'lucide-react';

interface TrendCardProps {
  trend: Trend;
  viewMode: 'grid' | 'list';
}

export const TrendCard: React.FC<TrendCardProps> = ({ trend, viewMode }) => {
  const router = useRouter();

  const getSourceBadge = () => {
    switch (trend.source) {
      case 'GITHUB':
        return (
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-cyan-950 text-cyan-400 border border-cyan-800/60">
            GitHub
          </span>
        );
      case 'HUGGING_FACE':
        return (
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-amber-950 text-yellow-400 border border-amber-800/60">
            Hugging Face
          </span>
        );
      default:
        return (
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-indigo-950 text-indigo-400 border border-indigo-800/60">
            {trend.source}
          </span>
        );
    }
  };

  const handleCardClick = () => {
    router.push(`/trends/${trend.id}`);
  };

  if (viewMode === 'list') {
    return (
      <div
        onClick={handleCardClick}
        role="link"
        tabIndex={0}
        onKeyDown={(e) => {
          if (e.key === 'Enter' || e.key === ' ') {
            e.preventDefault();
            handleCardClick();
          }
        }}
        className="bg-slate-900/90 border border-slate-800 hover:border-cyan-500/60 rounded-xl p-5 transition-all duration-200 hover:shadow-lg hover:shadow-cyan-500/5 flex flex-col md:flex-row items-start md:items-center justify-between gap-4 cursor-pointer group select-none"
      >
        <div className="space-y-2 flex-1">
          <div className="flex items-center space-x-3">
            {getSourceBadge()}
            <h3 className="text-lg font-bold text-slate-100 group-hover:text-cyan-400 transition-colors">
              {trend.title}
            </h3>
            {trend.language && (
              <span className="text-xs text-slate-400 bg-slate-800 px-2 py-0.5 rounded">
                {trend.language}
              </span>
            )}
          </div>
          <p className="text-sm text-slate-400 line-clamp-2">{trend.description}</p>
          
          {trend.aiSummary && (
            <div className="flex items-center space-x-2 text-xs text-purple-300 bg-purple-950/40 border border-purple-800/40 rounded-lg px-3 py-1.5 mt-2">
              <Sparkles className="h-3.5 w-3.5 text-purple-400 shrink-0" />
              <span className="italic">{trend.aiSummary}</span>
            </div>
          )}
        </div>

        <div className="flex items-center space-x-6 shrink-0 text-sm">
          <div className="flex items-center space-x-4 text-slate-400">
            <span className="flex items-center space-x-1">
              <Star className="h-4 w-4 text-amber-400 fill-amber-400" />
              <span className="font-semibold text-slate-200">{trend.stars?.toLocaleString()}</span>
            </span>
            {trend.forks !== undefined && (
              <span className="flex items-center space-x-1">
                <GitFork className="h-4 w-4 text-slate-400" />
                <span>{trend.forks?.toLocaleString()}</span>
              </span>
            )}
            <span className="flex items-center space-x-1 text-cyan-400 font-bold bg-cyan-950/60 px-2.5 py-1 rounded-lg border border-cyan-800/60">
              <Cpu className="h-3.5 w-3.5" />
              <span>{trend.trendScore?.toFixed(1)}</span>
            </span>
          </div>

          <a
            href={trend.repositoryUrl}
            target="_blank"
            rel="noopener noreferrer"
            onClick={(e) => e.stopPropagation()}
            className="p-2 bg-slate-800 hover:bg-cyan-500 hover:text-slate-950 text-cyan-400 rounded-lg transition-all border border-slate-700"
            title="Open Original Repository in New Tab"
          >
            <ExternalLink className="h-4 w-4" />
          </a>
        </div>
      </div>
    );
  }

  return (
    <div
      onClick={handleCardClick}
      role="link"
      tabIndex={0}
      onKeyDown={(e) => {
        if (e.key === 'Enter' || e.key === ' ') {
          e.preventDefault();
          handleCardClick();
        }
      }}
      className="bg-slate-900/90 border border-slate-800 hover:border-cyan-500/60 rounded-2xl p-6 transition-all duration-300 hover:shadow-xl hover:shadow-cyan-500/10 hover:-translate-y-0.5 flex flex-col justify-between space-y-4 group cursor-pointer select-none"
    >
      <div className="space-y-3">
        {/* Top Badges & Trend Score */}
        <div className="flex items-center justify-between">
          {getSourceBadge()}
          <span className="inline-flex items-center space-x-1 text-xs font-bold text-cyan-400 bg-cyan-950/80 px-2.5 py-1 rounded-full border border-cyan-800/60">
            <Cpu className="h-3 w-3" />
            <span>Score: {trend.trendScore?.toFixed(1)}</span>
          </span>
        </div>

        {/* Title */}
        <h3 className="text-xl font-bold text-slate-100 group-hover:text-cyan-400 transition-colors line-clamp-1">
          {trend.title}
        </h3>

        {/* Description */}
        <p className="text-sm text-slate-400 line-clamp-3 leading-relaxed">
          {trend.description}
        </p>

        {/* AI Insight Badge */}
        {trend.aiCategory && (
          <div className="bg-purple-950/40 border border-purple-800/40 rounded-xl p-3 space-y-1">
            <div className="flex items-center space-x-1.5 text-xs font-semibold text-purple-300">
              <Sparkles className="h-3.5 w-3.5 text-purple-400" />
              <span>AI Category: {trend.aiCategory}</span>
            </div>
            {trend.aiSummary && (
              <p className="text-xs text-slate-400 italic line-clamp-2">{trend.aiSummary}</p>
            )}
          </div>
        )}

        {/* Topics Tags */}
        {trend.topics && trend.topics.length > 0 && (
          <div className="flex flex-wrap gap-1.5 pt-1">
            {trend.topics.slice(0, 4).map((topic, i) => (
              <span
                key={i}
                className="text-xs px-2 py-0.5 rounded-md bg-slate-800/90 text-slate-300 border border-slate-700/60"
              >
                #{topic}
              </span>
            ))}
          </div>
        )}
      </div>

      {/* Card Footer Metrics & Direct Source Link */}
      <div className="pt-4 border-t border-slate-800/80 flex items-center justify-between text-xs text-slate-400">
        <div className="flex items-center space-x-4">
          <span className="flex items-center space-x-1">
            <Star className="h-4 w-4 text-amber-400 fill-amber-400" />
            <span className="font-semibold text-slate-200">{trend.stars?.toLocaleString()}</span>
          </span>
          {trend.forks !== undefined && (
            <span className="flex items-center space-x-1">
              <GitFork className="h-3.5 w-3.5" />
              <span>{trend.forks?.toLocaleString()}</span>
            </span>
          )}
          {trend.language && (
            <span className="flex items-center space-x-1 text-slate-300">
              <Code className="h-3.5 w-3.5" />
              <span>{trend.language}</span>
            </span>
          )}
        </div>

        <a
          href={trend.repositoryUrl}
          target="_blank"
          rel="noopener noreferrer"
          onClick={(e) => e.stopPropagation()}
          className="inline-flex items-center space-x-1 px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-cyan-500 hover:text-slate-950 text-cyan-400 font-semibold transition-all duration-200 border border-slate-700"
          title="Open Original Repository in New Tab"
        >
          <span>Source</span>
          <ExternalLink className="h-3.5 w-3.5" />
        </a>
      </div>
    </div>
  );
};
