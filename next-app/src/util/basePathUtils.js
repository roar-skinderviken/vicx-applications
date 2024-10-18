

const basePath = process.env.NEXT_PUBLIC_BASE_PATH || '';

export const urlFromBasePath = (url) => `${basePath}${url}`