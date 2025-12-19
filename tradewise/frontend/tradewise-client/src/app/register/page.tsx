"use client"

import { useState } from "react"
import Link from "next/link"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { TrendingUp, AlertCircle, CheckCircle } from "lucide-react"

export default function RegisterPage() {
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [confirmPassword, setConfirmPassword] = useState("")
  const [message, setMessage] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [messageType, setMessageType] = useState<"success" | "error" | null>(null)
  const router = useRouter()

  const handleRegister = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setMessage("")

    if (password !== confirmPassword) {
      setMessage("Passwords do not match")
      setMessageType("error")
      return
    }

    if (password.length < 8) {
      setMessage("Password must be at least 8 characters long")
      setMessageType("error")
      return
    }

    setIsLoading(true)

    try {
      const apiBase = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8000"
      const url = `${apiBase.replace(/\/$/, "")}/api/auth/register`

      const controller = new AbortController()
      const timeout = setTimeout(() => controller.abort(), 10000)

      const res = await fetch(url, {
        method: "POST",
        mode: "cors",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
        signal: controller.signal,
      })
      clearTimeout(timeout)

      const contentType = res.headers.get("content-type") || ""
      let payload: string = ""
      if (contentType.includes("application/json")) {
        const json = await res.json()
        payload = json?.message ?? JSON.stringify(json)
      } else {
        payload = await res.text()
      }

      if (res.ok) {
        setMessage("Account created successfully! Redirecting to login...")
        setMessageType("success")
        setTimeout(() => {
          router.push("/login")
        }, 1500)
      } else {
        setMessage(`Error: ${payload || res.statusText}`)
        setMessageType("error")
      }
    } catch (error) {
      if ((error as any)?.name === "AbortError") {
        setMessage("Error: Request timed out. Please try again.")
      } else {
        console.error("Registration error:", error)
        setMessage("Error: Failed to connect to the server. Check that the backend is running.")
      }
      setMessageType("error")
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-background px-4 py-12">
      <Card className="w-full max-w-md border border-border shadow-lg">
        <CardHeader className="space-y-1 text-center">
          <div className="flex justify-center mb-4">
            <div className="p-3 rounded-full bg-primary/10">
              <TrendingUp className="h-8 w-8 text-primary" />
            </div>
          </div>
          <CardTitle className="text-2xl font-bold">Create your account</CardTitle>
          <CardDescription>
            Start your journey with TradeWise today
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleRegister} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="email" className="text-sm font-medium">
                Email
              </Label>
              <Input
                id="email"
                type="email"
                placeholder="name@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                disabled={isLoading}
                className="h-10"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="password" className="text-sm font-medium">
                Password
              </Label>
              <Input
                id="password"
                type="password"
                placeholder="At least 8 characters"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                minLength={8}
                required
                disabled={isLoading}
                className="h-10"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="confirmPassword" className="text-sm font-medium">
                Confirm Password
              </Label>
              <Input
                id="confirmPassword"
                type="password"
                placeholder="Confirm your password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                minLength={8}
                required
                disabled={isLoading}
                className="h-10"
              />
            </div>

            {message && (
              <div
                className={`flex items-center gap-2 rounded-md p-3 text-sm ${
                  messageType === "success"
                    ? "bg-green-500/10 text-green-600"
                    : "bg-red-500/10 text-red-600"
                }`}
              >
                {messageType === "success" ? (
                  <CheckCircle className="h-4 w-4" />
                ) : (
                  <AlertCircle className="h-4 w-4" />
                )}
                <span>{message}</span>
              </div>
            )}

            <Button type="submit" className="w-full h-10" disabled={isLoading}>
              {isLoading ? "Creating account..." : "Create Account"}
            </Button>
          </form>
        </CardContent>
        <CardFooter className="flex flex-col space-y-4 border-t border-border pt-6">
          <p className="text-center text-sm text-muted-foreground">
            Already have an account?{" "}
            <Link
              href="/login"
              className="font-medium text-primary hover:underline"
            >
              Sign in
            </Link>
          </p>
        </CardFooter>
      </Card>
    </div>
  )
}