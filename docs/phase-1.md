## Introduction

This document contains the relevant design and implementation aspects of LS project's first phase.

## Modeling the database

### Conceptual model ###

The following diagram holds the Entity-Relationship model for the information managed by the system.

![Entity-Relationship Model Diagram](./er-diagram.svg)

We highlight the following aspects:

* (_include a list of relevant design issues_)

The conceptual model has the following restrictions:

* (_include a list of relevant design issues_)

### Physical Model ###

The physical model of the database is available [here](../src/main/sql/createSchema.sql).

We highlight the following aspects of this model:

* (_include a list of relevant design issues_)

## Software organization

### Open-API Specification ###

The specification of the API is available:

* [As a JSON file](../open-api.json)
* [As a Postman documentation webpage](https://documenter.getpostman.com/view/26358395/2s93RRvsbv)

In our Open-API specification, we highlight the following aspects:

(_include a list of relevant issues or details in your specification_)

### Request Details

(_describe how a request goes through the different elements of your solution_)

(_describe the relevant classes/functions used internally in a request_)

(_describe how and where request parameters are validated_)

### Connection Management

At the time of writing, connections are created based on a connection string stored as an environment variable under the name ``JDBC_DATABASE_URL``.

During each of the single units defined for our database interface, a connection is created and managed in the scope of the function via ``.use``, ensuring automatic disposal of the connection once the scope exits.

If the work being done with the connection may alter the database in any way, transactions are handled by disabling the connection's ``autoCommit``. If any error occurs, we issue a ``rollback()`` and throw an exception to the calling function. In all other cases we issue a ``commit()`` and if needed the correct return value.

### Data Access

To access the data, we created 4 significant interfaces:

* UsersData
* BoardsData
* ListsData
* CardsData

These represent our 4 strong entities and their related tables. With each of them, one is able to access CRUD operations via ``getById()``, ``add()``, ``delete()`` and ``edit()``.

Each of these also has entity specific operations like ``getByEmail()`` or ``createToken()`` for ``Users``.

By the use of these interfaces, implementations were created for in-memory data storage and for PostgresSQL (located in [data\mem](../src/main/kotlin/pt/isel/ls/data/mem/) and [data\pgsql](../src/main/kotlin/pt/isel/ls/data/pgsql/) respectively).

(_identify any non-trivial used SQL statements_).

### Error Handling/Processing

(_describe how errors are handled and their effects on the application behavior_).

## Critical Evaluation

(_enumerate the functionality that is not concluded and the identified defects_)

(_identify improvements to be made on the next phase_)