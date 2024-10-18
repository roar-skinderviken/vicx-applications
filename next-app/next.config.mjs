/** @type {import('next').NextConfig} */

const nextConfig = {
    basePath: process.env.NEXT_PUBLIC_CUSTOM_BASE_PATH || "",
    async headers() {
        return [
            {
                source: '/(.*)', // Apply to all routes
                headers: [
                    {
                        key: 'Cache-Control',
                        value: 'public, max-age=3600',
                    },
                ],
            },
            {
                source: '/esport',
                headers: [
                    {
                        key: 'Cache-Control',
                        value: 'public, max-age=30',
                    },
                ],
            },
        ]
    },
};

export default nextConfig;