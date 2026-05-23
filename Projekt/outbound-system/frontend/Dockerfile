FROM node:26-alpine AS builder

WORKDIR /app

COPY package.json package-lock.json ./

RUN npm i -g npm@11.6.0 && npm cache clean --force
RUN --mount=type=cache,target=/root/.npm npm ci

COPY . .
RUN npm run build


FROM nginx:alpine
EXPOSE 80
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html
