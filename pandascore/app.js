const express = require("express")
const axios = require("axios")
const app = express()
const port = 3000

const apiKey = process.env.API_KEY
const RUNNING_MATCH_TYPE = "running"
const UPCOMING_MATCH_TYPE = "upcoming"

const cache = {}
const CACHE_EXPIRATION = Number(process.env.CACHE_EXPIRATION) || 5 * 60 * 1000 // 5 minutes in milliseconds

const handleProxyRequest = (matchType, req, res) => {
    if (cache[matchType] && (Date.now() - cache[matchType].timestamp < CACHE_EXPIRATION)) {
        return res.json(cache[matchType].data)
    }

    const url = `https://api.pandascore.co/csgo/matches/${matchType}?token=${apiKey}`

    axios.get(url)
        .then(response => {
            cache[matchType] = {
                data: response.data,
                timestamp: Date.now()
            }
            return res.json(response.data)
        })
        .catch(error => res.status(error.response?.status || 500).send(error.message))
}

// Health
app.get("/", (req, res) =>
    res.send("ALIVE"))

app.get("/api/csgo/matches/:type", (req, res) => {
    const matchType = req.params.type
    res.set("X-Pod", req.headers["X-Pod"])

    if (![RUNNING_MATCH_TYPE, UPCOMING_MATCH_TYPE].includes(matchType)) {
        return res.status(404).send(`The match type ${matchType} is not supported`)
    }
    handleProxyRequest(matchType, req, res)
})

app.get("/headers", (req, res) => {
    res.json({ headers: req.headers });
});

app.listen(port, () => console.log(`Server is running on http://localhost:${port}`))
