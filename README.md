# Anti-Fraud System

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
- [Get all users](#get-all-users)
- [Delete an user](#delete-an-user)
- [Lock/Unlock an user](#lock/unlock-an-user)
- [Update user role](#Update-user-role)
- [Post a new transaction](#post-a-new-transaction)
- [Save a suspicious IP address](#save-a-suspicious-IP-address)
- [Delete a suspicious IP address](#delete-a-suspicious-IP-address)
- [Get all suspicious IP addresses](#get-all-suspicious-IP-address)
- [Save a stolen card](#save-a-stolen-card)
- [Delete a stolen card](#delete-a-stolen-card)
- [Get all stolen cards](#get-all-stolen-cards)

## API Endpoints

| Endpoint                                            | Anonymous | MERCHANT | ADMINISTRATOR | SUPPORT |
|-----------------------------------------------------|-----------|----------|---------------|---------|
| POST /api/auth/user                                 | +         | +        | +             | +       |
| GET /api/auth/list                                  | -         | -        | +             | +       |
| DELETE /api/auth/user/{username}                    | -         | -        | +             | -       |
| PUT /api/auth/access                                | -         | -        | +             | -       |
| PUT /api/auth/role                                  | -         | -        | +             | -       |
| POST /api/antifraud/transaction                     | -         | +        | -             | -       |
| POST, DELETE, GET /api/antifraud/suspicious-ip/{ip} | -         | -        | -             | +       |
| POST, DELETE, GET /api/antifraud/stolencard/{number}| -         | -        | -             | +       |

_'+' means the user with given role can access given endpoint. '-' means the user without given role can't access given endpoint._

### Examples

#### Registration

#### Objectives

- It must be available to unauthorized users for registration and accept data in the JSON format;
- If a user has been successfully added, the endpoint must respond with the `HTTP CREATED` status `201`;
- If an attempt to register an existing user was a failure, the endpoint must respond with the `HTTP CONFLICT` status `409`;
- If a request contains wrong data, the endpoint must respond with the `BAD REQUEST` status `400`;
- The first registered user should receive the `ADMINISTRATOR` role; the rest - `MERCHANT`. 
- In case of authorization violation, respond with the `HTTP FORBIDDEN` status `403`. Mind that only one role can be assigned to a user;
- All users, except `ADMINISTRATOR`, must be locked immediately after registration; only `ADMINISTRATOR` can unlock users.

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

#### Get all users

#### Objectives

- The endpoint must respond with the `HTTP OK` status `200` and the body with an array of objects representing the users sorted by ID in ascending order. Return an empty JSON array if there's no information.

`GET /api/auth/list` request

*Response:* `200 OK`

*Response body:*

```
[
  {
    "name":"John Doe",
    "username":"JohnDoe",
    "role": "ADMINISTRATOR"
  },
  {
    "name":"JohnDoe2",
    "username":"JohnDoe2",
    "role": "MERCHANT"
  }
]
```
or

```
[]
```

#### Delete an user

#### Objectives

- The endpoint must delete the user and respond with the `HTTP OK` status `200`;
- If a user is not found, respond with the `HTTP NOT FOUND` status `404`.

`DELETE /api/auth/user/johndoe` request

*Response:* `200 OK`

*Response body:*

```
{
   "username": "JohnDoe",
   "status": "Deleted successfully!"
}
```

#### Lock/Unlock an user

#### Objectives

- Endpoint that locks/unlocks users;
- If successful, respond with the `HTTP OK` status `200`;
- For safety reasons, `ADMINISTRATOR` cannot be blocked. In this case, respond with the `HTTP BAD REQUEST` status `400`;
- If a user is not found, respond with `HTTP NOT FOUND` status `404`.

`PUT /api/auth/access` request with the correct authentication under the `ADMINISTRATOR` role:

*Request body:*

```
{
   "username": "JohnDoe2",
   "operation": "UNLOCK"
}
```

*Response:* `200 OK`

*Response body:*

```
{
   "status": "User JohnDoe2 unlocked!"
}
```

#### Update user role

#### Objectives

- Endpoint that changes user roles;
- If successful, respond with the `HTTP OK` status `200`;
- If a user is not found, respond with the `HTTP NOT FOUND` status `404`;
- If a role is not `SUPPORT` or `MERCHANT`, respond with `HTTP BAD REQUEST` status `400`;
- If you want to assign a role that has been already provided to a user, respond with the `HTTP CONFLICT` status `409`.

`PUT /api/auth/role` request with the correct authentication under the `ADMINISTRATOR` role:

*Request body:*

```
{
   "username": "JohnDoe2",
   "role": "SUPPORT"
}
```

*Response:* `200 OK`

*Response body:*

```
{
   "id": 1,
   "name": "John Doe 2",
   "username": "JohnDoe2",
   "role": "SUPPORT"
}
```

#### Post a new transaction

#### Objectives

- The transaction amount must be greater than `0`;
- Transactions with a sum of lower or equal to `200` are `ALLOWED`;
- Transactions with a sum of greater than `200` but lower or equal than `1500` require `MANUAL_PROCESSING`;
- Transactions with a sum of greater than `1500` are `PROHIBITED`;
- If the validation process was successful, the endpoint should respond with the status `HTTP OK` `200`;
- In case of wrong data in the request, the endpoint should respond with the status `HTTP BAD REQUEST` `400`;
- If the result is `ALLOWED`, the `info` field must be set to `none` (a string value);
- A transaction containing a card number is set to:
    1. `MANUAL_PROCESSING` if there are transactions from 3 different regions/IP addresses (count == 3) of the world other than the region/IP addresses of the transaction that are being verified in the last hour in the transaction history;
    2. `PROHIBITED` if there are transactions from more than 3 different regions/IP addresses (count > 3) of the world other than the region/IP addresses of the transaction that are being verified in the last hour in the transaction history;
- In the case of the `PROHIBITED` or `MANUAL_PROCESSING` result, the `info` field must contain the reason for rejecting the transaction. The reason must be separated by `,` and sorted alphabetically. For example, `amount, card-number, ip, ip-correlation, region-correlation`;
- IP addresses are checked for compliance with IPv4. Any address following this format consists of four series of numbers from 0 to 255 separated by dots;
- Card numbers must be checked according to the Luhn algorithm.

World region codes:

| Code     | Description                     |
|----------|---------------------------------|
| EAP      | East Asia and Pacific           |
| ECA      | Europe and Central Asia         |
| HIC      | High-Income countries           |
| LAC      | Latin America and the Caribbean |
| MENA     | The Middle East and North Africa|
| SA       | South Asia                      |
| SSA      | Sub-Saharan Africa              |

`POST /api/antifraud/transaction` request

*Request body:*

```
{
  "amount": 150,
  "ip": "192.168.1.1",
  "number": "4000008449433403",
  "region": "EAP",
  "date": "2022-01-22T16:04:00"
}
```

*Response:* `200 OK`

*Response body:*

```
{
   "result": "ALLOWED",
   "info": "none"
}
```
#### Save a suspicious IP address

#### Objectives

- Endpoint that saves suspicious IP addresses in the database;
- If successful, respond with the `HTTP OK` status `200`;
- If the IP is already in the database, respond with the `HTTP CONFLICT` status `409`;
- If an IP address has the wrong format, respond with the `HTTP BAD REQUEST` status `400`;

`POST /api/antifraud/suspicious-ip` request

*Request body:*

```
{
   "ip": "192.168.1.1"
}
```

*Response:* `200 OK`

*Response body:*

```
{
   "id": 1,
   "ip": "192.168.1.1"
}
```

#### Delete a suspicious IP address

#### Objectives

- Endpoint that deletes IP addresses from the database;
- If successful, respond with the `HTTP OK` status `200`;
- If an IP is not found in the database, respond with the `HTTP NOT FOUND` status `404`;
- If an IP address has the wrong format, respond with the `HTTP BAD REQUEST` status `400`.

`DELETE /api/antifraud/suspicious-ip/192.168.1.1` request

*Response:* `200 OK`

*Response body:*

```
{
   "status": "IP 192.168.1.1 successfully removed!"
}
```

#### Get all suspicious IP addresses

#### Objectives

- Endpoint that shows IP addresses stored in the database;
- If successful, respond with the `HTTP OK` status `200` and a body with an array of JSON objects representing IP address sorted by ID in ascending order (or an empty array if the database is empty).

`GET /api/antifraud/suspicious-ip` request

*Response:* `200 OK`

*Response body:*

```
[
    {
        "id": 1,
        "ip": "192.168.1.1"
    },
     ...
    {
        "id": 100,
        "ip": "192.168.1.254"
    }
]
```
or
```
[]
```
#### Save a stolen card

#### Objectives

- Endpoint that saves stolen card in the database;
- If successful, respond with the `HTTP OK` status `200`;
- If a card is already in the database, respond with the `HTTP CONFLICT` status `409`;
- If a card has the wrong format, respond with the `HTTP BAD REQUEST` status `400`;

`POST /api/antifraud/stolencard` request

*Request body:*

```
{
   "number": "4000008449433403"
}
```

*Response:* `200 OK`

*Response body:*

```
{
   "id": 1,
   "number": "4000008449433403"
}
```

#### Delete a stolen card

#### Objectives

- Endpoint that deletes card number from the database;
- If successful, respond with the `HTTP OK` status `200`;
- If a card number is not found in the database, respond with the `HTTP NOT FOUND` status `404`;
- If a card number has the wrong format, respond with the `HTTP BAD REQUEST` status `400`.

`DELETE /api/antifraud/suspicious-ip/4000008449433403` request

*Response:* `200 OK`

*Response body:*

```
{
   "status": "Card 4000008449433403 successfully removed!"
}
```

#### Get all stolen cards

#### Objectives

- Endpoint that shows card numbers stored in the database;
- If successful, respond with the `HTTP OK` status `200` and a body with an array of JSON objects representing card numbers sorted by ID in ascending order (or an empty array if the database is empty).

`GET /api/antifraud/stolencard` request

*Response:* `200 OK`

*Response body:*

```
[
    {
        "id": 1,
        "number": "4000008449433403"
    },
     ...
    {
        "id": 100,
        "number": "4000009455296122"
    }
]
```
or
```
[]
```
