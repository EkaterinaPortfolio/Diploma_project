FROM node:8.16.2-alpine
WORKDIR /opt/app
COPY gate-simulator .
RUN npm install
CMD ["npm", "start"]
EXPOSE 9999
