# Vicx Applications

## Getting started

### Create next-app/.env.local
Should have the following content
```
NEXT_PUBLIC_KMEANS_BACKEND_URL=http://localhost:8000/k-means
NEXT_PUBLIC_CALCULATOR_BACKEND_URL=http://localhost:8080/api/calculator
OIDC_CLIENT_SECRET=secret
NEXTAUTH_SECRET=secret
NEXTAUTH_URL=http://localhost:3000/api/auth
AUTHORIZATION_URL=http://localhost:9000/auth-server/oauth2/authorize
TOKEN_URL=http://localhost:9000/auth-server/oauth2/token
ISSUER=http://localhost:9000/auth-server
JWKS_ENDPOINT=http://localhost:9000/auth-server/oauth2/jwks
```

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
