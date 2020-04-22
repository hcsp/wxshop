### 启动步骤

- `docker run -d -v /path/to/wxshop-data:/var/lib/mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=wxshop mysql`
- `./mvnw flyway:migrate` 或 `./mvnw.cmd flyway:migrate`
- `./mvnw package -DskipTests` 或 `./mvnw.cmd package -DskipTests`
- `java -jar target/wxshop-0.0.1-SNAPSHOT.jar`
- open http://127.0.0.1:8080

### 数据库配置

如果你的 mysql 服务器没有在 localhost 启动，而是在其他 ip 启动，那么你需要将以下两个文件中的 localhost 替换成对应的 ip

* pom.xml 
* src/main/resources/application.yml