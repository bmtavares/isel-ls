## Introduction

This document contains the relevant design and implementation aspects of LS project's first phase.

## Modeling the database

### Conceptual model ###

The Entity-Relationship (ER) diagram below shows the conceptual model for the information managed by the system.

![Entity-Relationship Model Diagram](./er-diagram.svg)

The conceptual model includes entities for users, boards, lists, and cards, each with their corresponding attributes.
Each board can be associated with one or several users, and each list must belong to a specific board.
Cards are always associated with a board and must belong to a list, except if they are archived.
The conceptual model has the following restrictions:

A card cannot be associated with multiple boards/lists.
A list must belong to one board only.

### Physical Model ###

The physical model of the database is available [here](../src/main/sql/createSchema.sql).

We highlight the following aspects of this model:

The schema defines tables for users, boards, lists, and cards, each with their corresponding columns and constraints.
Relationships between tables are enforced through foreign keys.

## Software organization

### Open-API Specification ###

The specification of the API is available:

* [As a JSON file](../open-api.json)
* [As a Postman documentation webpage](https://documenter.getpostman.com/view/26358395/2s93RRvsbv)

Authorization is required for some endpoints with the token key.

### Request Details

Requests follow a RESTFUL architecture, where each endpoint corresponds to a specific action, such as creating a new user or retrieving a specific card.

Internally, requests are processed by relevant classes and functions.

Request parameters are validated using token validation.

### Connection Management
To use the database we must set the environment variable under the name ``USE_POSTGRESQL`` as True, if false we will use de information on RAM only. 

Connections are created based on a connection string stored as an environment variable under the name ``JDBC_DATABASE_URL``.

During each of the single units defined for our database interface, a connection is created and managed in the scope of the function via ``.use``, ensuring automatic disposal of the connection once the scope exits.

If the work being done with the connection may alter the database in any way, transactions are handled by disabling the connection's ``autoCommit``. If any error occurs, we issue a ``rollback()`` and throw an exception to the calling function. In all other cases we issue a ``commit()`` and if needed the correct return value.

Requests go through the following elements of the solution:

Client sends a request to the server
Request is validated and authenticated
Request is processed by the appropriate class or function
Database is queried and data is returned to the class or function
Data is transformed and returned to the client

### Other
We must ser a environment variable under the name ``PORT`` to indicate the port that the service will be listening to. 


### Data Access

Data access is done through four main interfaces:

UsersData
BoardsData
ListsData
CardsData

Each interface provides CRUD operations via getById(), add(), delete(), and edit() functions. Additionally, each interface has entity-specific operations, such as getByEmail() or createToken() for Users.

Implementations were created for in-memory data storage and for PostgreSQL (located in data\mem and data\pgsql, respectively).

### Error Handling/Processing

### Single page application


## Critical Evaluation




