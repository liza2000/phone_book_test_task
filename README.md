Инструкция к запуску:

git clone https://github.com/liza2000/phone_book_test_task.git

cd phone_book_test_task

#В файле src\main\resources\application.properties поменять свойства spring.datasource.url, 
spring.datasource.username и spring.datasource.password на соответствующие вашей БД PostgreSQL

mvn clean package 

java -jar target/PhoneBook-1.0.jar

#Спецификация REST может быть открыта по адресу http://localhost:8080/swagger-ui.html#/

