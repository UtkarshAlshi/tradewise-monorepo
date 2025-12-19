# Dashboard Architecture & Visual Structure

## Visual Layout

### Desktop View (≥768px)
```
┌─────────────────────────────────────────────────────────────────┐
│                      Header / Navigation Bar                     │
│  ┌─────────────┐                               │ User Avatar ▼  │
│  │ TradeWise   │  [Search/Breadcrumb]          │ │ Settings    │
│  │ Logo        │                               │ │ Support     │
│  └─────────────┘                               │ │ Logout      │
├──────────────┬────────────────────────────────────────────────────┤
│              │                                                    │
│  Dashboard   │                                                    │
│  ▸ Overview  │                    MAIN CONTENT AREA              │
│  ▸ Portfolio │                                                    │
│  ▸ Strategies│     [Dynamic Page Content Renders Here]          │
│  ▸ Backtest  │                                                    │
│  ▸ Settings  │                                                    │
│              │                                                    │
│              │                                                    │
│              │                                                    │
│              │                                                    │
│              │                                                    │
├──────────────┴────────────────────────────────────────────────────┤
│                        Footer (Optional)                          │
└─────────────────────────────────────────────────────────────────┘
```

### Tablet/Mobile View (<768px)
```
┌──────────────────────────────────────┐
│ ☰ Menu  [Logo]   User Avatar ▼      │
│ │ Overview                          │
│ │ Portfolio                         │
│ │ Strategies                        │
│ │ Backtest                          │
│ │ Settings                          │
├──────────────────────────────────────┤
│                                      │
│    MAIN CONTENT AREA                 │
│    (Full Width)                      │
│                                      │
│                                      │
│                                      │
│                                      │
│                                      │
│                                      │
│                                      │
└──────────────────────────────────────┘
```

---

## Component Hierarchy

```
DashboardLayout (src/app/dashboard/layout.tsx)
│
├── Desktop Sidebar (hidden on mobile)
│   ├── Logo Header
│   │   └── TrendingUp Icon + "TradeWise"
│   └── Navigation
│       └── DashboardNav Component
│           ├── Overview Link
│           ├── Portfolios Link
│           ├── Strategies Link
│           ├── Backtesting Link
│           └── Settings Link
│
├── Main Content Flex Container
│   │
│   ├── Header Section
│   │   ├── Mobile Menu Button (Sheet Trigger)
│   │   │   └── Sheet Component (Mobile Navigation Drawer)
│   │   │       └── DashboardNav Component (Duplicated for mobile)
│   │   │
│   │   └── User Menu Dropdown
│   │       ├── Avatar Component
│   │       │   └── AvatarImage + AvatarFallback
│   │       └── Dropdown Menu Items
│   │           ├── My Account
│   │           ├── Settings
│   │           ├── Support
│   │           └── Logout
│   │
│   └── Main Content Area
│       └── {children} - Dynamic Page Content
│           │
│           └── Dashboard Page (src/app/dashboard/page.tsx)
│               ├── Page Title
│               ├── KPI Cards Grid
│               │   ├── Total Balance Card
│               │   ├── Active Strategies Card
│               │   ├── Portfolio Assets Card
│               │   └── Notifications Card
│               │
│               └── Dashboard Content Grid
│                   ├── Portfolio Performance Card
│                   │   ├── Card Header
│                   │   ├── Card Description
│                   │   └── Chart Placeholder
│                   │
│                   └── Recent Activity Card
│                       ├── Card Header
│                       ├── Card Description
│                       └── Activity List Items
```

---

## File Organization

