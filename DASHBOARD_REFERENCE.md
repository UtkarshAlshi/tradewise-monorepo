# Dashboard Usage & Component Reference

## Quick Start

1. **Start the development server:**
```bash
cd tradewise/frontend/tradewise-client
npm run dev
```

2. **Access the dashboard:**
   - URL: http://localhost:3000/dashboard
   - You must be logged in (redirects to /login if not authenticated)

3. **View the app:**
   - Dashboard Overview: Shows KPI cards and portfolio info
   - Responsive layout: Resize window to see mobile navigation

---

## Component Structure

### DashboardNav Component
**Path:** `src/components/dashboard-nav.tsx`

Navigation items automatically highlight based on current route.

```tsx
const items = [
  { title: "Overview", href: "/dashboard", icon: LayoutDashboard },
  { title: "Portfolios", href: "/dashboard/portfolios", icon: PieChart },
  { title: "Strategies", href: "/dashboard/strategies", icon: FlaskConical },
  { title: "Backtesting", href: "/dashboard/backtest", icon: LineChart },
  { title: "Settings", href: "/dashboard/settings", icon: Settings },
]
```

**To add a new navigation item:**
1. Import the icon from lucide-react
2. Add to the `items` array
3. Create the corresponding page at the href path

---

### DashboardLayout Component
**Path:** `src/app/dashboard/layout.tsx`

This is the parent layout for all dashboard pages. All pages under `/dashboard/*` will use this layout.

**Key features:**
- `<Sheet>` component for mobile navigation drawer
- `<DropdownMenu>` for user profile menu
- `<Avatar>` for user profile image
- Responsive grid layout (flex-col on mobile, flex-row on desktop)

**To customize the layout:**
```tsx
// Modify the sidebar width
<div className="md:w-64 lg:w-72">  // Change these values

// Modify header height
<div className="h-14 lg:h-[60px]">  // Change these values

// Customize user menu items
<DropdownMenuItem>Custom Item</DropdownMenuItem>
```

---

### Dashboard Overview Page
**Path:** `src/app/dashboard/page.tsx`

Displays KPI metrics and portfolio information.

**Features:**
- Fetches user data from `/api/users/me`
- Fetches portfolios from `/api/portfolios`
- Shows real portfolio count in KPI cards
- Displays portfolio list with links

**To modify KPI cards:**
```tsx
// Update card title and icon
<CardTitle className="text-sm font-medium">Your Custom Title</CardTitle>
<YourIcon className="h-4 w-4 text-muted-foreground" />

// Update card values
<div className="text-2xl font-bold">Your Value</div>
```

---

## Styling & Customization

### Color Scheme
All colors use Tailwind's semantic classes for dark mode support:
- `text-muted-foreground` - Secondary text
- `bg-muted/40` - Subtle backgrounds
- `text-accent-foreground` - Highlighted text
- `text-destructive` - Error/delete actions

### Responsive Breakpoints
- `hidden md:block` - Hidden on mobile, visible on medium+ screens
- `md:grid-cols-2 lg:grid-cols-4` - 1 col mobile, 2 col tablet, 4 col desktop
- `shrink-0 md:hidden` - Visible on mobile only

### Dark Mode
Dark mode is automatically applied via the ThemeProvider in root layout.

---

## Adding New Dashboard Pages

### Step 1: Create Directory
```bash
mkdir -p src/app/dashboard/your-feature
```

### Step 2: Create page.tsx
```tsx
export default function YourFeaturePage() {
  return (
    <div className="flex flex-col gap-4">
      <h1 className="text-lg font-semibold md:text-2xl">Your Feature</h1>
      {/* Your content here */}
    </div>
  )
}
```

### Step 3: Add Navigation Item
In `src/components/dashboard-nav.tsx`:
```tsx
import { YourIcon } from 'lucide-react'

const items = [
  // ... existing items
  {
    title: "Your Feature",
    href: "/dashboard/your-feature",
    icon: YourIcon,
  },
]
```

