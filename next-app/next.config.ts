import type { NextConfig } from "next"

const nextConfig: NextConfig = {
    output: "standalone",
    images: {
        remotePatterns: [
            {
                protocol: 'https',
                hostname: 'avatars.githubusercontent.com',
                pathname: '/**',
            },
            {
                protocol: 'https',
                hostname: 'private-avatars.githubusercontent.com',
                pathname: '/**',
            },
        ],
    },
    async headers() {
        return [
            {
                source: "/(.*)", // Apply to all routes
                headers: [
                    {
                        key: "Cache-Control",
                        value: "public, max-age=3600",
                    },
                ],
            },
            {
                source: "/api/auth/(.*)",
                headers: [
                    {
                        key: "Cache-Control",
                        value: "no-store, max-age=0"
                    },
                ],
            },
            {
                source: "/api/calculator",
                headers: [
                    {
                        key: "Cache-Control",
                        value: "no-store, max-age=0"
                    },
                ],
            },
            {
                source: "/esport",
                headers: [
                    {
                        key: "Cache-Control",
                        value: "public, max-age=30",
                    },
                ],
            },
            {
                source: "/user/dashboard",
                headers: [
                    {
                        key: "Cache-Control",
                        value: "no-store, max-age=0"
                    },
                ],
            },
        ]
    },
    async redirects() {
        return [
            {
                source: "/arch.html",
                destination: "/arch",
                permanent: true
            },
            {
                source: "/cs.html",
                destination: "/cs",
                permanent: true
            },
            {
                source: "/esport.html",
                destination: "/esport",
                permanent: true
            },
            {
                source: "/index.html",
                destination: "/",
                permanent: true
            },
            {
                source: "/k-means.html",
                destination: "/k-means",
                permanent: true
            },
            {
                source: "/microk8s.html",
                destination: "/microk8s",
                permanent: true
            },
            {
                source: "/old_index.html",
                destination: "/portfolio",
                permanent: true
            },
            {
                source: "/setup.html",
                destination: "/microk8s",
                permanent: true
            },
            {
                source: "/pen.html",
                destination: "/penetration-testing",
                permanent: true
            },
            {
                source: "/snake-game.html",
                destination: "/snake",
                permanent: true
            },
            {
                source: "/sample",
                destination: "/tomcat",
                permanent: true
            }
        ]
    },
}

export default nextConfig
