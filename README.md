# Vicx Applications

## Getting started

### Create next-app/.env.local
Should have the following content
```
OAUTH_CLIENT_SECRET=secret
OAUTH_BASE_URL=http://localhost:9000/auth-server
NEXTAUTH_SECRET=secret
NEXTAUTH_URL=http://localhost:3000/api/auth
RECAPTCHA_SITE_KEY=6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI
NEXT_PUBLIC_KMEANS_BACKEND_URL=http://localhost:8000/k-means
SPRING_BACKEND_BASE_URL=http://localhost:8080/backend-spring-boot
```
Value for `RECAPTCHA_SITE_KEY` is a dev value. 


### Start the auth-server on port 9000
```shell
./gradlew -p auth-server bootRun
```

### Start the Next app
```shell
cd next-app
```
```shell
npm ci
```
```shell
npm run dev
```

Next app will now be available on 
http://localhost:3000

Username/password for logging in with OAuth:
- user1
- password

### Start the backend-spring-boot (optional)
```shell
./gradlew -p backend-spring-boot bootRun
```

### Start the backend-python (optional, requires Poetry)
```shell
cd backend-python
```
```shell
poetry install
```
```shell
poetry run uvicorn src.app:app --reload
```

## API Documentation

The OpenAPI documentation for the API is available at the following URL:

[Swagger UI](http://localhost:8080/backend-spring-boot/swagger-ui/index.html)

You can use this interface to explore and interact with the API endpoints, view their descriptions, 
and test requests directly from the UI.

## GraphQL

http://localhost:8080/backend-spring-boot/graphiql


```
mutation {
  createCalculation(
    firstValue: 1, 
    secondValue: 2, 
    operation: PLUS
  ) {
    id
    firstValue
    secondValue
    operation
    result
    username
    createdAt
  }
}
```

```
query {
  getAllCalculations(page: 0) {
    calculations {
      firstValue
      secondValue
      operation
      result
    },
    page,
    totalPages
  }
}
```

```
mutation {
    deleteCalculations(ids: [1,2])
}
```
