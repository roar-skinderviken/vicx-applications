const express = require('express');
const axios = require('axios');
const app = express();
const port = 3000;

const apiKey = process.env.API_KEY;

app.get('/', (req, res) => {
    res.send('Hello World!');
});

// Proxy request for running matches
app.get('/api/csgo/matches/running', (req, res) => {
    const url = `https://api.pandascore.co/csgo/matches/running?token=${apiKey}`;
    axios.get(url)
        .then(response => {
            res.json(response.data);
        })
        .catch(error => {
            res.status(error.response?.status || 500).send(error.message);
        });
});

// Proxy request for upcoming matches
app.get('/api/csgo/matches/upcoming', (req, res) => {
    const url = `https://api.pandascore.co/csgo/matches/upcoming?token=${apiKey}`;
    axios.get(url)
        .then(response => {
            res.json(response.data);
        })
        .catch(error => {
            res.status(error.response?.status || 500).send(error.message);
        });
});

app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});
