const express = require('express');
const app = express();

let clickCount = 0;

app.get('/', (req, res) => {
    res.send(`
    <!DOCTYPE html>
    <html lang="en">
    <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Click Counter</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <style>
    body {
        background-color: #f0f0f0;
        padding-top: 50px;
    }
    .container {
        text-align: center;
        margin-top: 50px;
    }
    .btn-primary {
        background-color: #007bff;
        border-color: #007bff;
    }
    .btn-primary:hover {
        background-color: #0056b3;
        border-color: #0056b3;
    }
    </style>
    </head>
    <body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
    <div class="container-fluid">
    <a class="navbar-brand" href="/">VICX</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
    <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse justify-content-end" id="navbarNav">
    <ul class="navbar-nav">
    <li class="nav-item">
    <a class="nav-link" href="/">Home</a>
    </li>
    <li class="nav-item">
    <a class="nav-link" href="index.html">Server Setup</a>
    </li>
    <li class="nav-item">
    <a class="nav-link active" aria-current="page" href="#">Arch Setup</a>
    </li>
    </ul>
    </div>
    </div>
    </nav>
    <div class="container">
    <h1>Click Counter</h1>
    <p>Number of clicks: <span id="clickCount">${clickCount}</span></p>
    <button id="clickButton" class="btn btn-primary">Click me!</button>
    </div>
    <!-- Bootstrap JS and your custom JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
    <script>
    // Initialize clickCount from server-side value
    let clickCount = ${clickCount};

    document.getElementById('clickButton').addEventListener('click', () => {
        // Increment clickCount
        clickCount++;
        // Update the displayed count
        document.getElementById('clickCount').textContent = clickCount;
    });
    </script>
    </body>
    </html>
    `);
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});