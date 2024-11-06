import {NextRequest, NextResponse} from "next/server"
import {getServerSession} from "next-auth"
import {authOptions} from "@/auth"

const BACKEND_BASE_URL = process.env.TOMCAT_BACKEND_SECURE_URL || "http://localhost:8080/api-secured/calculator"

export async function GET(request: NextRequest) {
    const {searchParams} = new URL(request.url)

    const firstValue = searchParams.get("first")
    const secondValue = searchParams.get("second")
    const operation = searchParams.get("operation")

    const session = await getServerSession(authOptions)

    // @ts-expect-error Because accessToken is not a prop of session
    const accessToken = session?.accessToken

    if (!accessToken) {
        return NextResponse.json({message: "Unauthorized"}, {status: 401})
    }

    const url = `${BACKEND_BASE_URL}/${firstValue}/${secondValue}/${operation}`

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
