FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
ARG API_BASE_URL
ENV API_BASE_URL=$API_BASE_URL
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew :shared:wasmJsBrowserDistribution --no-daemon
FROM nginx:alpine
COPY --from=build /app/shared/build/dist/wasmJs/productionExecutable /usr/share/nginx/html
COPY nginx.conf /etc/nginx/templates/default.conf.template
EXPOSE 80