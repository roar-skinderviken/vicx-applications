import * as path from "node:path"
import * as fs from "node:fs"

const IMAGE_EXPIRATION_IN_SECS = 900 // 15 minutes
const IMAGE_URL = 'https://picsum.photos/1280/720'

const FALLBACK_IMAGE_PATH = path.join(process.cwd(), 'public', 'images', 'hero-fallback.jpg')

const buildResponse = (buffer) => new Response(
    buffer,
    {headers: {"Content-Type": "image/jpeg"}}
)

const buildFallbackResponse = () =>
    buildResponse(fs.readFileSync(FALLBACK_IMAGE_PATH))

export async function GET() {
    try {
        const response = await fetch(
            IMAGE_URL,
            {next: {revalidate: IMAGE_EXPIRATION_IN_SECS}}
        )

        if (!response.ok) {
            return buildFallbackResponse()
        }

        return buildResponse(response.body)
    } catch (error) {
        return buildFallbackResponse()
    }
}
