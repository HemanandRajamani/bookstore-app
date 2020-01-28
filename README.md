BookStore Application:

Environment :
JRE 11

mvn clean install

java -jar bookstore-app-0.0.1-SNAPSHOT.jar


API:

1. Purchase Books

POST - http://localhost:8080/api/books/v1.0/purchase

Headers:
Content-Type : application/json

Body:
{
"bookType": String,
"quantity": Integer
}

2. Report

Get - http://localhost:8080/api/books/v1.0/report