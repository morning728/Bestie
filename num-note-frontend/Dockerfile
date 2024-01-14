FROM node:alpine
WORKDIR /app
COPY package.json .
COPY package-lock.json .
# COPY ./env.sh .
# COPY .env .

# # Make our shell script executable
# RUN chmod +x env.sh
RUN npm install

COPY . .

ARG REACT_APP_API_URL

ENV REACT_APP_API_URL $REACT_APP_API_URL
EXPOSE 3001
CMD ["npm", "start"]