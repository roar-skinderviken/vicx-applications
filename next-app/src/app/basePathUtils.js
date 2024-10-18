
const basePath = process.env.NEXT_PUBLIC_CUSTOM_BASE_PATH || '';

export function urlFromBasePath(url){
    return `${basePath}${url}`
}