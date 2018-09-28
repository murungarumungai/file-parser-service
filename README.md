# file-parser-service
This repo contains the source to the file parser service that reads a log file and stores it's content to a DB 
and exposes the data via a resful API using embedded Jetty web server achieved through [SparkJava](http://sparkjava.com/) 
# Prerequisites
You will need the below to build and run the service
1. Gradle 4 or higher
2. JDK 8 or higher

# Setting Up
- Clone the Repository from Github

# Building
  ```sh
$ cd file-parser-service
$ gradle fatJar
```
# config.properties
- `APIPORT` the port used to expose the resfful API.
- `ProcessedLogFilesPath` the path where processed log files are moved.

# hibernate.properties File
- `hibernate.connection.driver_class` JDBC driver class.
- `hibernate.connection.url` JDBC URL to the database instance.
- `hibernate.connection.username` database username.
- `hibernate.connection.password` database password.
- `hibernate.dialect` makes Hibernate generate the appropriate SQL for the chosen database.

# Starting the service
Run the command
`java -jar{path to jar file}`

# Process Log File
- Request type **POST** 
- Request attribute - **Multipart** key **logFile**
- Route - `/api/v1/logEntries/upload`

#Get Data
- Request type **GET**
- Params (optional) 
  -  `page_size`
  -  `offset`
- Route - `/api/v1/logEntries`
