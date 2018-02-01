These three files are from this past fall when I took a multi-tier web design class. We programmed the back end of a server.

0000_initial-data.js is a seed file that Knex.js uses to seed a PostgreSQL database. I exclusively wrote this file.

20171130142550_initial.js is a migration file that Knex.js uses to create the database schema (or rollback to a previous version). I also exclusively wrote this file.

hapi-server.js is the file that contains most of the server's code. It uses Objection to abstract the tables in our database so that we can interact with them as if they were Javascript objects. I worked together with a team of three other classmates to write this file (and they were responsible for other aspects of the project individually). We make use of the Hapi ecosystem through our use of Knex, Joi, Boom, Objection, and Bcrypt. 

In this file, I'd like to point your attention to lines 418-460. Here I personally wrote the authentication handler. This route allows the user to sign in using a valid username and password; if the values are indeed valid (going through the database and checking the hashed passwords stored there against the unhashed passwords given by the user), then the server will serve back a token to use as confirmation that this session is valid. However, if the username OR password is incorrect, then an error message will be served and no token is given. 

I made use of ECMA Script 6's promise chaining to make sure that the error message is thrown approximately within a consistent time interval -- this way a hacker cannot check to see if an email is in the system, but the password is wrong, by calculating the time to receive a response from the server (generally if the server takes longer to respond, the email was correct but the password was not). This is for the utmost security.