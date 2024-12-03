import {NextRequest} from "next/server"
import {forwardRequest} from "@/utils/apiUtils"

export async function POST(request: NextRequest) {
    return forwardRequest(request, "/api/calculator", "POST", "application/json")
}

export async function DELETE(request: NextRequest) {
    return forwardRequest(request, "/api/calculator", "DELETE", "application/json")
}
