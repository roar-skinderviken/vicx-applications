import { NextRequest, NextResponse } from "next/server"
import { getServerSession } from "next-auth"
import authOptions from "@/auth/authOptions"
import { CustomSession } from "@/types/authTypes"

const SPRING_BACKEND_BASE_URL = process.env.SPRING_BACKEND_BASE_URL || ""

type SupportedHttpMethods = "PATCH" | "GET" | "POST" | "PUT" | "DELETE"

export const createHeaders = (
    accessToken: string,
    contentType?: string
) => ({
    Authorization: `Bearer ${accessToken}`,
    ...(contentType && { "Content-Type": contentType }),
})

export async function forwardRequest(
    request: NextRequest,
    endpoint: string,
    method: SupportedHttpMethods,
    contentType?: string
) {
    const session = (await getServerSession(authOptions)) as CustomSession | null
    const accessToken = session?.accessToken

    if (!accessToken) {
        return NextResponse.json({ message: "Unauthorized" }, { status: 401 })
    }

    const fetchOptions = {
        method,
        headers: createHeaders(accessToken, contentType),
        ...(contentType && { body: request.body, duplex: "half" as const }),
    }

    try {
        const response =
            await fetch(`${SPRING_BACKEND_BASE_URL}${endpoint}`, fetchOptions)

        return new NextResponse(response.body, {
            status: response.status,
            headers: response.headers,
        })
    } catch (error) {
        console.error("Error forwarding request:", error)
        return NextResponse.json({ message: "Internal server error" }, { status: 500 })
    }
}
