# docker build -t snake-game .
# docker run --name snake-game -p 4000:4000 snake-game
# docker stop <container_id_or_name>
# docker rm snake-game # remove stopped container
# docker rmi snake-game # remove Docker-image


# Use the official Node.js 18 image with Alpine Linux
FROM node:18-alpine

# Create and change to the app directory
WORKDIR /usr/src/app

# Install dependencies
COPY package*.json ./
RUN npm install

# Copy the rest of the application code
COPY . .

# Expose the port the app runs on
EXPOSE 4000

# Define the command to run the application
CMD ["node", "server.js"]