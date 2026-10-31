# Java Multi-Threaded Console Chat Application

A lightweight, multi-threaded server-client chat application written in **Vanilla Java** using standard Socket programming and **SQLite** for persistent chat history management.

---

## 🚀 Features

- **Multi-Client Support**: Handles multiple simultaneous client connections using Java threads.
- **Persistent Storage**: Saves all sent messages to a local SQLite database (`chat.db`).
- **Chat History**: New clients receive recent message history upon joining.
- **Personalized Messaging**: Sender sees `me` while other connected clients see the actual sender's username.
- **Timestamps**: Messages include real-time timestamp formatting (`[HH:mm:ss]`).
- **Zero Heavy Frameworks**: Built using standard Java Networking, Concurrency, and JDBC API.

---

## 🛠️ Prerequisites

- **Java JDK**: Version 17 or higher.
- **Apache Maven**: Version 3.8+ for building and dependency management.

---

## 📁 Project Structure

```text
vanilla-chat-sqlite/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   └── java/
    │       ├── Database.java       # SQLite initialization & database queries
    │       ├── Server.java     # Multi-threaded socket server & broadcast logic
    │       └── Client.java     # Console client for typing & receiving messages
    └── test/
        └── java/
            ├── TestDatabase.java         # Database unit tests
            └── TestIntegration.java  # Socket client-server integration tests
```

---

## 📦 Dependencies

The project uses Maven to manage the following dependencies:
- [SQLite JDBC Driver](https://github.com/xerial/sqlite-jdbc) for database connectivity.
- [SLF4J Simple Binding](https://www.slf4j.org/) for clean logging output.
- [JUnit 5](https://junit.org/junit5/) for testing.

---

## ⚙️ Setup & Building

1. **Clone or Download** the repository.
2. **Compile and Package** the application using Maven:
   ```bash
   mvn clean compile
   ```

---

## 🏃 Running the Application

### 1. Start the Chat Server
Run the server first. It will initialize the SQLite database (`chat.db`) and begin listening for client connections on port `12345`:

```bash
mvn exec:java -Dexec.mainClass="za.co.codes.Server"
```

### 2. Start Clients
Open one or more new terminal windows and launch a client in each:

```bash
mvn exec:java -Dexec.mainClass="za.co.codes.Client"
```

1. Enter a unique username when prompted.
2. Type messages into the console to chat.
3. Type `exit` to disconnect from the chat room.

---

## 🧪 Running Tests

Execute the unit and integration test suite via Maven:

```bash
mvn test
```

---

## 📝 Example Output

**Sender View:**
```text
Enter your username: Alice
Connected! Type your messages below (type 'exit' to quit):
[14:30:05] me: Hello everyone!
```

**Receiver View:**
```text
Enter your username: Bob
Connected! Type your messages below (type 'exit' to quit):
[14:30:05] Alice: Hello everyone!
```