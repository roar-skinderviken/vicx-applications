# Stage 1: Build the wheel using Poetry
FROM python:3.8-slim AS builder

# Install Poetry
RUN pip install poetry

# Set the working directory
WORKDIR /app

# Copy pyproject.toml and poetry.lock
COPY pyproject.toml poetry.lock* /app/

# Install dependencies and build the wheel
RUN poetry config virtualenvs.create false \
 && poetry install --no-dev \
 && poetry build -f wheel

# Stage 2: Production image
FROM python:3.8-slim

# Set the working directory
WORKDIR /app

# Copy the wheel from the builder stage
COPY --from=builder /app/dist/*.whl /app/

# Install the wheel package
RUN pip install --no-cache-dir *.whl

# Copy the rest of the application code
COPY . /app

# Command to run the application
CMD ["python", "main.py"]
