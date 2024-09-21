const express = require("express")
const axios = require("axios")
const app = express()
const port = 3000

const apiKey = process.env.API_KEY
const RUNNING_MATCH_TYPE = "running"
const UPCOMING_MATCH_TYPE = "upcoming"

function handleProxyRequest(matchType, req, res) {
    const url = `https://api.pandascore.co/csgo/matches/${matchType}?token=${apiKey}`
    axios.get(url)
        .then(response => res.json(response.data))
        .catch(error => res.status(error.response?.status || 500).send(error.message))
}

// Health
app.get("/", (req, res) =>
    res.send("ALIVE"))

app.get("/api/csgo/matches/:type", (req, res) => {
    const matchType = req.params.type
    if (![RUNNING_MATCH_TYPE, UPCOMING_MATCH_TYPE].includes(matchType)) {
        return res.status(404).send(`The match type ${matchType} is not supported`)
    }
    handleProxyRequest(matchType, req, res)
})

app.listen(port, () => console.log(`Server is running on http://localhost:${port}`))
