import type { Config } from "tailwindcss";

const config: Config = {
  content: ["./src/**/*.{js,ts,jsx,tsx,mdx}"],
  theme: {
    extend: {
      colors: {
        background: "#0B1020",
        panel: "#121A2D",
        panelAlt: "#0F1728",
        border: "#1E293B",
        muted: "#94A3B8",
        brand: "#5B8CFF",
        success: "#10B981",
        danger: "#EF4444",
        warning: "#F59E0B"
      },
      boxShadow: {
        panel: "0 8px 30px rgba(2, 6, 23, 0.35)",
      },
      borderRadius: {
        xl: "1rem",
      }
    },
  },
  plugins: [],
};

export default config;
