const express = require('express');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 4000; // Use port 4000 or the port set by environment

// Serve static files from the 'public' directory
app.use(express.static(path.join(__dirname, 'public')));

// Define a route to handle incoming requests
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'snake_game.html'));
});

// Start the server
app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});