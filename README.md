# Vicx Applications

## Getting started

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
