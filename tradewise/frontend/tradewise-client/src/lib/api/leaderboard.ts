import { apiFetch } from "@/lib/api/client";
import type { LeaderboardEntryResponse } from "@/types/leaderboard";

export function getLeaderboard() {
  return apiFetch<LeaderboardEntryResponse[]>("/api/leaderboard");
}
