import {NextRequest} from "next/server"
import {forwardRequest} from "@/utils/apiUtils"

const USER_IMAGE_ENDPOINT = "/api/user/image"

export async function POST(request: NextRequest) {
    return forwardRequest(request, USER_IMAGE_ENDPOINT, "POST", "multipart/form-data")
}

export async function GET(request: NextRequest) {
    return forwardRequest(request, USER_IMAGE_ENDPOINT, "GET")
}

export async function DELETE(request: NextRequest) {
    return forwardRequest(request, USER_IMAGE_ENDPOINT, "DELETE")
}
