# ==============================================================================
# AI Trend Explorer — GCP Cloud Run + Neon + Upstash Automated Deployment
# ==============================================================================
# Usage:
#   .\deploy-gcp.ps1 -GcpProjectId "your-gcp-project-id" `
#                    -NeonJdbcUrl "jdbc:postgresql://ep-xyz.us-east-2.aws.neon.tech/neondb?sslmode=require" `
#                    -NeonUser "neondb_owner" `
#                    -NeonPassword "your-neon-password" `
#                    -UpstashHost "your-upstash-db.upstash.io" `
#                    -UpstashPassword "your-upstash-password" `
#                    -GeminiApiKey "your-gemini-api-key" `
#                    -GithubToken "your-github-token"
# ==============================================================================

param (
    [Parameter(Mandatory=$true)][string]$GcpProjectId,
    [Parameter(Mandatory=$true)][string]$NeonJdbcUrl,
    [Parameter(Mandatory=$true)][string]$NeonUser,
    [Parameter(Mandatory=$true)][string]$NeonPassword,
    [Parameter(Mandatory=$true)][string]$UpstashHost,
    [Parameter(Mandatory=$true)][string]$UpstashPassword,
    [Parameter(Mandatory=$false)][string]$GeminiApiKey = "",
    [Parameter(Mandatory=$false)][string]$GithubToken = "",
    [Parameter(Mandatory=$false)][string]$JwtSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970337336763979244226452948404D6351655468576D5A7134743777217A25432A",
    [Parameter(Mandatory=$false)][string]$Region = "us-central1"
)

$ErrorActionPreference = "Stop"

Write-Host "`n🚀 Starting AI Trend Explorer GCP Deployment..." -ForegroundColor Green
Write-Host "📌 GCP Project: $GcpProjectId"
Write-Host "📌 Region: $Region"

# 1. Set GCP Config Project
Write-Host "`n[1/6] Setting GCP project context..." -ForegroundColor Cyan
gcloud config set project $GcpProjectId

# 2. Enable Required APIs
Write-Host "`n[2/6] Enabling required GCP APIs..." -ForegroundColor Cyan
gcloud services enable run.googleapis.com artifactregistry.googleapis.com cloudbuild.googleapis.com scheduler.googleapis.com

