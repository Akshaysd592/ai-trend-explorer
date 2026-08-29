import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  reactStrictMode: true,
  output: "standalone",
  async rewrites() {
    let rawUrl =
      process.env.API_GATEWAY_URL ||
      process.env.NEXT_PUBLIC_API_URL ||
      "http://localhost:8080";

    // Normalize URL scheme for Next.js rewrite validation and cloud routing
    if (
      !rawUrl.startsWith("http://") &&
      !rawUrl.startsWith("https://") &&
      !rawUrl.startsWith("/")
    ) {
      if (rawUrl.includes(".onrender.com")) {
        rawUrl = `https://${rawUrl}`;
      } else {
        rawUrl = `http://${rawUrl}:10000`;
      }
    }

    const gatewayUrl = rawUrl.endsWith("/") ? rawUrl.slice(0, -1) : rawUrl;

    return [
      {
        source: "/api/v1/:path*",
        destination: `${gatewayUrl}/api/v1/:path*`,
      },
    ];
  },
};

export default nextConfig;
