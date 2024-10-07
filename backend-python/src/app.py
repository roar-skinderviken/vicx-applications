from fastapi import FastAPI
from fastapi.responses import HTMLResponse
from fastapi.staticfiles import StaticFiles
import uvicorn
from pydantic import BaseModel
from src.kmeans_grades import map_scores_to_grades

app = FastAPI()

# Serve static files (HTML)
app.mount("/html-pages", StaticFiles(directory="html-pages"), name="html-pages")

@app.get("/health")
async def health_check():
    return {"status": "UP"}

@app.get("/items/{item_id}")
async def read_item(item_id: int, q: str = None):
    return {"item_id": item_id, "q": q}

class KMeansRequest(BaseModel):
    failScore: int
    scores: list[int]

@app.post("/k-means")
async def post_kmeans(body: KMeansRequest) -> dict:
    return map_scores_to_grades(
        fail_score=body.failScore,
        scores=body.scores
    )

@app.get("/", response_class=HTMLResponse)
async def get_html():
    with open("html-pages/content/python.html") as f:
        return f.read()

if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8000)
