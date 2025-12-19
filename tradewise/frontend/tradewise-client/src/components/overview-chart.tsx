"use client"

import { Area, AreaChart, ResponsiveContainer, Tooltip, XAxis, YAxis, CartesianGrid } from "recharts"
import { useTheme } from "next-themes"

// Mock data simulating portfolio growth
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
  const isDark = theme === "dark" || true

  const gridStroke = isDark ? "#233240" : "#e6eef6"
  const axisColor = isDark ? "#9aa7b2" : "#6b7280"

  return (
    <div style={{ width: '100%', height: 300 }}>
      <ResponsiveContainer width="100%" height="100%">
        <AreaChart data={data}>
          <defs>
            <linearGradient id="colorTotal" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#22c55e" stopOpacity={0.28}/>
              <stop offset="95%" stopColor="#22c55e" stopOpacity={0}/>
            </linearGradient>
          </defs>

          <XAxis
            dataKey="name"
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
            dataKey="total"
            stroke="#22c55e"
            strokeWidth={2}
            fillOpacity={1}
            fill="url(#colorTotal)"
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  )
}
