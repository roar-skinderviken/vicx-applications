import {NextRequest, NextResponse} from "next/server"
import {getServerSession} from "next-auth"
import authOptions from "@/authOptions"
import {CustomSession} from "@/types/authTypes"

const BACKEND_BASE_URL = process.env.TOMCAT_BACKEND_SECURED_URL || "http://localhost:8080/api-secured/calculator"

export async function GET(request: NextRequest) {
    const session = (await getServerSession(authOptions)) as CustomSession | null
    const accessToken = session?.accessToken

    if (!accessToken) {
        return NextResponse.json({message: "Unauthorized"}, {status: 401})
    }

    const {first, second, operation} =
        Object.fromEntries(new URL(request.url).searchParams.entries())

    const url = `${BACKEND_BASE_URL}/${first}/${second}/${operation}`

    try {
        // Make a request to the Spring Boot OAuth2 token endpoint to refresh the token
        const response = await fetch(
            url,
            {headers: {"Authorization": `Bearer ${accessToken}`}}
        )

        if (!response.ok) {
            return NextResponse.json(
                {message: "Failed to fetch calculator result"},
                {status: 500}
            )
        }

        const data = await response.json()
        return NextResponse.json(data)
    } catch (error) {
        console.error("Error fetching calculator result:", error)
        return NextResponse.json({message: "Internal server error"}, {status: 500})
    }
}
