#!/usr/bin/env bash
# ==============================================================================
# AI Trend Explorer — GCP Cloud Run + Neon + Upstash Automated Deployment (Bash)
# ==============================================================================
# Usage in Cloud Shell:
#   chmod +x deploy-gcp.sh
#   ./deploy-gcp.sh \
#       --project-id "YOUR_GCP_PROJECT_ID" \
#       --neon-url "jdbc:postgresql://ep-xyz.us-east-2.aws.neon.tech/neondb?sslmode=require" \
#       --neon-user "neondb_owner" \
#       --neon-pass "YOUR_NEON_PASSWORD" \
#       --upstash-host "YOUR_UPSTASH_HOST.upstash.io" \
#       --upstash-pass "YOUR_UPSTASH_PASSWORD" \
#       --gemini-key "YOUR_GEMINI_KEY" \
#       --github-token "YOUR_GITHUB_TOKEN"
# ==============================================================================

set -e

REGION="us-central1"
JWT_SECRET="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970337336763979244226452948404D6351655468576D5A7134743777217A25432A"

# Parse arguments
while [[ "$#" -gt 0 ]]; do
    case $1 in
        --project-id) PROJECT_ID="$2"; shift ;;
        --neon-url) NEON_URL="$2"; shift ;;
        --neon-user) NEON_USER="$2"; shift ;;
        --neon-pass) NEON_PASS="$2"; shift ;;
        --upstash-host) UPSTASH_HOST="$2"; shift ;;
        --upstash-pass) UPSTASH_PASS="$2"; shift ;;
        --gemini-key) GEMINI_KEY="$2"; shift ;;
        --github-token) GITHUB_TOKEN="$2"; shift ;;
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
    shift
done

if [[ -z "$PROJECT_ID" || -z "$NEON_URL" || -z "$NEON_USER" || -z "$NEON_PASS" || -z "$UPSTASH_HOST" || -z "$UPSTASH_PASS" ]]; then
    echo "❌ Error: Missing required arguments."
    echo "Required: --project-id, --neon-url, --neon-user, --neon-pass, --upstash-host, --upstash-pass"
    exit 1
fi

echo -e "\n🚀 Starting AI Trend Explorer GCP Deployment..."
echo "📌 GCP Project: $PROJECT_ID"
echo "📌 Region: $REGION"

# 1. Set Project
gcloud config set project "$PROJECT_ID"

# 2. Enable APIs
echo -e "\n[1/6] Enabling required GCP APIs..."
gcloud services enable run.googleapis.com artifactregistry.googleapis.com cloudbuild.googleapis.com scheduler.googleapis.com

# 3. Create Artifact Registry
echo -e "\n[2/6] Ensuring Artifact Registry repository exists..."
if ! gcloud artifacts repositories describe ai-trend-repo --location="$REGION" &>/dev/null; then
    gcloud artifacts repositories create ai-trend-repo \
        --repository-format=docker \
        --location="$REGION" \
        --description="Docker repository for AI Trend Explorer microservices"
fi

REPO_URI="$REGION-docker.pkg.dev/$PROJECT_ID/ai-trend-repo"

# 4. Build Images
echo -e "\n[3/6] Building container images with Cloud Build..."
echo "   -> Building auth-service..."
gcloud builds submit --tag "$REPO_URI/auth-service:latest" ./auth-service

echo "   -> Building trend-service..."
gcloud builds submit --tag "$REPO_URI/trend-service:latest" ./trend-service

echo "   -> Building ai-analysis-service..."
gcloud builds submit --tag "$REPO_URI/ai-analysis-service:latest" ./ai-analysis-service

echo "   -> Building api-gateway..."
gcloud builds submit --tag "$REPO_URI/api-gateway:latest" ./api-gateway

# 5. Deploy Microservices
echo -e "\n[4/6] Deploying microservices to Cloud Run..."

