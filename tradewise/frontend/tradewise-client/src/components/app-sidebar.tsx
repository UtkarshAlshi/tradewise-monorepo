"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"
import { LayoutDashboard, LineChart, Trophy, LogOut, Wallet } from "lucide-react"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"

const routes = [
  {
    label: "Dashboard",
    icon: LayoutDashboard,
    href: "/dashboard",
    color: "text-sky-500",
  },
  {
    label: "My Strategies",
    icon: LineChart,
    href: "/strategies",
    color: "text-violet-500",
  },
  {
    label: "Leaderboard",
    icon: Trophy,
    href: "/leaderboard",
    color: "text-yellow-500",
  },
]

export function AppSidebar() {
  const pathname = usePathname()

  // Don't show sidebar on login/register pages
  if (pathname === "/login" || pathname === "/register" || pathname === "/") {
    return null
  }

  const handleLogout = () => {
    localStorage.removeItem("token")
    window.location.href = "/login"
  }

  return (
    <div className="space-y-4 py-4 flex flex-col h-full bg-slate-900 text-white w-64 border-r border-slate-800 fixed left-0 top-0 bottom-0 z-50">
      <div className="px-3 py-2 flex-1">
        <Link href="/dashboard" className="flex items-center pl-3 mb-14">
          <div className="relative w-8 h-8 mr-4">
            <Wallet className="w-8 h-8 text-emerald-500" />
          </div>
          <h1 className="text-2xl font-bold">
            TradeWise
          </h1>
        </Link>
        <div className="space-y-1">
          {routes.map((route) => (
            <Link
              key={route.href}
              href={route.href}
              className={cn(
                "text-sm group flex p-3 w-full justify-start font-medium cursor-pointer hover:text-white hover:bg-white/10 rounded-lg transition",
                pathname === route.href ? "text-white bg-white/10" : "text-zinc-400"
              )}
            >
              <div className="flex items-center flex-1">
                <route.icon className={cn("h-5 w-5 mr-3", route.color)} />
                {route.label}
              </div>
            </Link>
          ))}
        </div>
      </div>
      <div className="px-3 py-2">
        <Button 
            onClick={handleLogout}
            variant="ghost" 
            className="w-full justify-start text-zinc-400 hover:text-white hover:bg-white/10"
        >
            <LogOut className="h-5 w-5 mr-3 text-red-500" />
            Logout
        </Button>
      </div>
    </div>
  )
}