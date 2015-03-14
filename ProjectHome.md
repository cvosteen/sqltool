Allows users to connect to multiple ODBC/JDBC compliant databases and execute custom SQL queries or commands on these databases.

This program was originally created when I found I needed to execute SQL queries on a number of databases.  At my workplace we have almost a dozen different ODBC backed applications, all of which have crummy reporting tools.  SQL queries are much more powerful and flexible.  I designed the program to allow ODBC/JDBC access to these databases and to save the SQL queries for later reuse.  It has made my life easier, so I've open sourced it with hopes that it can do the same for someone else.

This program is written in java and uses the Swing GUI library.