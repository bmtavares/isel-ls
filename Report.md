# Phase 1

## Introduction

This document contains the relevant design and implementation aspects of LS project's first phase.

## Modeling the database

### Conceptual model ###
The following diagram holds the Entity-Relationship model for the information managed by the system.

(include an image or a link to the conceptual diagram)

We highlight the following aspects:

The conceptual model includes entities for users, boards, lists, and cards, each with their corresponding attributes.
Each board can be associated with one or several users, and each list must belong to a specific board.
Cards are always associated with a board and must belong to a list, except if they are archived.
The conceptual model has the following restrictions:

A card cannot be associated with multiple boards/lists.
A list must belong to one board only.

### Physical Model ###

The physical model of the database is available in (link to the SQL script with the schema definition).

We highlight the following aspects of this model:

The schema defines tables for users, boards, lists, and cards, each with their corresponding columns and constraints.
Relationships between tables are enforced through foreign keys.

## Software organization

### Open-API Specification ###

(include a link to the YAML file containing the Open-API Specification)

In our Open-API specification, we highlight the following aspects:

The API includes endpoints for creating and managing users, boards, lists, and cards.
Requests and responses follow the JSON format.
Authorization is required for some endpoints with the token key.

### Request Details

Requests follow a RESTFUL architecture, where each endpoint corresponds to a specific action, such as creating a new user or retrieving a specific card.

Internally, requests are processed by relevant classes and functions, such as UserController and CardService.

Request parameters are validated using token validation.

### Connection Management

Database connections are created and managed by a connection pool. Transactions are used to ensure atomicity and consistency in database operations.

### Data Access

Data access is managed through a DataAccess class, which handles basic CRUD operations on database entities.

Non-trivial SQL statements are used to perform complex queries or updates, such as fetching all cards belonging to a specific board and list.

### Error Handling/Processing


## Critical Evaluation
defects:
missing error messages / proper error handeling

Improvements for the next phase include:

Improved error handling and data validation.
Implementation of missing functionality.
Optimization of database queries for performance.