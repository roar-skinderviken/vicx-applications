import * as path from "node:path"
import * as fs from "node:fs"
import {revalidateTag} from "next/cache"
import {
    FALLBACK_CACHE_TAG,
    JPEG_IMAGE_CONTENT_TYPE,
    PICSUM_IMAGE_URL,
    PICSUM_OPTIONS
} from "@/constants/picsumConstants"

const FALLBACK_IMAGE_PATH = path.join(process.cwd(), 'public', 'images', 'hero-fallback.jpg')

const buildFallbackResponse = () => {
    return buildResponse(fs.readFileSync(FALLBACK_IMAGE_PATH))
}

const buildResponse = (buffer: Buffer) => new Response(
    buffer,
    {headers: {"Content-Type": JPEG_IMAGE_CONTENT_TYPE}}
)

export async function GET() {
    try {
        const response = await fetch(
            PICSUM_IMAGE_URL,
            PICSUM_OPTIONS
        )

        if (!response.ok) {
            revalidateTag(FALLBACK_CACHE_TAG)
            return buildFallbackResponse()
        }

        const buffer = await response.arrayBuffer()
        return buildResponse(Buffer.from(buffer))
    } catch (error) {
        console.error(error)
        return buildFallbackResponse()
    }
}
