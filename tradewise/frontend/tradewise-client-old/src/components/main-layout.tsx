"use client"

import { usePathname } from "next/navigation"
import { AppSidebar } from "@/components/app-sidebar"

export function MainLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname()
  // Define paths where the sidebar should NOT appear
  const isAuthPage = pathname === "/login" || pathname === "/register" || pathname === "/"

  return (
    <div className="flex h-full">
      {!isAuthPage && <AppSidebar />}
      <main className={`flex-1 h-full ${!isAuthPage ? "md:pl-64" : ""}`}>
        {children}
      </main>
    </div>
  )
}