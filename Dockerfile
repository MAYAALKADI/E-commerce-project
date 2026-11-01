# استخدام نسخة جافا 21 الرسمية والخفيفة
FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /app

# نسخ ملفات البناء
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

# نسخ الكود وبناء المشروع
COPY src src
RUN ./mvnw clean package -DskipTests

# تشغيل التطبيق
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]