```
src/
│
├── components/
│   ├── dashboard-nav.tsx              ← Navigation component
│   ├── theme-provider.tsx             ← Dark mode wrapper
│   │
│   └── ui/                            ← Shadcn/UI components
│       ├── avatar.tsx                 ← User profile picture
│       ├── badge.tsx                  ← Status indicators
│       ├── button.tsx                 ← Reusable buttons
│       ├── card.tsx                   ← Content containers
│       ├── dropdown-menu.tsx          ← User menu
│       ├── form.tsx                   ← Form handling
│       ├── input.tsx                  ← Input fields
│       ├── label.tsx                  ← Form labels
│       ├── separator.tsx              ← Visual dividers
│       └── sheet.tsx                  ← Mobile drawer
│
└── app/
    │
    ├── layout.tsx                     ← Root app layout
    ├── globals.css                    ← Global styles
    ├── page.tsx                       ← Home page (/)
    │
    ├── login/
    │   └── page.tsx                   ← Login page
    │
    ├── register/
    │   └── page.tsx                   ← Registration page
    │
    ├── dashboard/
    │   ├── layout.tsx                 ← Dashboard parent layout
    │   ├── page.tsx                   ← Dashboard overview
    │   │
    │   ├── portfolios/
    │   │   └── page.tsx               ← (Future) Portfolio list
    │   │
    │   ├── strategies/
    │   │   └── page.tsx               ← (Future) Strategies page
    │   │
    │   ├── backtest/
    │   │   └── page.tsx               ← (Future) Backtesting page
    │   │
    │   ├── settings/
    │   │   └── page.tsx               ← (Future) User settings
    │   │
    │   └── portfolio/
    │       └── [id]/
    │           └── page.tsx           ← Portfolio detail view
    │
    ├── leaderboard/
    │   └── page.tsx                   ← Leaderboard page
    │
    └── strategies/
        └── page.tsx                   ← Strategies list
```

---

## Data Flow Diagram

### Authentication Flow
```
User Login (http://localhost:3000/login)
        ↓
    Submit Credentials
        ↓
API Request → http://localhost:8000/api/auth/login
        ↓
Backend Returns JWT Token
        ↓
Store Token in localStorage
        ↓
Redirect to /dashboard
        ↓
Layout checks for token
        ↓
If valid → Show Dashboard
If invalid → Redirect to /login
```

### Data Fetching Flow
```
Dashboard Page Loads
        ↓
useEffect Hook Triggered
        ↓
GET /api/users/me (with JWT header)
GET /api/portfolios (with JWT header)
        ↓ (Parallel requests)
        ↓
Backend Returns User Data
Backend Returns Portfolio List
        ↓
setState() Updates Component
        ↓
Render KPI Cards with data
Render Portfolio List with data
```

### Navigation Flow
```
User Clicks Navigation Link
        ↓
Next.js Router Updates URL
        ↓
Dashboard Layout Persists
        ↓
Page Component (child) Re-renders
        ↓
DashboardNav Detects Active Route
        ↓
Highlights Current Nav Item
```

---

## Responsive Design Breakpoints

```
Mobile (0px - 640px)
├── Sidebar: HIDDEN
├── Menu Button: VISIBLE
├── Header: Simplified
├── Content: Full Width
└── Grid: 1 Column

Tablet (641px - 1024px)
├── Sidebar: HIDDEN
├── Menu Button: VISIBLE
├── Header: Normal
├── Content: Full Width
└── Grid: 2 Columns

Desktop (1025px+)
├── Sidebar: VISIBLE (260-288px)
├── Menu Button: HIDDEN
├── Header: Full Width
├── Content: Flex Layout
└── Grid: 4 Columns

Tailwind Classes Used:
- md: (≥768px) - Tablet and up
- lg: (≥1024px) - Desktop and up
```

---

## State Management Flow

```
DashboardLayout
├── Children (Dashboard Page)
│   │
│   ├── State: user (User object)
│   ├── State: portfolios (Portfolio[])
│   ├── State: loading (boolean)
│   ├── State: error (string)
│   │
│   └── Effects:
│       ├── On Mount → Fetch user data & portfolios
│       ├── On Error → Clear token, redirect to login
│       └── On Success → Update state, render content
│
└── Derived UI:
    ├── Loading State → Show spinner
    ├── Error State → Show error message
    └── Success State → Render dashboard with data
```

