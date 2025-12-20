import { Metadata } from "next"
import { SidebarNav, dashboardNavItems } from "@/components/sidebar-nav"
import { Separator } from "@/components/ui/separator"
import { ThemeToggle } from "@/components/theme-toggle"

export const metadata: Metadata = {
  title: "Dashboard | TradeWise",
  description: "Manage your portfolios and strategies.",
}

interface DashboardLayoutProps {
  children: React.ReactNode
}

export default function DashboardLayout({ children }: DashboardLayoutProps) {
  return (
    <div className="hidden space-y-6 p-10 pb-16 md:block">
      <div className="flex items-center justify-between space-y-0.5">
        <div>
          <h2 className="text-2xl font-bold tracking-tight">TradeWise Dashboard</h2>
          <p className="text-muted-foreground">
            Manage your investments, track performance, and automate strategies.
          </p>
        </div>
        <div className="flex items-center space-x-4">
            <ThemeToggle />
        </div>
      </div>
      <Separator className="my-6" />
      <div className="flex flex-col space-y-8 lg:flex-row lg:space-x-12 lg:space-y-0">
        <aside className="-mx-4 lg:w-1/5">
          <SidebarNav items={dashboardNavItems} />
        </aside>
        <div className="flex-1 lg:max-w-4xl">{children}</div>
      </div>
    </div>
  )
}
