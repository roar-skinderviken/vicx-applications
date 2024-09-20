# Step 1: Use an official Node.js runtime as a parent image
FROM node:20-alpine

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the package.json and package-lock.json (if present)
COPY package*.json ./

# Step 4: Install dependencies (this will create a node_modules folder)
RUN npm install

# Step 5: Copy the rest of the application code to the working directory
COPY . .

# Step 6: Expose the port that the app runs on (3000 in this case)
EXPOSE 3000

# Step 7: Define the command to run the application
CMD ["node", "app.js"]
