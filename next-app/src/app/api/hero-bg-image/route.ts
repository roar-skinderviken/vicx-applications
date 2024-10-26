import * as path from "node:path"
import * as fs from "node:fs"
import {revalidateTag} from "next/cache"

const IMAGE_EXPIRATION_IN_SECS = 900 // 15 minutes
const FALLBACK_IMAGE_PATH = path.join(process.cwd(), 'public', 'images', 'hero-fallback.jpg')

export const JPEG_IMAGE_CONTENT_TYPE = "image/png"
export const FALLBACK_CACHE_TAG = "hero-image-cache"
export const PICSUM_IMAGE_URL = 'https://picsum.photos/1280/720'
export const PICSUM_OPTIONS = {
    next: {
        revalidate: IMAGE_EXPIRATION_IN_SECS,
        tags: [FALLBACK_CACHE_TAG]
    }
}

const buildResponse = (buffer: Buffer) => new Response(
    buffer,
    {headers: {"Content-Type": JPEG_IMAGE_CONTENT_TYPE}}
)

const buildFallbackResponse = () => {
    return buildResponse(fs.readFileSync(FALLBACK_IMAGE_PATH))
}

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
