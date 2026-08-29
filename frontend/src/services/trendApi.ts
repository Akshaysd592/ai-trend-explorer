import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { GetTrendsParams, PagedResult, Trend } from '@/types/trend';

function getApiBaseUrl(): string {
  const url = (process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080').trim();

  if (url.startsWith('http://') || url.startsWith('https://')) {
    try {
      const parsed = new URL(url);
      if (!parsed.hostname.includes('.') && parsed.hostname !== 'localhost') {
        return `https://${parsed.hostname}.onrender.com`;
      }
    } catch {
      // ignore
    }
    return url.replace(/\/$/, '');
  }

  if (!url.includes('.') && url !== 'localhost') {
    return `https://${url}.onrender.com`;
  }

  return `https://${url.replace(/\/$/, '')}`;
}

const API_BASE_URL = getApiBaseUrl();

export async function fetchTrends(params: GetTrendsParams): Promise<PagedResult<Trend>> {
  const queryParams = new URLSearchParams();

  if (params.source && params.source !== 'ALL') {
    queryParams.append('source', params.source);
  }
  if (params.language) {
    queryParams.append('language', params.language);
  }
  if (params.q) {
    queryParams.append('q', params.q);
  }
  if (params.page !== undefined) {
    queryParams.append('page', params.page.toString());
  }
  if (params.size !== undefined) {
    queryParams.append('size', params.size.toString());
  }
  if (params.sortBy) {
    queryParams.append('sortBy', params.sortBy);
  }
  if (params.sortDir) {
    queryParams.append('sortDir', params.sortDir);
  }

  const queryString = queryParams.toString();
  const url = `${API_BASE_URL}/api/v1/trends${queryString ? `?${queryString}` : ''}`;

  const response = await fetch(url, {
    method: 'GET',
    headers: { 'Content-Type': 'application/json' },
  });

  if (!response.ok) {
    throw new Error('Failed to fetch trends');
  }

  return response.json();
}

export async function fetchTrendById(id: string | number): Promise<Trend> {
  const response = await fetch(`${API_BASE_URL}/api/v1/trends/${id}`, {
    method: 'GET',
    headers: { 'Content-Type': 'application/json' },
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch trend ${id}`);
  }

  return response.json();
}

export async function triggerAiAnalysis(trendId: string | number): Promise<Trend> {
  const response = await fetch(`${API_BASE_URL}/api/v1/trends/${trendId}/analyze`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
  });

  if (!response.ok) {
    throw new Error(`Failed to trigger AI analysis for trend ${trendId}`);
  }

  return response.json();
}

// ── TanStack Query Hooks ─────────────────────────────────────────────

export function useTrends(params: GetTrendsParams) {
  return useQuery({
    queryKey: ['trends', params],
    queryFn: () => fetchTrends(params),
    staleTime: 1000 * 60 * 2, // 2 minutes
  });
}

export function useTrend(id: string | number) {
  return useQuery({
    queryKey: ['trend', id],
    queryFn: () => fetchTrendById(id),
    enabled: !!id,
  });
}

export function useAnalyzeTrend() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (trendId: string | number) => triggerAiAnalysis(trendId),
    onSuccess: (updatedTrend) => {
      queryClient.invalidateQueries({ queryKey: ['trends'] });
      queryClient.setQueryData(['trend', updatedTrend.id], updatedTrend);
    },
  });
}
