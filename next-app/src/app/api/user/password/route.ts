import { NextRequest } from "next/server"
import { handleRequest } from "@/utils/apiUtils"

export async function PATCH(request: NextRequest) {
    return handleRequest(request, "/api/user/password", "PATCH")
}