import { NextRequest } from "next/server"
import { forwardRequest } from "@/utils/apiUtils"

export async function POST(request: NextRequest) {
    return forwardRequest(request, "/api/user", "POST", "multipart/form-data", false)
}