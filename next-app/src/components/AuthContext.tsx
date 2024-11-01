"use client"

import { SessionProvider } from "next-auth/react"
import React from "react"

export interface AuthContextProps {
    children: React.ReactNode
}

export default function AuthContext({ children }: AuthContextProps) {
    return <SessionProvider>{children}</SessionProvider>
}