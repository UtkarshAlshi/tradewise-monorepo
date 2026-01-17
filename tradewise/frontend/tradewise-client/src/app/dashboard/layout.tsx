import { DashboardNav } from "@/components/dashboard-nav"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import { Sheet, SheetContent, SheetTrigger, SheetTitle, SheetDescription } from "@/components/ui/sheet"
import { Menu, TrendingUp } from "lucide-react"
import Link from "next/link"
import { VisuallyHidden } from "@radix-ui/react-visually-hidden"

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <div className="grid min-h-screen w-full md:grid-cols-[220px_1fr] lg:grid-cols-[280px_1fr]">
      {/* 1. DESKTOP SIDEBAR */}
      <div className="hidden border-r bg-muted/40 md:block z-20">
        <div className="flex h-full max-h-screen flex-col gap-2 sticky top-0">
          <div className="flex h-14 items-center border-b px-4 lg:h-[60px] lg:px-6 bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
            <Link href="/" className="flex items-center gap-2 font-semibold">
              <TrendingUp className="h-6 w-6 text-primary" />
              <span className="">TradeWise</span>
            </Link>
          </div>
          <div className="flex-1 overflow-auto py-2">
            <nav className="grid items-start px-2 text-sm font-medium lg:px-4">
              <DashboardNav />
            </nav>
          </div>
        </div>
      </div>

      {/* 2. MAIN CONTENT AREA */}
      <div className="flex flex-col min-w-0">
        {/* HEADER */}
        <header className="flex h-14 items-center gap-4 border-b bg-muted/40 px-4 lg:h-[60px] lg:px-6 sticky top-0 z-10 backdrop-blur-sm">
          
          {/* Mobile Menu Trigger */}
          <Sheet>
            <SheetTrigger asChild>
              <Button variant="outline" size="icon" className="shrink-0 md:hidden">
                <Menu className="h-5 w-5" />
                <span className="sr-only">Toggle navigation menu</span>
              </Button>
            </SheetTrigger>
            <SheetContent side="left" className="flex flex-col">
              <VisuallyHidden>
                <SheetTitle>Navigation Menu</SheetTitle>
                <SheetDescription>
                  A list of links to navigate the application.
                </SheetDescription>
              </VisuallyHidden>
              <nav className="grid gap-2 text-lg font-medium">
                <Link href="#" className="flex items-center gap-2 text-lg font-semibold mb-4">
                  <TrendingUp className="h-6 w-6 text-primary" />
                  <span>TradeWise</span>
                </Link>
                <DashboardNav />
              </nav>
            </SheetContent>
          </Sheet>

          {/* Header Actions (Search / Profile) */}
          <div className="w-full flex-1">
            {/* Add Search Input here later if needed */}
          </div>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="secondary" size="icon" className="rounded-full">
                <Avatar className="h-8 w-8">
                  <AvatarImage src="" alt="User" />
                  <AvatarFallback>JD</AvatarFallback>
                </Avatar>
                <span className="sr-only">Toggle user menu</span>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuLabel>My Account</DropdownMenuLabel>
              <DropdownMenuSeparator />
              <DropdownMenuItem>Settings</DropdownMenuItem>
              <DropdownMenuItem>Support</DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem className="text-destructive">Logout</DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </header>

        {/* PAGE CONTENT */}
        <main className="flex flex-1 flex-col gap-4 p-4 lg:gap-6 lg:p-6 overflow-x-hidden">
          {children}
        </main>
      </div>
    </div>
  )
}
