'use client';

import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '@/store/store';
import { setPage } from '@/store/uiSlice';
import { ChevronLeft, ChevronRight } from 'lucide-react';

interface PaginationProps {
  totalPages: number;
  totalElements: number;
  isLast: boolean;
}

export const Pagination: React.FC<PaginationProps> = ({ totalPages, totalElements, isLast }) => {
  const dispatch = useDispatch();
  const { page } = useSelector((state: RootState) => state.ui);

  if (totalPages <= 1) return null;

  return (
    <div className="flex flex-col sm:flex-row items-center justify-between gap-4 py-6 border-t border-slate-800 text-sm text-slate-400">
      <div>
        Showing Page <span className="font-bold text-slate-200">{page + 1}</span> of{' '}
        <span className="font-bold text-slate-200">{totalPages}</span> ({totalElements} total trends)
      </div>

      <div className="flex items-center space-x-2">
        <button
          onClick={() => dispatch(setPage(page - 1))}
          disabled={page === 0}
          className="flex items-center space-x-1 px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 disabled:opacity-40 disabled:hover:bg-slate-800 text-slate-200 font-semibold border border-slate-700 transition-colors"
        >
          <ChevronLeft className="h-4 w-4" />
          <span>Previous</span>
        </button>

        <button
          onClick={() => dispatch(setPage(page + 1))}
          disabled={isLast || page >= totalPages - 1}
          className="flex items-center space-x-1 px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 disabled:opacity-40 disabled:hover:bg-slate-800 text-slate-200 font-semibold border border-slate-700 transition-colors"
        >
          <span>Next</span>
          <ChevronRight className="h-4 w-4" />
        </button>
      </div>
    </div>
  );
};
