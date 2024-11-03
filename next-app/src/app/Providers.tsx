"use client"

import React from "react"
import {SessionProvider} from "next-auth/react"

export interface ProviderProps {
    children: React.ReactNode
}

export default function Providers({children}: ProviderProps) {
    return (
        <SessionProvider>
            {children}
        </SessionProvider>
    )
}