const IMAGE_EXPIRATION_IN_SECS = 900 // 15 minutes
export const JPEG_IMAGE_CONTENT_TYPE = "image/png"
export const FALLBACK_CACHE_TAG = "hero-image-cache"
export const PICSUM_IMAGE_URL = 'https://picsum.photos/1280/720'
export const PICSUM_OPTIONS = {
    next: {
        revalidate: IMAGE_EXPIRATION_IN_SECS,
        tags: [FALLBACK_CACHE_TAG]
    }
}
