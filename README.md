# Vicx Applications

## Getting started

### Create next-app/.env.local
Should have the following content
```
OAUTH_CLIENT_SECRET=secret
OAUTH_BASE_URL=http://localhost:9000/auth-server
NEXTAUTH_SECRET=secret
USER_IMAGE_BACKEND_URL=http://localhost:8080/api/user/image
RECAPTCHA_SITE_KEY=6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI
NEXT_PUBLIC_KMEANS_BACKEND_URL=http://localhost:8000/k-means
NEXT_PUBLIC_CALCULATOR_BACKEND_URL=http://localhost:8080/api/calculator
NEXT_PUBLIC_USER_BACKEND_URL=http://localhost:8080/api/user
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
