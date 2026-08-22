import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { SourceType } from '@/types/trend';

interface UiState {
  selectedSource: SourceType | 'ALL';
  searchKeyword: string;
  selectedLanguage: string;
  sortBy: string;
  sortDir: 'asc' | 'desc';
  page: number;
  viewMode: 'grid' | 'list';
}

const initialState: UiState = {
  selectedSource: 'ALL',
  searchKeyword: '',
  selectedLanguage: '',
  sortBy: 'stars',
  sortDir: 'desc',
  page: 0,
  viewMode: 'grid',
};

export const uiSlice = createSlice({
  name: 'ui',
  initialState,
  reducers: {
    setSource: (state, action: PayloadAction<SourceType | 'ALL'>) => {
      state.selectedSource = action.payload;
      state.page = 0; // reset to first page on filter change
    },
    setSearchKeyword: (state, action: PayloadAction<string>) => {
      state.searchKeyword = action.payload;
      state.page = 0;
    },
    setLanguage: (state, action: PayloadAction<string>) => {
      state.selectedLanguage = action.payload;
      state.page = 0;
    },
    setSortBy: (state, action: PayloadAction<string>) => {
      state.sortBy = action.payload;
      state.page = 0;
    },
    toggleSortDir: (state) => {
      state.sortDir = state.sortDir === 'desc' ? 'asc' : 'desc';
      state.page = 0;
    },
    setPage: (state, action: PayloadAction<number>) => {
      state.page = action.payload;
    },
    setViewMode: (state, action: PayloadAction<'grid' | 'list'>) => {
      state.viewMode = action.payload;
    },
  },
});

export const {
  setSource,
  setSearchKeyword,
  setLanguage,
  setSortBy,
  toggleSortDir,
  setPage,
  setViewMode,
} = uiSlice.actions;

export default uiSlice.reducer;
