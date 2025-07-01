import {NextRequest, NextResponse} from "next/server"
import {getServerSession} from "next-auth"
import authOptions from "@/auth/authOptions"
import {CustomSession} from "@/types/authTypes"

const SPRING_BACKEND_BASE_URL = process.env.SPRING_BACKEND_BASE_URL || ""

type SupportedHttpMethods = "PATCH" | "GET" | "POST" | "PUT" | "DELETE"

export const createHeaders = (
    accessToken?: string,
    contentType?: string
) => ({
    ...(accessToken ? {Authorization: `Bearer ${accessToken}`} : {}),
    ...(contentType && contentType !== "multipart/form-data" ? {"Content-Type": contentType} : {}),
})

export async function forwardRequest(
    request: NextRequest,
    endpoint: string,
    method: SupportedHttpMethods,
    contentType?: string,
    requiresAuthHeader: boolean = true
) {
    const accessToken = requiresAuthHeader
        ? ((await getServerSession(authOptions)) as CustomSession | null)?.accessToken
        : undefined

    if (requiresAuthHeader && !accessToken) {
        return NextResponse.json({message: "Unauthorized"}, {status: 401})
    }

    const requestBody = contentType === "multipart/form-data"
        ? await request.formData()
        : contentType
            ? request.body
            : undefined

    const fetchOptions: RequestInit = {
        method,
        headers: createHeaders(accessToken, contentType),
        ...(requestBody && {body: requestBody, duplex: "half" as const})
    }

    try {
        const response = await fetch(`${SPRING_BACKEND_BASE_URL}${endpoint}`, fetchOptions)

        return new NextResponse(response.body, {
            status: response.status,
            headers: response.headers,
        })
    } catch (error) {
        console.error("Error forwarding request:", error)
        return NextResponse.json({message: "Internal server error"}, {status: 500})
    }
}