---

## Backend Integration

### API Endpoints Used

**1. User Information**
```
GET /api/users/me
Headers: Authorization: Bearer {token}
Response: { id, email, createdAt }
```

**2. Portfolios List**
```
GET /api/portfolios
Headers: Authorization: Bearer {token}
Response: [{ id, name, description, createdAt, userId }]
```

### To add new API calls:
```tsx
// In your page component
const token = localStorage.getItem('token')
const response = await fetch('http://localhost:8000/api/your-endpoint', {
  headers: {
    'Authorization': `Bearer ${token}`,
  },
})
const data = await response.json()
```

---

## Common Tasks

### Change App Logo
In `src/app/dashboard/layout.tsx`:
```tsx
<Link href="/" className="flex items-center gap-2 font-semibold">
  <YourLogo className="h-6 w-6 text-primary" />
  <span>Your App Name</span>
</Link>
```

### Customize User Avatar
In `src/app/dashboard/layout.tsx`:
```tsx
<Avatar className="h-8 w-8">
  <AvatarImage src={user?.profileImage || "/placeholder.jpg"} />
  <AvatarFallback>{getInitials(user?.email)}</AvatarFallback>
</Avatar>
```

### Add User Email to Header
```tsx
<header className="flex h-14 items-center gap-4 border-b bg-muted/40 px-4">
  <div className="flex-1">
    <p className="text-sm text-muted-foreground">{user?.email}</p>
  </div>
</header>
```

### Change Logout Behavior
In `src/app/dashboard/layout.tsx`:
```tsx
<DropdownMenuItem 
  onClick={() => {
    // Add custom logout logic here
    localStorage.removeItem('token')
    // router.push('/login')
  }}
  className="text-destructive"
>
  Logout
</DropdownMenuItem>
```

---

## Available Icons (lucide-react)

Import and use any of these in navigation or cards:
```tsx
import {
  LayoutDashboard,    // Dashboard overview
  LineChart,          // Charts/analytics
  PieChart,           // Portfolio/assets
  FlaskConical,       // Experiments/strategies
  Settings,           // Configuration
  DollarSign,         // Finance/balance
  Activity,           // Active/status
  CreditCard,         // Assets/cards
  Users,              // People/count
  Menu,               // Mobile menu
  TrendingUp,         // Growth/performance
  LogOut,             // Sign out
} from 'lucide-react'
```

**Browse all icons:** https://lucide.dev/

---

## Troubleshooting

### Layout not showing correctly?
- Clear browser cache (`Cmd+Shift+Delete` on Mac)
- Restart dev server: `npm run dev`
- Check that `ThemeProvider` is in root layout.tsx

### API calls returning 401?
- Token might be expired
- Check browser console for error details
- Verify token exists: `localStorage.getItem('token')`

### Navigation not highlighting?
- Ensure page is under `/dashboard/*` path
- Check that href in DashboardNav matches the route
- Use `usePathname()` hook to debug: `console.log(pathname)`

### Mobile menu not working?
- Verify `Sheet` component is properly imported
- Check that `SheetTrigger` wraps the Menu button
- Ensure `md:hidden` class is on the button

---

## Performance Tips

1. **Lazy load images:** Use Next.js `Image` component
2. **Memoize components:** Wrap with `memo()` if they don't need frequent updates
3. **Cache API calls:** Use React Query or SWR for smart caching
4. **Optimize routes:** Use dynamic imports for heavy pages

---

## Next Features to Build

- [ ] Portfolio management page
- [ ] Strategy builder with drag-drop
- [ ] Backtesting results with charts
- [ ] Real-time market data display
- [ ] Notification system
- [ ] User settings and preferences
- [ ] Search functionality
- [ ] Mobile app view (PWA)

---

**Last Updated:** December 19, 2025
**Version:** 1.0 - Initial Dashboard Implementation
