### 启动步骤

- `docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=wxshop mysql`
- `./mvnw flyway:migrate` 或 `./mvnw.cmd flyway:migrate`
- `./mvnw package -DskipTests` 或 `./mvnw.cmd package -DskipTests`
- `java -jar target/wxshop-0.0.1-SNAPSHOT.jar`
