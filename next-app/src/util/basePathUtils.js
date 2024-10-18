

const basePath = process.env.NEXT_PUBLIC_CUSTOM_BASE_PATH || '';

export const urlFromBasePath = (url) => `${basePath}${url}`