# 3. Create Artifact Registry Repository if not exists
Write-Host "`n[3/6] Ensuring Artifact Registry repository exists..." -ForegroundColor Cyan
$repoExists = gcloud artifacts repositories list --location=$Region --filter="name:$Region/repositories/ai-trend-repo" --format="value(name)"
if (-not $repoExists) {
    gcloud artifacts repositories create ai-trend-repo `
        --repository-format=docker `
        --location=$Region `
        --description="Docker repository for AI Trend Explorer microservices"
}

$REPO_URI = "$Region-docker.pkg.dev/$GcpProjectId/ai-trend-repo"

# 4. Build and Push Container Images using GCP Cloud Build
Write-Host "`n[4/6] Building and pushing Docker container images..." -ForegroundColor Cyan

Write-Host "   -> Building auth-service..."
gcloud builds submit --tag "$REPO_URI/auth-service:latest" ./auth-service

Write-Host "   -> Building trend-service..."
gcloud builds submit --tag "$REPO_URI/trend-service:latest" ./trend-service

Write-Host "   -> Building ai-analysis-service..."
gcloud builds submit --tag "$REPO_URI/ai-analysis-service:latest" ./ai-analysis-service

Write-Host "   -> Building api-gateway..."
gcloud builds submit --tag "$REPO_URI/api-gateway:latest" ./api-gateway

# 5. Deploy Backend Microservices to Cloud Run
Write-Host "`n[5/6] Deploying microservices to GCP Cloud Run..." -ForegroundColor Cyan

Write-Host "   -> Deploying auth-service..."
gcloud run deploy auth-service `
    --image="$REPO_URI/auth-service:latest" `
    --region=$Region `
    --allow-unauthenticated `
    --port=8082 `
    --memory=512Mi `
    --cpu=1 `
    --set-env-vars="SPRING_DATASOURCE_URL=$NeonJdbcUrl,SPRING_DATASOURCE_USERNAME=$NeonUser,SPRING_DATASOURCE_PASSWORD=$NeonPassword,JWT_SECRET=$JwtSecret"

$AUTH_URL = (gcloud run services describe auth-service --region=$Region --format="value(status.url)")

Write-Host "   -> Deploying trend-service..."
gcloud run deploy trend-service `
    --image="$REPO_URI/trend-service:latest" `
    --region=$Region `
    --allow-unauthenticated `
    --port=8081 `
    --memory=512Mi `
    --cpu=1 `
    --set-env-vars="SPRING_DATASOURCE_URL=$NeonJdbcUrl,SPRING_DATASOURCE_USERNAME=$NeonUser,SPRING_DATASOURCE_PASSWORD=$NeonPassword,REDIS_HOST=$UpstashHost,REDIS_PORT=6379,REDIS_PASSWORD=$UpstashPassword,GEMINI_API_KEY=$GeminiApiKey,GITHUB_TOKEN=$GithubToken,JWT_SECRET=$JwtSecret"

$TREND_URL = (gcloud run services describe trend-service --region=$Region --format="value(status.url)")

Write-Host "   -> Deploying ai-analysis-service..."
gcloud run deploy ai-analysis-service `
    --image="$REPO_URI/ai-analysis-service:latest" `
    --region=$Region `
    --allow-unauthenticated `
    --port=8083 `
    --memory=512Mi `
    --cpu=1 `
    --set-env-vars="TREND_SERVICE_URL=$TREND_URL,GEMINI_API_KEY=$GeminiApiKey"

$AI_ANALYSIS_URL = (gcloud run services describe ai-analysis-service --region=$Region --format="value(status.url)")

Write-Host "   -> Deploying api-gateway..."
gcloud run deploy api-gateway `
    --image="$REPO_URI/api-gateway:latest" `
    --region=$Region `
    --allow-unauthenticated `
    --port=8080 `
    --memory=512Mi `
    --cpu=1 `
    --set-env-vars="AUTH_SERVICE_URL=$AUTH_URL,TREND_SERVICE_URL=$TREND_URL,AI_ANALYSIS_SERVICE_URL=$AI_ANALYSIS_URL"

$API_GATEWAY_URL = (gcloud run services describe api-gateway --region=$Region --format="value(status.url)")

# 6. Deploy Frontend
Write-Host "`n[6/6] Building & Deploying Next.js Frontend..." -ForegroundColor Cyan

$frontendBuildYaml = @"
steps:
  - name: 'gcr.io/cloud-builders/docker'
    args:
      - 'build'
      - '-t'
      - '$REPO_URI/frontend:latest'
      - '--build-arg'
      - 'NEXT_PUBLIC_API_URL=$API_GATEWAY_URL'
      - '.'
    dir: 'frontend'
images:
  - '$REPO_URI/frontend:latest'
"@
Set-Content -Path "./cloudbuild-frontend.yaml" -Value $frontendBuildYaml

gcloud builds submit --config=cloudbuild-frontend.yaml .
Remove-Item -Path "./cloudbuild-frontend.yaml" -Force -ErrorAction SilentlyContinue

gcloud run deploy frontend `
    --image="$REPO_URI/frontend:latest" `
    --region=$Region `
    --allow-unauthenticated `
    --port=3000 `
    --memory=256Mi `
    --cpu=0.5

$FRONTEND_URL = (gcloud run services describe frontend --region=$Region --format="value(status.url)")

# 7. Setup Automated Hourly Cron (Cloud Scheduler)
Write-Host "`n⏰ Setting up Cloud Scheduler (Hourly Trend Ingestion)..." -ForegroundColor Cyan
gcloud scheduler jobs create http hourly-trend-sync `
    --schedule="0 * * * *" `
    --uri="$API_GATEWAY_URL/api/v1/trends/ingest" `
    --http-method=POST `
    --location=$Region `
    --quiet 2>$null

Write-Host "`n==========================================================================" -ForegroundColor Green
Write-Host "🎉 DEPLOYMENT COMPLETE!" -ForegroundColor Green
Write-Host "==========================================================================" -ForegroundColor Green
Write-Host "🌐 Live Frontend URL:    $FRONTEND_URL" -ForegroundColor Yellow
Write-Host "🌐 Live API Gateway URL: $API_GATEWAY_URL" -ForegroundColor Yellow
Write-Host "==========================================================================" -ForegroundColor Green
