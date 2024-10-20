/** @type {import('tailwindcss').Config} */

import flowbite from "flowbite-react/tailwind"

module.exports = {
    content: [
        "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
        "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
        "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
        flowbite.content(),
    ],
    theme: {
        extend: {
            keyframes: {
                fadeInDown: {
                    '0%': { opacity: 0, transform: 'translateY(-50px)' },
                    '100%': { opacity: 1, transform: 'translateY(0)' },
                },
                fadeInUp: {
                    '0%': { opacity: 0, transform: 'translateY(50px)' },
                    '100%': { opacity: 1, transform: 'translateY(0)' },
                },
            },
            animation: {
                fadeInDown: 'fadeInDown 0.5s ease-in-out', // Adjusted to 0.5s
                fadeInUp: 'fadeInUp 0.5s ease-in-out', // Adjusted to 0.5s
            },
            colors: {
                background: "var(--background)",
                foreground: "var(--foreground)",
            },
        },
    },
    plugins: [
        flowbite.plugin(),
    ]
};
