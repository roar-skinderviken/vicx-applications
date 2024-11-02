export const PANDASCORE_BASE_URL = "https://api.pandascore.co/csgo/matches"
export const RUNNING_MATCH_TYPE = "running"
export const UPCOMING_MATCH_TYPE = "upcoming"
export const CACHE_TAG_BASE = "pandascore-cache-"
const CACHE_TIMEOUT_IN_SECS = 30

export const pandaScoreFetchOptions = (cacheTag: string) => ({
    next: {
        revalidate: CACHE_TIMEOUT_IN_SECS,
        tags: [cacheTag],
    },
})
