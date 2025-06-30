import {NextRequest} from "next/server"
import {forwardGraphQLRequestWithoutAuth} from "@/utils/apiUtils"

export async function POST(request: NextRequest) {
    return forwardGraphQLRequestWithoutAuth(request)
}