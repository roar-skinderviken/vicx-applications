import { NextRequest } from "next/server"
import { forwardRequest } from "@/utils/apiUtils"

export async function PATCH(request: NextRequest) {
    return forwardRequest(request, "/api/user", "PATCH", "application/json")
}