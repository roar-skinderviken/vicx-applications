/** @type {import('tailwindcss').Config} */

import type { Config } from "tailwindcss"
import {content, plugin} from "flowbite-react/tailwind"
import {nextui} from "@nextui-org/react"

const config: Config = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
    "./node_modules/@nextui-org/theme/dist/**/*.{js,ts,jsx,tsx}",
    content()
  ],
  theme: {
    extend: {
      keyframes: {
        fadeInDown: {
          "0%": {opacity: "0", transform: 'translateY(-50px)'},
          "100%": {opacity: "1", transform: 'translateY(0)'},
        },
        fadeInUp: {
          "0%": {opacity: "0", transform: 'translateY(50px)'},
          "100%": {opacity: "1", transform: 'translateY(0)'},
        },
      },
      animation: {
        fadeInDown: "fadeInDown 0.8s ease-in-out",
        fadeInUp: "fadeInUp 0.8s ease-in-out",
      },
      colors: {
        background: "var(--background)",
        foreground: "var(--foreground)",
      },
    },
  },
  darkMode: "class",
  plugins: [
    nextui(),
    plugin(),
  ],
}

export default config
