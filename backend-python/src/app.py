from fastapi import FastAPI
import uvicorn
from pydantic import BaseModel
from src.kmeans_grades import map_scores_to_grades

app = FastAPI()

# Comment in CORS when testing on localhost
# origins = [
#     "*"
# ]
#
# # Add CORS middleware
# # noinspection PyTypeChecker
# app.add_middleware(
#     CORSMiddleware,
#     allow_origins=origins,  # List of allowed origins
#     allow_credentials=True,
#     allow_methods=["*"],  # Allows all HTTP methods (GET, POST, etc.)
#     allow_headers=["*"],  # Allows all headers
# )


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


if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8000)