echo "   -> Deploying auth-service..."
gcloud run deploy auth-service \
    --image="$REPO_URI/auth-service:latest" \
    --region="$REGION" \
    --allow-unauthenticated \
    --port=8082 \
    --memory=512Mi \
    --cpu=1 \
    --set-env-vars="SPRING_DATASOURCE_URL=$NEON_URL,SPRING_DATASOURCE_USERNAME=$NEON_USER,SPRING_DATASOURCE_PASSWORD=$NEON_PASS,JWT_SECRET=$JWT_SECRET"

AUTH_URL=$(gcloud run services describe auth-service --region="$REGION" --format="value(status.url)")

echo "   -> Deploying trend-service..."
gcloud run deploy trend-service \
    --image="$REPO_URI/trend-service:latest" \
    --region="$REGION" \
    --allow-unauthenticated \
    --port=8081 \
    --memory=512Mi \
    --cpu=1 \
    --set-env-vars="SPRING_DATASOURCE_URL=$NEON_URL,SPRING_DATASOURCE_USERNAME=$NEON_USER,SPRING_DATASOURCE_PASSWORD=$NEON_PASS,REDIS_HOST=$UPSTASH_HOST,REDIS_PORT=6379,REDIS_PASSWORD=$UPSTASH_PASS,GEMINI_API_KEY=$GEMINI_KEY,GITHUB_TOKEN=$GITHUB_TOKEN,JWT_SECRET=$JWT_SECRET"

TREND_URL=$(gcloud run services describe trend-service --region="$REGION" --format="value(status.url)")

echo "   -> Deploying ai-analysis-service..."
gcloud run deploy ai-analysis-service \
    --image="$REPO_URI/ai-analysis-service:latest" \
    --region="$REGION" \
    --allow-unauthenticated \
    --port=8083 \
    --memory=512Mi \
    --cpu=1 \
    --set-env-vars="TREND_SERVICE_URL=$TREND_URL,GEMINI_API_KEY=$GEMINI_KEY"

echo "   -> Deploying api-gateway..."
gcloud run deploy api-gateway \
    --image="$REPO_URI/api-gateway:latest" \
    --region="$REGION" \
    --allow-unauthenticated \
    --port=8080 \
    --memory=512Mi \
    --cpu=1 \
    --set-env-vars="AUTH_SERVICE_URL=$AUTH_URL,TREND_SERVICE_URL=$TREND_URL,AI_ANALYSIS_SERVICE_URL=$AI_ANALYSIS_URL"

API_GATEWAY_URL=$(gcloud run services describe api-gateway --region="$REGION" --format="value(status.url)")

# 6. Deploy Frontend
echo -e "\n[5/6] Building & Deploying Next.js Frontend..."
cat <<EOF > cloudbuild-frontend.yaml
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
EOF

gcloud builds submit --config=cloudbuild-frontend.yaml .
rm -f cloudbuild-frontend.yaml

gcloud run deploy frontend \
    --image="$REPO_URI/frontend:latest" \
    --region="$REGION" \
    --allow-unauthenticated \
    --port=3000 \
    --memory=256Mi \
    --cpu=0.5

FRONTEND_URL=$(gcloud run services describe frontend --region="$REGION" --format="value(status.url)")

# 7. Setup Scheduler
echo -e "\n[6/6] Setting up Cloud Scheduler (Hourly Trend Ingestion)..."
gcloud scheduler jobs create http hourly-trend-sync \
    --schedule="0 * * * *" \
    --uri="$API_GATEWAY_URL/api/v1/trends/ingest" \
    --http-method=POST \
    --location="$REGION" \
    --quiet 2>/dev/null || true

echo -e "\n=========================================================================="
echo "🎉 DEPLOYMENT COMPLETE!"
echo "=========================================================================="
echo "🌐 Live Frontend URL:    $FRONTEND_URL"
echo "🌐 Live API Gateway URL: $API_GATEWAY_URL"
echo "=========================================================================="
