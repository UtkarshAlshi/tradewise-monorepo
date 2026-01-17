"use client"

import { Area, AreaChart, ResponsiveContainer, Tooltip, XAxis, YAxis, CartesianGrid } from "recharts"
import { useTheme } from "next-themes"

interface OverviewChartProps {
  data: { date: string; value: number }[];
}

export function OverviewChart({ data }: OverviewChartProps) {
  const { theme } = useTheme()
  const isDark = theme === "dark" || true

  const gridStroke = isDark ? "#233240" : "#e6eef6"
  const axisColor = isDark ? "#9aa7b2" : "#6b7280"

  return (
    <ResponsiveContainer width="100%" height={300}>
      <AreaChart data={data}>
        <defs>
          <linearGradient id="colorValue" x1="0" y1="0" x2="0" y2="1">
            <stop offset="5%" stopColor="#22c55e" stopOpacity={0.28}/>
            <stop offset="95%" stopColor="#22c55e" stopOpacity={0}/>
          </linearGradient>
        </defs>

        <XAxis
          dataKey="date"
          stroke={axisColor}
          fontSize={12}
          tickLine={false}
          axisLine={false}
        />

        <YAxis
          stroke={axisColor}
          fontSize={12}
          tickLine={false}
          axisLine={false}
          tickFormatter={(value) => `$${value.toLocaleString()}`}
        />

        <CartesianGrid vertical={false} strokeDasharray="3 3" stroke={gridStroke} />

        <Tooltip
          contentStyle={{
            backgroundColor: isDark ? '#0f1724' : '#ffffff',
            border: 'none',
            borderRadius: 8,
            color: isDark ? '#fff' : '#000'
          }}
          itemStyle={{ color: '#22c55e' }}
        />

        <Area
          type="monotone"
          dataKey="value"
          stroke="#22c55e"
          strokeWidth={2}
          fillOpacity={1}
          fill="url(#colorValue)"
        />
      </AreaChart>
    </ResponsiveContainer>
  )
}