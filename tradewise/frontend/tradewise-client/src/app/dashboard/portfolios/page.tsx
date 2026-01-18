"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { CreatePortfolioDialog } from "@/components/create-portfolio-dialog"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { ArrowRight, Wallet } from "lucide-react"
import { API_BASE_URL } from "@/lib/utils"

interface Portfolio {
  id: string
  name: string
  description: string
  createdAt: string
}

export default function PortfoliosPage() {
  const router = useRouter()
  const [portfolios, setPortfolios] = useState<Portfolio[]>([])
  const [loading, setLoading] = useState(true)

  const fetchPortfolios = async () => {
    try {
      const token = localStorage.getItem("token")
      if (!token) {
        router.push("/login")
        return
      }

      const res = await fetch(`${API_BASE_URL}/api/portfolios`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })

      if (res.ok) {
        const data = await res.json()
        setPortfolios(data)
      }
    } catch (error) {
      console.error("Failed to fetch portfolios", error)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchPortfolios()
  }, [router])

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h3 className="text-lg font-medium">Portfolios</h3>
          <p className="text-sm text-muted-foreground">
            Manage your investment collections.
          </p>
        </div>
        <CreatePortfolioDialog onPortfolioCreated={fetchPortfolios} />
      </div>

      {loading ? (
        <div>Loading portfolios...</div>
      ) : portfolios.length === 0 ? (
        <div className="flex h-[450px] shrink-0 items-center justify-center rounded-md border border-dashed">
          <div className="mx-auto flex max-w-[420px] flex-col items-center justify-center text-center">
            <Wallet className="h-10 w-10 text-muted-foreground" />
            <h3 className="mt-4 text-lg font-semibold">No portfolios added</h3>
            <p className="mb-4 mt-2 text-sm text-muted-foreground">
              You haven't created any portfolios yet. Create one to start tracking your assets.
            </p>
            <CreatePortfolioDialog onPortfolioCreated={fetchPortfolios} />
          </div>
        </div>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {portfolios.map((portfolio) => (
            <Card key={portfolio.id} className="flex flex-col justify-between hover:shadow-md transition-shadow">
              <CardHeader>
                <CardTitle>{portfolio.name}</CardTitle>
                <CardDescription>{portfolio.description || "No description"}</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="flex justify-end">
                  <Button variant="ghost" size="sm" className="gap-2">
                    View Details <ArrowRight className="h-4 w-4" />
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}