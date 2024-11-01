Test-URL:
curl -X POST "http://localhost:8000/k-means" -H "Content-Type: application/json" -d '{"failScore": 30, "scores": [20, 30, 60, 90]}'