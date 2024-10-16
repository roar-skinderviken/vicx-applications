from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import uvicorn
from pydantic import BaseModel
from src.kmeans_grades import map_scores_to_grades

app = FastAPI()

# Allow CORS from http://localhost:3000
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000"],  # List of allowed origins
    allow_credentials=True,
    allow_methods=["*"],  # Allow all methods
    allow_headers=["*"],  # Allow all headers
)

@app.get("/health")
async def health_check():
    return {"status": "UP"}


class KMeansRequest(BaseModel):
    failScore: float
    maxScore: float
    scores: list[float]
    maxIter: int


@app.post("/k-means")
async def post_kmeans(body: KMeansRequest) -> dict:
    return map_scores_to_grades(
        fail_score=body.failScore,
        max_score=body.maxScore,
        scores=body.scores,
        max_iter=body.maxIter
    )


if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8000)
