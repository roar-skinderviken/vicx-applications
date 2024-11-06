export async function fetchData<T>(url: string, secured: boolean): Promise<T> {
    try {
        return await makeRequest(url, secured)
    } catch (error) {
        // Check if the error is due to expired token (status 401)

        console.log("Kommer hit 1", JSON.stringify(error))
        if (secured) {
            console.log("Kommer hit 2")
            // Attempt to refresh the token
            const tokenRefreshed = await refreshAccessToken()
            if (tokenRefreshed) {
                console.log("Kommer hit 3")
                // Retry the original request
                return await makeRequest(url, secured)
            }
        }
        throw error
    }
}

// Helper function to make the fetch request
async function makeRequest<T>(url: string, secured: boolean): Promise<T> {
    const response = await fetch(url, {
        method: 'GET',
        credentials: secured ? "include" : "omit",
    })

    if (!response.ok) {
        throw response
    }

    return await response.json()
}

async function refreshAccessToken() {
    const response = await fetch('/api/refresh-token', {
        method: 'POST',
        credentials: 'include', // to include HttpOnly cookies in the request
    })

    if (response.ok) {
        const data = await response.json()
        console.log("New access token", data.access_token)
        return data.access_token // Return the new access token if needed on client-side
    } else {
        console.error('Failed to refresh token')
        // Handle token refresh failure, e.g., redirect to login
    }
}