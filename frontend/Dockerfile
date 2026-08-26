# ── Stage 1: Dependencies ──────────────────────────────────────────────────────
FROM node:20-alpine AS deps

WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci --only=production

# ── Stage 2: Builder ───────────────────────────────────────────────────────────
FROM node:20-alpine AS builder

WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci

COPY . .

# Build the Next.js production bundle
ARG NEXT_PUBLIC_API_URL=http://localhost:8080
ENV NEXT_PUBLIC_API_URL=$NEXT_PUBLIC_API_URL

RUN npm run build

# ── Stage 3: Runtime ───────────────────────────────────────────────────────────
FROM node:20-alpine AS runtime

WORKDIR /app
ENV NODE_ENV=production

# Copy built output and prod dependencies
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
COPY --from=builder /app/package.json ./package.json
COPY --from=deps /app/node_modules ./node_modules

# Non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 3000

CMD ["node_modules/.bin/next", "start", "-p", "3000"]
