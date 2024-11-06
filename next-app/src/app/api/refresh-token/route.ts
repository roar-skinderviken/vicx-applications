import {NextResponse} from "next/server"
import {cookies} from "next/headers"
import {REFRESH_TOKEN_COOKIE} from "@/constants/cookieConstants"

export async function POST() {
    const cookieStore = await cookies()
    const refreshToken = cookieStore.get(REFRESH_TOKEN_COOKIE)?.value

    console.log("KOMMER HIT 1", refreshToken)

    if (!refreshToken) {
        return NextResponse.json({message: "No refresh token available"}, {status: 401})
    }

    console.log("KOMMER HIT 2")

    try {
        // Make a request to the Spring Boot OAuth2 token endpoint to refresh the token
        const response =
            await fetch(process.env.TOKEN_URL || "http://localhost:9000/auth-server/oauth2/token",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded",
                    },
                    body: new URLSearchParams({
                        grant_type: "refresh_token",
                        refresh_token: refreshToken,
                        client_id: "next-app-client",
                        client_secret: process.env.CLIENT_SECRET || "secret",
                    } as Record<string, string>),
                })

        if (!response.ok) {
            console.log("KOMMER HIT 3", response)
            return NextResponse.json({message: "Failed to refresh token"}, {status: 500})
        }

        const data = await response.json()

        // Set the refreshed tokens in HttpOnly cookies
        cookieStore.set({
            name: "access_token",
            value: data.access_token,
            httpOnly: true,
            secure: process.env.NODE_ENV === "production",
            maxAge: data.expires_in,
            path: "/",
        })

        return NextResponse.json({access_token: data.access_token})
    } catch (error) {
        console.error("Error refreshing token:", error)
        return NextResponse.json({message: "Internal server error"}, {status: 500})
    }
}
