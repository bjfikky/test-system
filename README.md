# Test System

This is a minimum viable product of a **Test System**. It is **Java Spring-boot** backend application that provides APIs that allows:
- creation of **Questions** with **Options**
- creation of a **Test Taker**
- **Test** creation for a **Test Taker**
- submission of **Test Answers** by **Test Taker**, with **Result**.


## Technology Used

1. Java 17
2. Spring-boot
3. Java Persistent API (JPA)
4. PostgreSQL
5. Integration Testing
6. Unit Testing
7. Docker for TestContainers
8. GitHub Continuous Integration (CI)

## Running the Application

On your system, you will need to install:
- Maven
- PostgreSQL
- Docker (for running the integration tests)

1. **Build the Project:**
   Navigate to the root directory of your Maven Spring Boot project using the command line. Run the following Maven command to build the project:
    ```
    mvn clean install
    ```
   This command will compile the code, run tests, and package the application into a JAR or WAR file. You will need to install maven on your system. As well as PostgreSQL.

2. **Run the Application:**
   Once the build is successful, you can use the `java -jar` command to run the generated JAR file. The JAR file will typically be located in the `target` directory. Navigate to the `target` directory and run the following command:
    ```
    java -jar test-system.jar
    ```

3. **Access the Application:**
   Once the application is running, you can access it by opening a web browser and navigating to the specified port. By default, Spring Boot applications run on port 8080. If you have configured a different port, you can access the application using that port.

   Open a web browser and go to http://localhost:8080/api/version or the configured port. This should return the application version number.

Keep in mind that you might need to configure your application properties in `application.yml` to match the username and password for. your PostgreSQL.

To run the application tests, start Docker on your system, then run:
```
mvn clean test
```