---

## API Integration Points

```
┌─────────────────────────────────┐
│     Frontend (Next.js)          │
├─────────────────────────────────┤
│                                 │
│  Dashboard Page Component       │
│                                 │
│  ├─ fetch('/api/users/me')      │
│  │  Authorization: Bearer JWT   │
│  │  └─ Sets user state          │
│  │                              │
│  └─ fetch('/api/portfolios')    │
│     Authorization: Bearer JWT   │
│     └─ Sets portfolios state    │
│                                 │
└─────────────────────────────────┘
         ↓ API Calls ↓
┌─────────────────────────────────┐
│  Backend (Spring Boot)          │
├─────────────────────────────────┤
│                                 │
│  API Gateway (Port 8000)        │
│  ├─ /api/users/me               │
│  │  └─ Routes to user-service   │
│  │                              │
│  └─ /api/portfolios             │
│     └─ Routes to portfolio-svc  │
│                                 │
└─────────────────────────────────┘
         ↓ Service Layer ↓
┌─────────────────────────────────┐
│  Microservices & Databases      │
├─────────────────────────────────┤
│                                 │
│  ├─ user-service → users table  │
│  └─ portfolio-svc → portfolios  │
│                                 │
└─────────────────────────────────┘
```

---

## CSS Class Hierarchy

### Layout Classes
```
.flex                           → Display: flex
.min-h-screen                   → Minimum height: viewport
.flex-col                       → Flex direction: column
.md:flex-row                    → Flex direction: row on tablets+
.border-r                       → Right border
.border-b                       → Bottom border
```

### Spacing Classes
```
.gap-2 to .gap-6               → Gaps between elements
.px-4, .py-2                   → Padding (x-axis, y-axis)
.p-6                           → Padding all sides
.mb-4, .mt-2                   → Margin bottom/top
```

### Color Classes
```
.bg-muted/40                   → Subtle background
.text-muted-foreground         → Secondary text
.text-accent-foreground        → Highlighted text
.text-destructive              → Error/delete color
```

### Responsive Classes
```
.hidden                         → Display: none
.md:block                       → Display: block on tablets+
.md:grid-cols-2                → 2 columns on tablets+
.lg:grid-cols-4                → 4 columns on desktop+
.shrink-0 md:hidden            → Mobile-only element
```

---

## Component Props & Usage

### DashboardNav
```tsx
<DashboardNav />
// No props - reads pathname internally using usePathname() hook
```

### DashboardLayout
```tsx
<DashboardLayout>
  {children} {/* Page components render here */}
</DashboardLayout>
```

### Card Components
```tsx
<Card>
  <CardHeader>
    <CardTitle>Title</CardTitle>
    <CardDescription>Description</CardDescription>
  </CardHeader>
  <CardContent>
    Content here
  </CardContent>
</Card>
```

### Avatar
```tsx
<Avatar>
  <AvatarImage src="url" alt="name" />
  <AvatarFallback>JD</AvatarFallback>
</Avatar>
```

---

## Performance Considerations

1. **Component Tree Depth:** 4 levels max (shallow is better)
2. **Re-render Optimization:** Use useCallback for event handlers
3. **Image Optimization:** Use Next.js Image component (not implemented yet)
4. **Bundle Size:** Shadcn/UI components are tree-shakeable
5. **Caching:** API responses could be cached with React Query (future enhancement)

---

## Accessibility Features

✓ Semantic HTML structure
✓ ARIA labels on interactive elements
✓ Keyboard navigation support
✓ Focus management
✓ Color contrast compliance (WCAG AA)
✓ Icon labels for screen readers

---

**Generated:** December 19, 2025
**Version:** 1.0
**Status:** Complete & Ready for Feature Development
