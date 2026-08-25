'use client';

import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useRouter } from 'next/navigation';
import { useDispatch } from 'react-redux';
import { Sparkles, Mail, Lock, User, AlertTriangle, Loader2 } from 'lucide-react';
import Link from 'next/link';
import { useRegister, useLogin } from '@/services/authApi';
import { setCredentials } from '@/store/authSlice';

const registerSchema = z
  .object({
    firstName: z.string().min(1, 'First name is required'),
    lastName: z.string().min(1, 'Last name is required'),
    email: z.string().email('Please enter a valid email address'),
    password: z
      .string()
      .min(8, 'Password must be at least 8 characters')
      .regex(/[A-Z]/, 'Password must contain at least one uppercase letter')
      .regex(/[0-9]/, 'Password must contain at least one number'),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'Passwords do not match',
    path: ['confirmPassword'],
  });

type RegisterFormData = z.infer<typeof registerSchema>;

export default function RegisterPage() {
  const router = useRouter();
  const dispatch = useDispatch();
  const { mutate: register, isPending: isRegistering, error: registerError } = useRegister();
  const { mutate: login, isPending: isLoggingIn } = useLogin();

  const isPending = isRegistering || isLoggingIn;

  const {
    register: formRegister,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = (data: RegisterFormData) => {
    register(
      {
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
        password: data.password,
      },
      {
        onSuccess: () => {
          // Auto-login after successful registration
          login(
            { email: data.email, password: data.password },
            {
              onSuccess: (authResponse) => {
                dispatch(
                  setCredentials({
                    accessToken: authResponse.accessToken,
                    refreshToken: authResponse.refreshToken,
                    user: authResponse.user,
                  })
                );
                router.push('/');
              },
            }
          );
        },
      }
    );
  };

  const inputClass =
    'w-full pl-10 pr-4 py-2.5 bg-slate-800 border border-slate-700 rounded-xl text-sm text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-transparent transition-colors';

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center px-4 py-8">
      <div className="w-full max-w-md">
        {/* Logo */}
        <div className="flex items-center justify-center space-x-3 mb-8">
          <div className="p-2 bg-gradient-to-tr from-cyan-500 to-blue-600 rounded-xl shadow-lg shadow-cyan-500/20">
            <Sparkles className="h-6 w-6 text-white" />
          </div>
          <span className="font-bold text-2xl tracking-tight bg-gradient-to-r from-white via-cyan-200 to-cyan-400 bg-clip-text text-transparent">
            AI Trend Explorer
          </span>
        </div>

        {/* Card */}
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-8 shadow-2xl space-y-6">
          <div className="text-center">
            <h1 className="text-2xl font-bold text-slate-100">Create an account</h1>
            <p className="text-sm text-slate-400 mt-1">Start exploring AI trends today</p>
          </div>

          {/* API Error */}
          {registerError && (
            <div className="flex items-start space-x-3 p-4 bg-red-950/40 border border-red-800/50 rounded-xl">
              <AlertTriangle className="h-4 w-4 text-red-400 mt-0.5 shrink-0" />
              <p className="text-sm text-red-300">{(registerError as Error).message}</p>
            </div>
          )}

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            {/* Name Row */}
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <label className="text-sm font-medium text-slate-300">First name</label>
                <div className="relative">
                  <User className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-500" />
                  <input
                    {...formRegister('firstName')}
                    type="text"
                    autoComplete="given-name"
                    placeholder="John"
                    className={inputClass}
                  />
                </div>
                {errors.firstName && (
                  <p className="text-xs text-red-400">{errors.firstName.message}</p>
                )}
              </div>
              <div className="space-y-1.5">
                <label className="text-sm font-medium text-slate-300">Last name</label>
                <div className="relative">
                  <User className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-500" />
                  <input
                    {...formRegister('lastName')}
                    type="text"
                    autoComplete="family-name"
                    placeholder="Doe"
                    className={inputClass}
                  />
                </div>
                {errors.lastName && (
                  <p className="text-xs text-red-400">{errors.lastName.message}</p>
                )}
              </div>
            </div>

            {/* Email */}
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-slate-300">Email</label>
              <div className="relative">
                <Mail className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-500" />
                <input
                  {...formRegister('email')}
                  type="email"
                  autoComplete="email"
                  placeholder="you@example.com"
                  className={inputClass}
                />
              </div>
              {errors.email && (
                <p className="text-xs text-red-400">{errors.email.message}</p>
              )}
            </div>

            {/* Password */}
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-slate-300">Password</label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-500" />
                <input
                  {...formRegister('password')}
                  type="password"
                  autoComplete="new-password"
                  placeholder="Min 8 chars, 1 uppercase, 1 number"
                  className={inputClass}
                />
              </div>
              {errors.password && (
                <p className="text-xs text-red-400">{errors.password.message}</p>
              )}
            </div>

            {/* Confirm Password */}
            <div className="space-y-1.5">
              <label className="text-sm font-medium text-slate-300">Confirm password</label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-500" />
                <input
                  {...formRegister('confirmPassword')}
                  type="password"
                  autoComplete="new-password"
                  placeholder="••••••••"
                  className={inputClass}
                />
              </div>
              {errors.confirmPassword && (
                <p className="text-xs text-red-400">{errors.confirmPassword.message}</p>
              )}
            </div>

            {/* Submit */}
            <button
              type="submit"
              disabled={isPending}
              className="w-full py-2.5 px-4 bg-gradient-to-r from-cyan-500 to-blue-600 hover:from-cyan-400 hover:to-blue-500 disabled:opacity-60 disabled:cursor-not-allowed text-white font-semibold rounded-xl text-sm transition-all shadow-lg shadow-cyan-500/20 flex items-center justify-center space-x-2"
            >
              {isPending ? (
                <>
                  <Loader2 className="h-4 w-4 animate-spin" />
                  <span>{isRegistering ? 'Creating account...' : 'Signing in...'}</span>
                </>
              ) : (
                <span>Create Account</span>
              )}
            </button>
          </form>

          <p className="text-center text-sm text-slate-400">
            Already have an account?{' '}
            <Link href="/login" className="text-cyan-400 hover:text-cyan-300 font-medium transition-colors">
              Sign in
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
