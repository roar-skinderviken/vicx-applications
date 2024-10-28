# cd next-app
# docker build -t next-app .
# docker run --name next-app -p 3000:3000 next-app:latest
# docker rm next-app # remove stopped container
# docker rmi next-app # remove Docker-image


FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./

RUN npm ci

COPY . .

RUN npm run build

FROM node:20-alpine AS runtime

WORKDIR /app

COPY --from=builder /app ./

RUN npm ci --omit=dev

EXPOSE 3000

CMD ["npm", "start"]