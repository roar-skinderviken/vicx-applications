import nextAuthMiddleware from 'next-auth/middleware'
import {NextRequest, NextResponse} from "next/server"

const authConfigMatchers: string[] = [
    "/user/dashboard",
    "/api/user:path*"
]

export function middleware(req: NextRequest) {
    if (authConfigMatchers.some(path => req.url.includes(path))) {
        return nextAuthMiddleware(req as any)
    }

    return NextResponse.next()
}

export const config = {
    matcher: ['/((?!_next|static|.*\\..*).*)']
}