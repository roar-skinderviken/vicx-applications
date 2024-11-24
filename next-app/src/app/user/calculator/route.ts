import {NextRequest, NextResponse} from "next/server"
import {getServerSession} from "next-auth"
import authOptions from "@/authOptions"
import {CustomSession} from "@/types/authTypes"

const SPRING_BACKEND_BASE_URL = process.env.SPRING_BACKEND_BASE_URL || ""
const BACKEND_BASE_URL = `${SPRING_BACKEND_BASE_URL}/api/calculator`

export async function POST(request: NextRequest) {
    const session = (await getServerSession(authOptions)) as CustomSession | null
    const accessToken = session?.accessToken

    if (!accessToken) {
        return NextResponse.json({message: "Unauthorized"}, {status: 401})
    }

    const fetchOptions = {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${accessToken}`,
            'Content-Type': 'application/json'
        },
        body: request.body,
        duplex: "half"
    }

    try {
        const response = await fetch(BACKEND_BASE_URL, fetchOptions)

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

export async function DELETE(request: NextRequest) {
    const session = (await getServerSession(authOptions)) as CustomSession | null
    const accessToken = session?.accessToken

    if (!accessToken) {
        return NextResponse.json({message: "Unauthorized"}, {status: 401})
    }

    const fetchOptions = {
        method: "DELETE",
        headers: {
            "Authorization": `Bearer ${accessToken}`,
            'Content-Type': 'application/json'
        },
        body: request.body,
        duplex: "half"
    }

    try {
        const response = await fetch(BACKEND_BASE_URL, fetchOptions)
        return new NextResponse(null, {status: response.status})
    } catch (error) {
        console.error("Error deleting calculation entries: ", error)
        return new NextResponse(null, {status: 500})
    }
}
