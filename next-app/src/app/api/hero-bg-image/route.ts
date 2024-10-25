import * as path from "node:path"
import * as fs from "node:fs"
import {revalidateTag} from "next/cache"

const IMAGE_EXPIRATION_IN_SECS = 900 // 15 minutes
const IMAGE_URL = 'https://picsum.photos/1280/720'

const FALLBACK_IMAGE_PATH = path.join(process.cwd(), 'public', 'images', 'hero-fallback.jpg')
const FALLBACK_CACHE_TAG = "hero-image-cache"

const buildResponse = (buffer: Buffer) => new Response(
    buffer,
    {headers: {"Content-Type": "image/jpeg"}}
)

const buildFallbackResponse = () => {
    return buildResponse(fs.readFileSync(FALLBACK_IMAGE_PATH));
}

export async function GET() {
    try {
        const response = await fetch(
            IMAGE_URL,
            {
                next: {
                    revalidate: IMAGE_EXPIRATION_IN_SECS,
                    tags: [FALLBACK_CACHE_TAG]
                }
            }
        )

        if (!response.ok) {
            revalidateTag(FALLBACK_CACHE_TAG)
            return buildFallbackResponse()
        }

        const buffer = await response.arrayBuffer()
        return buildResponse(Buffer.from(buffer))
    } catch (error) {
        return buildFallbackResponse()
    }
}
