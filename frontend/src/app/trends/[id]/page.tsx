'use client';

import React, { use } from 'react';
import Link from 'next/link';
import { useTrend, useAnalyzeTrend } from '@/services/trendApi';
import {
  ArrowLeft,
  Star,
  GitFork,
  ExternalLink,
  Sparkles,
  Code,
  Cpu,
  Calendar,
  Layers,
  CheckCircle2,
  RefreshCw,
  AlertTriangle,
  Flame,
  Brain,
  Share2,
} from 'lucide-react';

interface TrendDetailsPageProps {
  params: Promise<{ id: string }>;
}

export default function TrendDetailsPage({ params }: TrendDetailsPageProps) {
  const resolvedParams = use(params);
  const trendId = resolvedParams.id;

  const { data: trend, isLoading, isError, error, refetch } = useTrend(trendId);
  const { mutate: runAnalysis, isPending: isAnalyzing } = useAnalyzeTrend();

  const [copied, setCopied] = React.useState(false);

  const handleShare = () => {
    if (typeof window !== 'undefined') {
      navigator.clipboard.writeText(window.location.href);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  const getSourceBadge = () => {
    if (!trend) return null;
    switch (trend.source) {
      case 'GITHUB':
        return (
          <span className="px-3 py-1 rounded-full text-xs font-semibold bg-cyan-950 text-cyan-400 border border-cyan-800/60 inline-flex items-center space-x-1.5">
            <span className="h-2 w-2 rounded-full bg-cyan-400 animate-pulse" />
            <span>GitHub Repository</span>
          </span>
        );
      case 'HUGGING_FACE':
        return (
          <span className="px-3 py-1 rounded-full text-xs font-semibold bg-amber-950 text-yellow-400 border border-amber-800/60 inline-flex items-center space-x-1.5">
            <span className="h-2 w-2 rounded-full bg-yellow-400 animate-pulse" />
            <span>Hugging Face Model</span>
          </span>
        );
      default:
        return (
          <span className="px-3 py-1 rounded-full text-xs font-semibold bg-indigo-950 text-indigo-400 border border-indigo-800/60">
            {trend.source}
          </span>
        );
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-slate-950 text-slate-100 py-12 px-4 sm:px-6 lg:px-8 max-w-5xl mx-auto space-y-8 animate-pulse">
        <div className="h-6 w-36 bg-slate-800 rounded" />
        <div className="space-y-4">
          <div className="h-10 w-3/4 bg-slate-800 rounded-lg" />
          <div className="h-5 w-1/2 bg-slate-800/60 rounded" />
        </div>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          {Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="h-24 bg-slate-900 border border-slate-800 rounded-xl" />
          ))}
        </div>
        <div className="h-64 bg-slate-900 border border-slate-800 rounded-2xl" />
      </div>
    );
  }

  if (isError || !trend) {
    return (
      <div className="min-h-screen bg-slate-950 text-slate-100 py-16 px-4 max-w-lg mx-auto text-center space-y-6">
        <div className="p-4 bg-red-950/40 border border-red-900/60 rounded-2xl space-y-4">
          <AlertTriangle className="h-12 w-12 text-red-400 mx-auto" />
          <h2 className="text-xl font-bold text-slate-100">Trend Not Found</h2>
          <p className="text-sm text-slate-400">
            {(error as Error)?.message || `Could not find details for Trend ID #${trendId}.`}
          </p>
          <div className="pt-2 flex justify-center space-x-4">
            <Link
              href="/"
              className="inline-flex items-center space-x-1.5 px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-xl text-xs font-semibold transition-colors border border-slate-700"
            >
              <ArrowLeft className="h-3.5 w-3.5" />
              <span>Back to Dashboard</span>
            </Link>
            <button
              onClick={() => refetch()}
              className="inline-flex items-center space-x-1.5 px-4 py-2 bg-cyan-600 hover:bg-cyan-500 text-slate-950 rounded-xl text-xs font-bold transition-colors"
            >
              <RefreshCw className="h-3.5 w-3.5" />
              <span>Retry</span>
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 py-10 px-4 sm:px-6 lg:px-8">
      <div className="max-w-5xl mx-auto space-y-8">
        {/* Navigation & Breadcrumb */}
        <div className="flex items-center justify-between">
          <Link
            href="/"
            className="inline-flex items-center space-x-2 text-sm font-medium text-slate-400 hover:text-cyan-400 transition-colors group"
          >
            <ArrowLeft className="h-4 w-4 group-hover:-translate-x-1 transition-transform" />
            <span>Back to Trends Dashboard</span>
          </Link>

          <button
            onClick={handleShare}
            className="inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-lg bg-slate-900 hover:bg-slate-800 text-slate-400 hover:text-slate-200 border border-slate-800 text-xs transition-colors"
            title="Copy URL to clipboard"
          >
            {copied ? (
              <>
                <CheckCircle2 className="h-3.5 w-3.5 text-green-400" />
                <span className="text-green-400 font-semibold">Copied Link!</span>
              </>
            ) : (
              <>
                <Share2 className="h-3.5 w-3.5" />
                <span>Share</span>
              </>
            )}
          </button>
        </div>

        {/* Header Hero Card */}
        <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-8 space-y-6 shadow-2xl relative overflow-hidden backdrop-blur-sm">
          <div className="absolute top-0 right-0 -mt-8 -mr-8 w-64 h-64 bg-cyan-500/10 rounded-full blur-3xl pointer-events-none" />
          <div className="absolute bottom-0 left-1/3 -mb-8 w-48 h-48 bg-purple-500/10 rounded-full blur-3xl pointer-events-none" />

          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div className="space-y-2">
              <div className="flex items-center space-x-3">
                {getSourceBadge()}
                {trend.language && (
                  <span className="px-2.5 py-1 rounded-full text-xs font-medium bg-slate-800 text-slate-300 border border-slate-700 flex items-center space-x-1">
                    <Code className="h-3 w-3 text-cyan-400" />
                    <span>{trend.language}</span>
                  </span>
                )}
              </div>
              <h1 className="text-3xl sm:text-4xl font-extrabold text-slate-100 tracking-tight">
                {trend.title}
              </h1>
            </div>

            {/* Main Action Buttons */}
            <div className="flex items-center space-x-3 shrink-0">
              <a
                href={trend.repositoryUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="inline-flex items-center space-x-2 px-5 py-2.5 rounded-xl bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-bold transition-all shadow-lg shadow-cyan-500/20 hover:shadow-cyan-500/30 text-sm"
              >
                <span>Visit {trend.source === 'HUGGING_FACE' ? 'Hugging Face' : 'GitHub'}</span>
                <ExternalLink className="h-4 w-4" />
              </a>
            </div>
          </div>

          <p className="text-base sm:text-lg text-slate-300 leading-relaxed max-w-4xl">
            {trend.description}
          </p>

          {/* Quick Metrics Bar */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 pt-4 border-t border-slate-800/80">
            <div className="bg-slate-950/60 border border-slate-800 rounded-2xl p-4 flex items-center space-x-3.5">
              <div className="p-2.5 rounded-xl bg-amber-950/50 text-amber-400 border border-amber-800/40">
                <Star className="h-5 w-5 fill-amber-400" />
              </div>
              <div>
                <p className="text-xs text-slate-400 font-medium">Stars / Likes</p>
                <p className="text-lg font-bold text-slate-100">{trend.stars?.toLocaleString() || '0'}</p>
              </div>
            </div>

            <div className="bg-slate-950/60 border border-slate-800 rounded-2xl p-4 flex items-center space-x-3.5">
              <div className="p-2.5 rounded-xl bg-slate-800 text-slate-300 border border-slate-700">
                <GitFork className="h-5 w-5" />
              </div>
              <div>
                <p className="text-xs text-slate-400 font-medium">Forks</p>
                <p className="text-lg font-bold text-slate-100">{trend.forks?.toLocaleString() || '0'}</p>
              </div>
            </div>

            <div className="bg-slate-950/60 border border-slate-800 rounded-2xl p-4 flex items-center space-x-3.5">
              <div className="p-2.5 rounded-xl bg-cyan-950/60 text-cyan-400 border border-cyan-800/60">
                <Cpu className="h-5 w-5" />
              </div>
              <div>
                <p className="text-xs text-slate-400 font-medium">Trend Score</p>
                <p className="text-lg font-bold text-cyan-400">{trend.trendScore?.toFixed(1) || '0.0'}</p>
              </div>
            </div>

            <div className="bg-slate-950/60 border border-slate-800 rounded-2xl p-4 flex items-center space-x-3.5">
              <div className="p-2.5 rounded-xl bg-purple-950/50 text-purple-400 border border-purple-800/40">
                <Flame className="h-5 w-5" />
              </div>
              <div>
                <p className="text-xs text-slate-400 font-medium">Velocity Rank</p>
                <p className="text-lg font-bold text-purple-300">
                  {trend.trendScore && trend.trendScore > 90 ? 'Top 1% 🔥' : 'High Growth'}
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* AI Insight & Analysis Deep Dive */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Left 2 Cols: AI Insights */}
          <div className="lg:col-span-2 space-y-6">
            <div className="bg-slate-900/90 border border-purple-900/40 rounded-3xl p-8 space-y-6 shadow-xl relative">
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-2.5">
                  <div className="p-2 rounded-xl bg-purple-950/70 border border-purple-800/60 text-purple-400">
                    <Brain className="h-5 w-5" />
                  </div>
                  <div>
                    <h2 className="text-xl font-bold text-slate-100">AI Intelligence & Summary</h2>
                    <p className="text-xs text-purple-300/80">Analyzed via Gemini 3.5 Flash Lite LLM</p>
                  </div>
                </div>

                <button
                  onClick={() => runAnalysis(trend.id)}
                  disabled={isAnalyzing}
                  className="inline-flex items-center space-x-1.5 px-3 py-1.5 bg-purple-950/60 hover:bg-purple-900/60 border border-purple-700/60 rounded-xl text-xs font-semibold text-purple-300 disabled:opacity-50 transition-colors"
                >
                  <RefreshCw className={`h-3.5 w-3.5 ${isAnalyzing ? 'animate-spin' : ''}`} />
                  <span>{isAnalyzing ? 'Analyzing...' : 'Re-run AI Analysis'}</span>
                </button>
              </div>

              {/* AI Category */}
              {trend.aiCategory && (
                <div className="space-y-2">
                  <span className="text-xs uppercase tracking-wider font-semibold text-slate-400">
                    AI Category Classification
                  </span>
                  <div className="inline-flex items-center space-x-2 px-4 py-2 rounded-xl bg-purple-950/60 text-purple-300 border border-purple-800/50 font-bold text-sm">
                    <Sparkles className="h-4 w-4 text-purple-400" />
                    <span>{trend.aiCategory}</span>
                  </div>
                </div>
              )}

              {/* AI Summary */}
              <div className="space-y-2">
                <span className="text-xs uppercase tracking-wider font-semibold text-slate-400">
                  Architectural Summary
                </span>
                <div className="bg-slate-950/70 border border-slate-800/90 rounded-2xl p-5 text-slate-300 leading-relaxed text-sm">
                  {trend.aiSummary ? (
                    <p className="italic">{trend.aiSummary}</p>
                  ) : (
                    <p className="text-slate-500 italic">
                      No automated summary generated yet. Click <span className="font-semibold text-purple-400 not-italic">Re-run AI Analysis</span> above to trigger a Gemini analysis.
                    </p>
                  )}
                </div>
              </div>

              {/* Topics / Tags Cloud */}
              {trend.topics && trend.topics.length > 0 && (
                <div className="space-y-3 pt-2">
                  <div className="flex items-center space-x-2 text-xs uppercase tracking-wider font-semibold text-slate-400">
                    <Layers className="h-3.5 w-3.5 text-slate-400" />
                    <span>Categorized Topics & Tags</span>
                  </div>
                  <div className="flex flex-wrap gap-2">
                    {trend.topics.map((topic, i) => (
                      <span
                        key={i}
                        className="text-xs px-3 py-1 rounded-lg bg-slate-800/90 text-slate-300 border border-slate-700/80 hover:border-cyan-500/50 transition-colors"
                      >
                        #{topic}
                      </span>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Right 1 Col: Metadata & Timestamps */}
          <div className="space-y-6">
            <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 space-y-6">
              <h3 className="text-base font-bold text-slate-100 flex items-center space-x-2">
                <Calendar className="h-4 w-4 text-cyan-400" />
                <span>Tracking Metadata</span>
              </h3>

              <div className="space-y-4 text-xs">
                <div className="flex justify-between items-center py-2 border-b border-slate-800">
                  <span className="text-slate-400">Trend ID</span>
                  <span className="font-mono font-bold text-slate-200">#{trend.id}</span>
                </div>

                <div className="flex justify-between items-center py-2 border-b border-slate-800">
                  <span className="text-slate-400">Data Source</span>
                  <span className="font-semibold text-slate-200">{trend.source}</span>
                </div>

                {trend.language && (
                  <div className="flex justify-between items-center py-2 border-b border-slate-800">
                    <span className="text-slate-400">Primary Language</span>
                    <span className="font-semibold text-slate-200">{trend.language}</span>
                  </div>
                )}

                <div className="flex justify-between items-center py-2 border-b border-slate-800">
                  <span className="text-slate-400">First Ingested</span>
                  <span className="text-slate-300">
                    {trend.createdAt ? new Date(trend.createdAt).toLocaleDateString() : 'Recent'}
                  </span>
                </div>

                <div className="flex justify-between items-center py-2">
                  <span className="text-slate-400">Last AI Sync</span>
                  <span className="text-slate-300">
                    {trend.updatedAt ? new Date(trend.updatedAt).toLocaleDateString() : 'Just now'}
                  </span>
                </div>
              </div>

              <div className="pt-2">
                <a
                  href={trend.repositoryUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="w-full inline-flex items-center justify-center space-x-2 py-2.5 px-4 rounded-xl bg-slate-800 hover:bg-slate-700 text-cyan-400 hover:text-white font-semibold text-xs transition-colors border border-slate-700"
                >
                  <span>Open External Source</span>
                  <ExternalLink className="h-3.5 w-3.5" />
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
