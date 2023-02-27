FROM node:18-alpine AS builder
ARG API_URL
ENV VITE_API_URL=$API_URL
WORKDIR /app
RUN corepack enable
COPY package.json pnpm-lock.yaml ./
RUN pnpm install
COPY . ./
RUN pnpm build

FROM nginx:stable-alpine
USER nginx
WORKDIR /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist ./
