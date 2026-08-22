export type SourceType = 'GITHUB' | 'HUGGING_FACE' | (string & {});

export interface Trend {
  id: number;
  title: string;
  description: string;
  repositoryUrl: string;
  source: SourceType;
  stars: number;
  forks: number;
  language: string;
  topics: string[];
  trendScore: number;
  aiCategory?: string;
  aiSummary?: string;
  createdAt: string;
  updatedAt: string;
}

export interface PagedResult<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface GetTrendsParams {
  source?: SourceType | 'ALL';
  language?: string;
  q?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: string;
}
