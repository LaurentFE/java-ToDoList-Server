# To Do List - Server

## Usage

Set up the MySQL database with the SQL scripts found in src/main/resources/todo_lists[_data].sql

Configure src/main/resources/db-connection-infos.json to allow connection to your MySQL server & database

Use Maven to compile and package a .war file

Install the .war file in your preferred webserver (you can locally use Tomcat v9, that's what I do for this dev project)
and run the webapp

Endpoints are detailed in src/main/resources/Java-TodoList-Server.postman_collection.json, a JSON file you can directly
import in POSTMAN, you can use the pre-made requests to interact with all available endpoints.

## What

This is a WIP of a Java server that connects to a MySQL Database to store todo lists for users, and expose API 
endpoints to provide the to do lists to a client.

Available endpoints allow :
- Restitution of all users
- Restitution of a user's specific todo list
- Restitution of all of a user's todo lists
- Creation of user 
- Creation of a list for a user
- Creation of a list item for a list attached to a user 
- Update of list name
- Update of list item name
- Update of list item status (checked/unchecked)

DELETE endpoints may or may not be implemented, they just introduce respect of the table's constraints with the many 
foreign keys referenced between tables, so the DELETE SQL statements should be handled in the proper order. Not exactly 
a lot to learn in this exercise.

The behaviour of the endpoints is not sophisticated : they don't check for the presence of the correct parameters, don't
give a response body to explained why a call failed, basically just send HTTP code "OK" and send the data for GET, "CREATED" for 
POST, and "OK" for  PUT endpoints, or "INTERNAL_ERROR" for everything else.
Server returns HTTP code "NOT_FOUND" when calling a non defined endpoint.

While I know this is not the best way to handle this in production, this is not the goal of this project. I wanted to 
learn how to make a Java webserver that can accept REST API calls, and handle a MySQL database. Which is now handled.

## How

Programmed in Java using IDE IntelliJ, storing data on a MySQL Database. 

Built with Maven, run on Tomcat v9 web server.

Logging through log4j