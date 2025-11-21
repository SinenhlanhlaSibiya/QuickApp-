#  Message Application (Java, NetBeans, ANT)  
A three–part Java project designed for educational purposes, demonstrating **user authentication**, **message processing**, **message storage**, and **report generation** using GUI dialogs (JOptionPane) and JSON persistence.

This project was developed in **NetBeans (ANT project)** and includes full **Part 1, Part 2, and Part 3** functionality with TestNG unit tests.

---

##  Features

###  **Part 1 – User Registration & Login**
- Create a user account with:
  - Username validation (must contain underscore, max 5 characters)
  - Password complexity rules (capital letter, number, special character, 8+ length)
  - Cellphone validation (+27xxxxxxxxx)
- Secure login verification.
- Fully tested using TestNG.

---

###  **Part 2 – Message Creation & Validation**
- Create a message with:
  - Recipient cell validation
  - Unique message ID (auto generated)
  - Auto-generated message hash
  - Message length validation (max 250 characters)
- User chooses to:  
  **Send message**, **Disregard**, or **Store message**
- Uses `JOptionPane` for GUI interaction.

---

###  **Part 3 – Message Management System**
Fully integrated message manager featuring:

- Dynamic arrays/lists for:
  - Sent messages
  - Stored messages
  - Disregarded messages
  - Message IDs
  - Message Hashes
- JSON file storage (`storedMessages.json`) using **GSON**
- Reporting functions:
  - Display sender + recipient of all sent messages  
  - Display the *longest* sent message  
  - Search by message ID  
  - Search by recipient  
  - Delete using message hash  
  - Full message report popup  

---

##  Unit Testing (TestNG)

The project includes comprehensive non-GUI tests covering:
- Login validation  
- Password rules  
- Phone number validation  
- MessageManager list population  
- JSON file creation  
- Longest message detection  
- Parallel array integrity  

### Required Testing Libraries
Be sure to add these to **Project → Properties → Libraries → Compile Tests**:
- `testng-7.9.0.jar`
- `slf4j-api-1.7.36.jar`
- `slf4j-simple-1.7.36.jar`

---

## 📂 Project Structure

