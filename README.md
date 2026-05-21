# Concurrent Web Server with Configurable Thread Pool

## Overview

This project is a **Concurrent Web Server** developed using **Java** as part of the Operating Systems course.

The server is designed to handle multiple client requests at the same time using a configurable thread pool. It demonstrates important operating systems and networking concepts such as socket programming, multithreading, thread pools, synchronization, HTTP request handling, logging, and path validation.

## Project Objectives

The main objectives of this project are to:

- Implement a concurrent web server using Java.
- Handle multiple clients simultaneously using multithreading.
- Use a configurable thread pool to manage client requests.
- Serve static HTML files from a configurable directory.
- Support HTTP GET requests.
- Generate valid HTTP responses.
- Return proper HTTP status codes such as `200 OK`, `404 Not Found`, and `500 Internal Server Error`.
- Maintain access and error log files.
- Support server configuration using command-line arguments.
- Test the server under concurrent load conditions.

## Features

- Concurrent client handling
- Configurable server port
- Configurable thread pool size
- Configurable document root directory
- Static HTML file serving
- HTTP GET request processing
- HTTP response generation
- Access logging using `access.log`
- Error logging using `error.log`
- Path validation to prevent directory traversal attacks
- Handling of malformed or unsupported requests
- Concurrent load testing using Apache Benchmark

## Technologies Used

- Java
- Ubuntu Linux
- Java Socket Programming
- ServerSocket
- Socket
- Runnable
- ExecutorService
- File I/O
- Apache Benchmark

## Background Concepts

### TCP Socket Programming

The server uses TCP sockets to communicate with clients. A `ServerSocket` listens on a specific port, while clients connect through socket connections.

### Concurrent Server Design

Instead of processing one client at a time, the server handles multiple clients simultaneously. This improves responsiveness and allows many users to access the server at the same time.

### Thread Pool

The server uses a fixed thread pool through `ExecutorService`. Instead of creating a new thread for every request, the server reuses worker threads from the pool.

This improves performance, resource management, stability, and concurrent request handling.

## System Workflow

The web server follows this workflow:

1. The server reads configuration parameters.
2. A `ServerSocket` is created.
3. The server listens for incoming client connections.
4. When a client connects, the server accepts the connection.
5. The client request is assigned to a worker thread from the thread pool.
6. The worker thread reads and parses the HTTP request.
7. The requested file is located.
8. The server generates the correct HTTP response.
9. The request is logged.
10. The connection is closed safely.

## Server Architecture Components

### 1. Socket Listener Module

The socket listener module is responsible for opening the server port, listening for incoming TCP connections, accepting client requests, and passing client sockets to the thread pool.

### 2. Thread Pool Manager

The thread pool manager handles concurrent execution of client requests.

```java
ExecutorService pool = Executors.newFixedThreadPool(THREADS);
```

Each client is processed using:

```java
pool.execute(new ClientHandler(client));
```

### 3. Client Handler Module

The `ClientHandler` module is responsible for handling each client request independently.

It performs reading HTTP requests, parsing request headers, extracting the requested file path, serving static files, sending HTTP responses, handling exceptions, and updating log files.

### 4. Request Parser

The request parser reads the HTTP request line and extracts the HTTP method, requested path, and HTTP version.

```java
BufferedReader in = new BufferedReader(
    new InputStreamReader(client.getInputStream())
);

String requestLine = in.readLine();
```

### 5. Static File Handling

The server serves static HTML files from the configured root directory.

If the user requests `/`, the server loads `index.html`.

If the requested file does not exist, the server returns `404 Not Found`.

### 6. HTTP Response Builder

The server generates different HTTP responses depending on the request result.

Example success response:

```http
HTTP/1.1 200 OK
Content-Type: text/html; charset=UTF-8
```

Example not found response:

```http
HTTP/1.1 404 Not Found
```

Example server error response:

```http
HTTP/1.1 500 Internal Server Error
```

### 7. Logging Module

The server maintains two log files:

```text
access.log
error.log
```

The `access.log` file stores incoming client requests.

Example:

```text
Request: GET / HTTP/1.1
```

The `error.log` file stores server-side exceptions and errors.

Example:

```text
java.lang.ArithmeticException: / by zero
```

### 8. Configuration Module

The server supports runtime configuration using command-line arguments.

Example:

```bash
java WebServer 9090 10 .
```

Where:

- `9090` is the port number
- `10` is the number of worker threads
- `.` is the document root directory

## Path Validation and Security

The server includes path validation to prevent directory traversal attacks.

For example, requests like `../../etc/passwd` are rejected to prevent users from accessing files outside the server root directory.

This improves server security by ensuring that only files inside the configured document root can be accessed.

## Testing and Evaluation

The server was tested using a web browser and Apache Benchmark.

### Functional Testing

| Test Case | Expected Result |
|----------|-----------------|
| Access `/` | Returns `200 OK` |
| Request missing file | Returns `404 Not Found` |
| Simulated server error | Returns `500 Internal Server Error` |
| Check `access.log` | Requests are logged |
| Check `error.log` | Errors are logged |

## Concurrent Load Testing

Apache Benchmark was used to test the server under concurrent access.

Command used:

```bash
ab -n 100 -c 30 http://localhost:9090/
```

Where:

- `-n 100` means 100 total requests
- `-c 30` means 30 concurrent users

### Results

- The server handled concurrent requests successfully.
- No crashes occurred.
- No failed requests were reported.
- Correct responses were returned.
- Logging worked correctly.
- No synchronization issues were observed.
- The server achieved approximately 363 requests per second.

## Handling Malformed Requests

The server was tested with unsupported HTTP requests such as:

```bash
curl -X POST http://localhost:9090/
```

Although the server mainly supports HTTP GET requests, it continued running normally and did not crash. This shows that the server can tolerate invalid or unexpected requests safely.


## How to Run

Compile the Java file:

```bash
javac WebServer.java
```

Run the server with default settings:

```bash
java WebServer
```

Run the server with custom configuration:

```bash
java WebServer 9090 10 .
```

Then open the browser and go to:

```text
http://localhost:9090/
```

## Sample Source Code

```java
ExecutorService pool = Executors.newFixedThreadPool(THREADS);

ServerSocket server = new ServerSocket(PORT);

while (true) {
    Socket client = server.accept();
    pool.execute(new ClientHandler(client));
}
```

## Team Members

- Mazen Yasser
- Mariam Mohamed Reyad
- Sandy Atef

## Course Information

**Module Title:** Operating Systems  
**Module Code:** CSE264 / CSE364  
**Semester:** Spring 2026  
**Supervisor:** Dr. Mokhtar Ahmed  

## Conclusion

This project successfully implemented a concurrent multithreaded web server using Java socket programming and thread pool architecture.

The server can handle multiple client connections at the same time, process HTTP GET requests, serve static HTML files, generate correct HTTP responses, and maintain access and error logs.

The project provided practical experience in Java networking, multithreading, synchronization, HTTP communication, secure file handling, and concurrent server development.
