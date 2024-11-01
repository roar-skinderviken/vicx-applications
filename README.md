# Vicx Applications

## Getting started

### Start the auth-server on port 9000
```shell
./gradlew -p auth-server bootRun
```

### Start the Next frontend
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

### Start the backend-spring-boot (optional)
```shell
./gradlew -p backend-spring-boot bootRun
```

### Start the backend-python (optional, requires Poetry)
```shell
poetry cd backend-python
```
```shell
poetry run uvicorn src.app:app --reload
```
