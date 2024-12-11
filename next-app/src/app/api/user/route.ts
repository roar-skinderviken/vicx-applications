import { NextRequest } from "next/server"
import { forwardRequest } from "@/utils/apiUtils"

const USER_ENDPOINT = "/api/user"

export async function GET(request: NextRequest) {
    return forwardRequest(request, USER_ENDPOINT, "GET")
}

export async function PATCH(request: NextRequest) {
    return forwardRequest(request, USER_ENDPOINT, "PATCH", "application/json")
}