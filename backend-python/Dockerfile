# cd backend-python
# docker build -t backend-python .
# docker run --name backend-python -p 80:8000 backend-python:latest
# docker rm backend-python # remove stopped container
# docker rmi backend-python # remove Docker-image

ARG PYTHON_VERSION=3.12-slim-bullseye

FROM python:${PYTHON_VERSION} AS builder
WORKDIR /app
COPY pyproject.toml poetry.lock* /app/
COPY src /app/src

RUN touch README.md && \
    pip install poetry && \
    poetry config virtualenvs.create false && \
    poetry install --no-root --no-dev && \
    poetry build --format wheel

FROM python:${PYTHON_VERSION} AS runtime
WORKDIR /app

COPY --from=builder /app/dist/*.whl /app/
RUN pip install /app/*.whl

CMD ["uvicorn", "src.app:app", "--host", "0.0.0.0", "--port", "8000"]
