import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { GetTrendsParams, PagedResult, Trend } from '@/types/trend';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

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

  const response = await fetch(`${API_BASE_URL}/api/v1/trends?${queryParams.toString()}`);
  if (!response.ok) {
    throw new Error(`Failed to fetch trends: ${response.statusText}`);
  }

  return response.json();
}

export async function fetchTrendById(id: number | string): Promise<Trend> {
  const response = await fetch(`${API_BASE_URL}/api/v1/trends/${id}`);
  if (!response.ok) {
    if (response.status === 404) {
      throw new Error('Trend not found');
    }
    throw new Error(`Failed to fetch trend details: ${response.statusText}`);
  }
  return response.json();
}

export async function triggerAiAnalysis(id: number | string): Promise<any> {
  const response = await fetch(`${API_BASE_URL}/api/v1/analysis/trend/${id}`, {
    method: 'POST',
  });
  if (!response.ok) {
    throw new Error(`AI Analysis request failed: ${response.statusText}`);
  }
  return response.json();
}

export function useTrends(params: GetTrendsParams) {
  return useQuery({
    queryKey: ['trends', params],
    queryFn: () => fetchTrends(params),
    staleTime: 1000 * 60 * 5, // 5 minutes cache
  });
}

export function useTrend(id: number | string) {
  return useQuery({
    queryKey: ['trend', id],
    queryFn: () => fetchTrendById(id),
    enabled: !!id,
    staleTime: 1000 * 60 * 5,
  });
}

export function useAnalyzeTrend() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number | string) => triggerAiAnalysis(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: ['trend', id] });
      queryClient.invalidateQueries({ queryKey: ['trends'] });
    },
  });
}
