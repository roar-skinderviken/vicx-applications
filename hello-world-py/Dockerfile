# Stage 1: Install dependencies using Poetry
FROM python:3.12-slim AS builder

# Install Poetry
RUN pip install poetry

# Set the working directory
WORKDIR /app

# Copy pyproject.toml and poetry.lock
COPY pyproject.toml poetry.lock* /app/

# Install dependencies
RUN poetry config virtualenvs.create false \
 && poetry install --only main

# Stage 2: Production image
FROM python:3.12-slim

# Set the working directory
WORKDIR /app

# Copy the installed dependencies from the builder stage
COPY --from=builder /app /app

# Copy the application code
COPY . /app

# Command to run the application
CMD ["python", "main.py"]
