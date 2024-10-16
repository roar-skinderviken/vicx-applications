/** @type {import('next').NextConfig} */

const nextConfig = {
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
        ];
    },
};

export default nextConfig;