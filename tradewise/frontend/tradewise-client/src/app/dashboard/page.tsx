"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { DollarSign, Activity, CreditCard, Users } from "lucide-react"
import { OverviewChart } from "@/components/overview-chart"
import { API_BASE_URL } from "@/lib/utils"

// --- Define Types for our data ---
interface User {
  id: string
  email: string
  createdAt: string
}

interface Portfolio {
  id: string
  name: string
  description: string
  createdAt: string
  userId: string
}

export default function DashboardPage() {
  const router = useRouter()
  const [user, setUser] = useState<User | null>(null)
  const [portfolios, setPortfolios] = useState<Portfolio[]>([])
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
        // --- Fetch User Data (with headers) ---
        const userRes = await fetch(`${API_BASE_URL}/api/users/me`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })

        if (!userRes.ok) throw new Error("Failed to fetch user data")
        const userData = await userRes.json()
        setUser(userData)

        // --- Fetch Portfolios (with headers) ---
        const portfoliosRes = await fetch(`${API_BASE_URL}/api/portfolios`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })

        if (!portfoliosRes.ok) throw new Error("Failed to fetch portfolios")
        const portfoliosData = await portfoliosRes.json()
        setPortfolios(portfoliosData)
      } catch (err: any) {
        setError(err.message)
        // If token is invalid, backend sends 401/403, we should log out
        localStorage.removeItem("token")
        router.push("/login")
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [router])

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
            <div className="text-2xl font-bold">$45,231.89</div>
            <p className="text-xs text-muted-foreground">+20.1% from last month</p>
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
            <div className="text-2xl font-bold">3 Running</div>
            <p className="text-xs text-muted-foreground">1 paused</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Portfolio Assets</CardTitle>
            <CreditCard className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{portfolios.length}</div>
            <p className="text-xs text-muted-foreground">
              {portfolios.length === 1 ? "Portfolio" : "Portfolios"}
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Notifications</CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">+5</div>
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
            <div className="mb-4">
              <OverviewChart />
            </div>
            <div className="space-y-4">
              {portfolios.length > 0 ? (
                portfolios.map((portfolio) => (
                  <div
                    key={portfolio.id}
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
              {/* Mock List */}
              <div className="flex items-center">
                <div className="ml-4 space-y-1">
                  <p className="text-sm font-medium leading-none">
                    AAPL Buy Order
                  </p>
                  <p className="text-xs text-muted-foreground">
                    Executed at $150.00
                  </p>
                </div>
                <div className="ml-auto font-medium">+$1,500.00</div>
              </div>
              <div className="flex items-center">
                <div className="ml-4 space-y-1">
                  <p className="text-sm font-medium leading-none">
                    Strategy Alert
                  </p>
                  <p className="text-xs text-muted-foreground">
                    RSI Oversold on TSLA
                  </p>
                </div>
                <div className="ml-auto font-medium text-yellow-500">Alert</div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}