# docker build -t pandascore2 .
# docker run -e API_KEY='1234' -p 3000:3000 pandascore3

FROM node:20-alpine

WORKDIR /app
COPY package*.json app.js ./

RUN npm ci

EXPOSE 3000
CMD ["node", "app.js"]
