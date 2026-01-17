import type { Metadata } from "next";
import { Geist } from "next/font/google";
import { Geist_Mono } from "next/font/google";
import "./globals.css";
import { ThemeProvider } from "@/components/theme-provider";
import { WebSocketProvider } from '@/app/context/WebSocketContext';
import { Toaster } from 'react-hot-toast';
import NotificationHandler from '@/components/notification-handler';
import { AppSidebar } from "@/components/app-sidebar";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "TradeWise",
  description: "Real-Time Investment & Portfolio Analytics Platform",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <head />
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased`}
        suppressHydrationWarning={true}
      >
        <ThemeProvider
          attribute="class"
          defaultTheme="dark"
          enableSystem
          disableTransitionOnChange
        >
          <WebSocketProvider>
            <Toaster position="top-right" />
            <NotificationHandler />
            <div className="flex h-full">
              <AppSidebar />
              <main className="flex-1 md:pl-64 h-full">
                {children}
              </main>
            </div>
          </WebSocketProvider>
        </ThemeProvider>
      </body>
    </html>
  );
}