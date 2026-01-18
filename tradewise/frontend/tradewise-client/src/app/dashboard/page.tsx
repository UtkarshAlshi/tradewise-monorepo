"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { DollarSign, Activity, CreditCard, Users } from "lucide-react"
import { OverviewChart } from "@/components/overview-chart"
import { API_BASE_URL } from "@/lib/utils"
import { z } from "zod"
import Link from "next/link"

// --- Define Zod Schemas ---
const PortfolioSchema = z.object({
  id: z.string(),
  name: z.string(),
  description: z.string().nullable().optional(),
  createdAt: z.string(),
  userEmail: z.string(),
})

const StrategySchema = z.object({
  id: z.string(),
  name: z.string(),
  description: z.string().nullable().optional(),
  createdAt: z.string(),
  userEmail: z.string(),
})

// --- Define Types for our data ---
interface User {
  id: string
  email: string
  createdAt: string
}

type Portfolio = z.infer<typeof PortfolioSchema>
type Strategy = z.infer<typeof StrategySchema>

interface DashboardAnalytics {
  totalBalance: number
  balanceChangePercent: number
  activeStrategies: number
  pausedStrategies: number
  totalPortfolios: number
  unreadNotifications: number
  portfolioGrowth: { date: string; value: number }[]
}

export default function DashboardPage() {
  const router = useRouter()
  const [user, setUser] = useState<User | null>(null)
  const [portfolios, setPortfolios] = useState<Portfolio[]>([])
  const [strategies, setStrategies] = useState<Strategy[]>([])
  const [analytics, setAnalytics] = useState<DashboardAnalytics | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")

  useEffect(() => {
    const token = localStorage.getItem("token")
    if (!token) {
      router.push("/login")
      return
    }

    const fetchData = async () => {
      try {
        // 1. Fetch User Data
        const userRes = await fetch(`${API_BASE_URL}/api/users/me`, {
          headers: { Authorization: `Bearer ${token}` },
        })
        if (userRes.status === 401 || userRes.status === 403) {
          localStorage.removeItem("token")
          router.push("/login")
          return
        }
        if (!userRes.ok) throw new Error("Failed to fetch user data")
        const userData = await userRes.json()
        setUser(userData)

        // 2. Fetch Portfolios (Real Data)
        const portfoliosRes = await fetch(`${API_BASE_URL}/api/portfolios`, {
          headers: { Authorization: `Bearer ${token}` },
        })
        if (!portfoliosRes.ok) throw new Error("Failed to fetch portfolios")
        const portfoliosData = await portfoliosRes.json()
        const validatedPortfolios = z.array(PortfolioSchema).parse(portfoliosData)
        setPortfolios(validatedPortfolios)

        // 3. Fetch Strategies (Real Data)
        const strategiesRes = await fetch(`${API_BASE_URL}/api/strategies`, {
          headers: { Authorization: `Bearer ${token}` },
        })
        if (!strategiesRes.ok) throw new Error("Failed to fetch strategies")
        const strategiesData = await strategiesRes.json()
        const validatedStrategies = z.array(StrategySchema).parse(strategiesData)
        setStrategies(validatedStrategies)

        // 4. Fetch Dashboard Analytics (Aggregated Data)
        const analyticsRes = await fetch(`${API_BASE_URL}/api/dashboard/analytics`, {
          headers: { Authorization: `Bearer ${token}` },
        })
        if (analyticsRes.ok) {
          const analyticsData = await analyticsRes.json()
          setAnalytics(analyticsData)
        } else {
          console.warn("Failed to fetch dashboard analytics")
        }

      } catch (err: any) {
        console.error("Data validation or fetch error:", err)
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [router])

  // --- Derived Metrics ---
  // We prefer analytics data if available, otherwise fallback to calculated/mock
  const totalPortfolios = analytics?.totalPortfolios ?? portfolios.length
  const activeStrategies = analytics?.activeStrategies ?? strategies.length
  const pausedStrategies = analytics?.pausedStrategies ?? 0
  const totalBalance = analytics?.totalBalance ?? 0.00
  const balanceChangePercent = analytics?.balanceChangePercent ?? 0.00
  const unreadNotifications = analytics?.unreadNotifications ?? 0
  
  const portfolioGrowth = analytics?.portfolioGrowth ?? [
    { date: "2023-01-01", value: 0 },
    { date: "2023-02-01", value: 0 },
    { date: "2023-03-01", value: 0 },
  ]

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        Loading...
      </div>
    )
  }

  if (error) {
    return (
      <div className="flex min-h-screen items-center justify-center text-red-500">
        Error: {error}
      </div>
    )
  }

  return (
    <div className="flex flex-col gap-4">
      <h1 className="text-lg font-semibold md:text-2xl">Dashboard Overview</h1>

      {/* KPI CARDS */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Balance</CardTitle>
            <DollarSign className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">${totalBalance.toLocaleString()}</div>
            <p className="text-xs text-muted-foreground">
              {balanceChangePercent.toFixed(2)}% from last month
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              Active Strategies
            </CardTitle>
            <Activity className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{activeStrategies} Running</div>
            <p className="text-xs text-muted-foreground">{pausedStrategies} paused</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Portfolios</CardTitle>
            <CreditCard className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{totalPortfolios}</div>
            <p className="text-xs text-muted-foreground">
              {totalPortfolios === 1 ? "Portfolio" : "Portfolios"}
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Notifications</CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">+{unreadNotifications}</div>
            <p className="text-xs text-muted-foreground">Since last login</p>
          </CardContent>
        </Card>
      </div>

      {/* PORTFOLIO AND ACTIVITY SECTION */}
      <div className="grid gap-4 md:gap-8 lg:grid-cols-3">
        <Card className="xl:col-span-2">
          <CardHeader>
            <CardTitle>Your Portfolios</CardTitle>
            <CardDescription>
              View and manage all your investment portfolios
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="h-[350px] w-full">
              <OverviewChart data={portfolioGrowth} />
            </div>
            <div className="space-y-4">
              {portfolios.length > 0 ? (
                portfolios.map((portfolio) => (
                  <Link href={`/dashboard/portfolio/${portfolio.id}`} key={portfolio.id}>
                    <div
                      className="flex items-center justify-between p-4 border rounded-lg hover:bg-accent/50 transition-colors cursor-pointer"
                    >
                      <div>
                        <p className="text-sm font-medium leading-none">
                          {portfolio.name}
                        </p>
                        <p className="text-xs text-muted-foreground mt-1">
                          {portfolio.description || "No description"}
                        </p>
                      </div>
                      <div className="text-sm font-medium">View →</div>
                    </div>
                  </Link>
                ))
              ) : (
                <div className="h-[150px] flex items-center justify-center border rounded border-dashed text-muted-foreground">
                  No portfolios yet. Create one to get started.
                </div>
              )}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Recent Activity</CardTitle>
            <CardDescription>Latest trades and alerts</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {/* Placeholder for Transaction History */}
              <div className="h-[150px] flex items-center justify-center text-muted-foreground text-sm">
                No recent activity.
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}