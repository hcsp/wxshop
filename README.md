### 启动步骤

- 启动第三方依赖
  - `docker run -d -v /path/to/wxshop-data:/var/lib/mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=wxshop --name=wxshop-mysql mysql`
  - `docker run -p 6379:6379 -d redis`
  - `docker run -p 2181:2181 -d zookeeper`
  - 等待半分钟，等容器启动完毕
  - `docker exec -it wxshop-mysql mysql -uroot -proot -e 'create database if not exists \`order\`'`
  - `./mvnw install -DskipTests`
  - `./mvnw flyway:migrate -pl wxshop-main` 
  - `./mvnw flyway:migrate -pl wxshop-order` 
  - `./mvnw package -DskipTests` 
  - **注意，如果你使用的是Windows，将所有的./mvnw换成./mvnw.cmd**
- 启动应用本身
  - `java -jar wxshop-order/target/wxshop-order-0.0.1-SNAPSHOT.jar`
  - `java -jar wxshop-main/target/wxshop-main-0.0.1-SNAPSHOT.jar`
- open http://127.0.0.1:8080

### 数据库/Redis/ZooKeeper配置

如果你的 mysql/redis/zookeeper 服务器没有在 localhost 启动，而是在其他 ip 启动，那么你需要全局替换 localhost 为对应的 ip

**注意，只替换`localhost`，不要替换`127.0.0.1`！！！**
