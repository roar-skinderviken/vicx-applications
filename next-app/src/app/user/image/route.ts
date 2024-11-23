import {NextResponse} from "next/server"
import {getServerSession} from "next-auth"
import authOptions from "@/authOptions"
import {CustomSession} from "@/types/authTypes"

const BACKEND_BASE_URL = process.env.USER_IMAGE_BACKEND_URL || "/backend-spring-boot/api/user/image"

const buildResponse = (contentType: string, buffer: Buffer) => new Response(
    buffer,
    {headers: {"Content-Type": contentType}}
)

export async function GET() {
    const session = (await getServerSession(authOptions)) as CustomSession | null
    const accessToken = session?.accessToken

    if (!accessToken) {
        return NextResponse.json({message: "Unauthorized"}, {status: 401})
    }

    const fetchOptions = {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${accessToken}`
        }
    }

    try {
        const response = await fetch(`${BACKEND_BASE_URL}/${session.user?.id}`, fetchOptions)

        if (!response.ok) {
            console.error("Failed to fetch image from backend:", response.statusText)
            return NextResponse.json(
                {message: "Failed to fetch user image"},
                {status: response.status}
            )
        }

        const contentType = response.headers.get("Content-Type") || "application/octet-stream"
        const imageBuffer = await response.arrayBuffer()

        return buildResponse(contentType, Buffer.from(imageBuffer))
    } catch (error) {
        console.error("Error fetching user image:", error)
        return NextResponse.json({message: "Internal server error"}, {status: 500})
    }
}