"use client"

import {
  Area,
  AreaChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
  CartesianGrid,
} from "recharts"
import { useTheme } from "next-themes"

// Mock data simulating portfolio growth over 12 months
const data = [
  { name: "Jan", total: 12000 },
  { name: "Feb", total: 13500 },
  { name: "Mar", total: 12800 },
  { name: "Apr", total: 14200 },
  { name: "May", total: 15100 },
  { name: "Jun", total: 16800 },
  { name: "Jul", total: 16200 },
  { name: "Aug", total: 17500 },
  { name: "Sep", total: 19200 },
  { name: "Oct", total: 18800 },
  { name: "Nov", total: 21500 },
  { name: "Dec", total: 23000 },
]

export function OverviewChart() {
  const { theme } = useTheme()

  // Dynamic colors based on theme, with dark mode as default for fintech aesthetic
  const isDark = theme === "dark" || true

  return (
    <ResponsiveContainer width="100%" height={350}>
      <AreaChart
        data={data}
        margin={{ top: 10, right: 30, left: 0, bottom: 0 }}
      >
        <defs>
          <linearGradient id="colorTotal" x1="0" y1="0" x2="0" y2="1">
            <stop offset="5%" stopColor="#22c55e" stopOpacity={0.3} />
            <stop offset="95%" stopColor="#22c55e" stopOpacity={0} />
          </linearGradient>
        </defs>

        <XAxis
          dataKey="name"
          stroke="#888888"
          fontSize={12}
          tickLine={false}
          axisLine={false}
        />

        <YAxis
          stroke="#888888"
          fontSize={12}
          tickLine={false}
          axisLine={false}
          tickFormatter={(value) => `$${(value / 1000).toFixed(0)}k`}
        />

        <CartesianGrid vertical={false} strokeDasharray="3 3" stroke="#333" />

        <Tooltip
          contentStyle={{
            backgroundColor: "#1f2937",
            border: "1px solid #4b5563",
            borderRadius: "8px",
            color: "#fff",
            boxShadow: "0 4px 12px rgba(0, 0, 0, 0.3)",
          }}
          itemStyle={{ color: "#22c55e" }}
          formatter={(value: any) => [`$${value?.toLocaleString()}`, "Balance"]}
        />

        <Area
          type="monotone"
          dataKey="total"
          stroke="#22c55e"
          strokeWidth={2}
          fillOpacity={1}
          fill="url(#colorTotal)"
          isAnimationActive={true}
        />
      </AreaChart>
    </ResponsiveContainer>
  )
}
