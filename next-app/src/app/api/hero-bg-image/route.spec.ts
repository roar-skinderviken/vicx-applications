import {FALLBACK_CACHE_TAG, GET, JPEG_IMAGE_CONTENT_TYPE, PICSUM_IMAGE_URL, PICSUM_OPTIONS} from "./route"
import * as fs from "node:fs"
import {revalidateTag} from "next/cache"

jest.mock('node:fs')
jest.mock('next/cache', () => ({
    revalidateTag: jest.fn(),
}))

describe('GET /api/hero', () => {
    const FALLBACK_IMAGE_BUFFER = Buffer.from('fallback image')
    const REMOTE_IMAGE_BUFFER = Buffer.from('remote image')
    const REMOTE_IMAGE_BASE64 = REMOTE_IMAGE_BUFFER.toString('base64')

    beforeEach(() => fetchMock.resetMocks())

    it("returns a remote image on a successful fetch", async () => {
        fetchMock.mockResponseOnce(Buffer.from(REMOTE_IMAGE_BASE64, 'base64').toString())

        const response = await GET()
        const buffer = await response.arrayBuffer()

        expect(response.headers.get("Content-Type")).toBe(JPEG_IMAGE_CONTENT_TYPE)
        expect(Buffer.from(buffer)).toEqual(REMOTE_IMAGE_BUFFER)

        expect(fetch).toHaveBeenCalledWith(PICSUM_IMAGE_URL, PICSUM_OPTIONS)
        expect(revalidateTag).not.toHaveBeenCalled()
    })

    it("returns fallback image when status is not OK", async () => {
        (fs.readFileSync as jest.Mock).mockReturnValue(FALLBACK_IMAGE_BUFFER)
        fetchMock.mockResponseOnce("", { status: 500 })

        const response = await GET()
        const buffer = await response.arrayBuffer()

        expect(response.headers.get("Content-Type")).toBe(JPEG_IMAGE_CONTENT_TYPE)
        expect(Buffer.from(buffer)).toEqual(FALLBACK_IMAGE_BUFFER)

        expect(revalidateTag).toHaveBeenCalledWith(FALLBACK_CACHE_TAG)
    })
})
