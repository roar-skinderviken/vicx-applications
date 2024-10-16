# docker build -t next-app .
# docker run -p 3000:3000 next-app

FROM node:20-alpine

WORKDIR /app

COPY package*.json ./

RUN npm ci

# Copy the rest of the application code
COPY . .

RUN npm run build

EXPOSE 3000

CMD ["npm", "start"]