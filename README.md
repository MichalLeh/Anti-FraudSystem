# Anti-FraudSystem

This project demonstrates (in a simplified form) the principles of anti-fraud systems in the financial sector. For this project, we will work on a system with an expanded role model, a set of REST endpoints responsible for interacting with users, and an internal transaction validation logic based on a set of heuristic rules.

Implementation of [Hyperskill's](https://hyperskill.org/projects/232) project that includes JSON, REST API, Spring Boot Security, H2 database, LocalDateTime, Project Lombok concepts. 

## Requirements

- JDK 11
- Gradle 7.3.3
- Spring Boot 2.7.2

## Usage

1. Clone the repository
    ```shell
    git clone https://github.com/MichalLeh/Anti-FraudSystem.git
    ```
2. Build and run the project
    ```shell
      cd Recipes
      gradlew build
    ```

The endpoints can be accessed using a browser or a tool that allows you to send HTTP requests
like [Postman](https://www.getpostman.com/).

### Processes

- [Registration](#registration)
- [Post a new transaction](#post-a-new-transaction)
- [Update a recipe](#update-a-recipe)
- [Get a recipe by id](#get-a-recipe-by-id)
- [Get recipes by category](#get-recipes-by-category)
- [Delete a recipe by id](#Delete-a-recipe-by-id)

## API Endpoints

| Endpoint                                        | Anonymous | MERCHANT | ADMINISTRATOR | SUPPORT |
|-------------------------------------------------|-----------|----------|---------------|---------|
| POST /api/auth/user                             | +         | +        | +             | +       |
| POST /api/antifraud/transaction                 | -         | -        | +             | -       |
| GET /api/auth/list                              | -         | -        | +             | +       |
| DELETE /api/auth/user                           | -         | +        | -             | -       |
| POST, DELETE, GET /api/antifraud/suspicious-ip  | -         | -        | -             | +       |
| POST, DELETE, GET /api/antifraud/stolencard     | -         | -        | -             | +       |

_'+' means the user with given role can access given endpoint. '-' means the user without given role can't access given endpoint._

### Examples

#### Registration

##### Objectives

- It must be available to unauthorized users for registration and accept data in the JSON format;
- If a user has been successfully added, the endpoint must respond with the `HTTP CREATED` status `201`;
- If an attempt to register an existing user was a failure, the endpoint must respond with the `HTTP CONFLICT` status `409`;
- If a request contains wrong data, the endpoint must respond with the `BAD REQUEST` status `400`;
- The first registered user should receive the `ADMINISTRATOR` role; the rest - `MERCHANT`. 
- In case of authorization violation, respond with the `HTTP FORBIDDEN` status `403`. Mind that only one role can be assigned to a user;
- All users, except `ADMINISTRATOR`, must be locked immediately after registration; only `ADMINISTRATOR` can unlock users;

`POST /api/auth/user` request

*Request body:*

```
{
   "name": "John Doe",
   "username": "JohnDoe",
   "password": "secret"
}
```

*Response:* `201 CREATED`

*Response body:*

```
{
    "id": 1,
    "name": "John Doe",
    "username": "JohnDoe",
    "role": "ADMINISTRATOR"
}
```
