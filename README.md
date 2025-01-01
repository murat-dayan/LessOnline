# LessOnline

This project is an Android application where teachers can share posts, students can comment on them, and teachers can initiate private messaging with students under specific conditions. The app also includes a chatbot feature that answers user questions related to the app.

## Features

### 1. **Post Sharing**
- Only teachers can share posts.
- Posts are stored using Firebase Firestore and Firebase Storage.
- Posts can be liked and saved by users.

### 2. **Comments**
- Students can comment on teachers' posts.
- Teachers can view comments on their posts.
- Teachers can initiate private discussions with students through a "Discuss" button.

### 3. **Private Messaging**
- Teachers can start private conversations with students who comment on their posts.
- Messaging functionality is developed as a separate module.
- Students can only reply to a teacher's message after the teacher starts the conversation.

### 4. **Chatbot Module**
- The chatbot answers app-related user questions.
- User data is fetched from Firebase.
- Integrated with Google Generative AI via a JSON-based API.


# Project Screenshots

Here are the screenshots of the application, grouped in rows of three for better readability.

---

<div align="center">

### Login,Register, User Save and Forget Password Pages

<img src="https://github.com/user-attachments/assets/010e4aa0-36b0-44d2-be5b-f820ec58e7f0" alt="Login" width="200"/>
<img src="https://github.com/user-attachments/assets/a5d763bf-d1fa-4603-b69b-0761f6b23b26" alt="Register" width="200"/>
<img src="https://github.com/user-attachments/assets/5afd8894-fb04-4aa4-b324-53e3e51f6456" alt="Save User" width="200"/>
<img src="https://github.com/user-attachments/assets/0a32cb3a-400d-4a87-8f1a-29b4f2588d1f" alt="Forget Password" width="200"/>

---

### Home, Search,Notifications, Profile, Saved Posts Pages

<img src="https://github.com/user-attachments/assets/0f4b81b9-35e2-4ece-a9d0-5ab361f6f8bc" alt="Home" width="200"/>
<img src="https://github.com/user-attachments/assets/9f4e7060-550b-4d4b-8dad-bb0e93f9be0a" alt="Comments" width="200"/>
<img src="https://github.com/user-attachments/assets/9eb680f2-5654-4e1f-837e-0910e985fb06" alt="Search" width="200"/>
<img src="https://github.com/user-attachments/assets/12ffe219-6978-433e-9632-64f34753c318" alt="Notifications" width="200"/>
<img src="https://github.com/user-attachments/assets/6ca8003c-e12e-4bbd-baad-bf29b87813a8" alt="Profile" width="200"/>
<img src="https://github.com/user-attachments/assets/325a4f99-680a-4f2a-9762-8fd306497f41" alt="Sign out" width="200"/>
<img src="https://github.com/user-attachments/assets/4fa0ba9b-87c3-4ba2-b1d2-9ab97748cd7b" alt="Saved Posts" width="200"/>


---

### Post Sharing Pages

<img src="https://github.com/user-attachments/assets/0fefa6a1-3532-4179-919c-956079c6b138" alt="Reports" width="200"/>
<img src="https://github.com/user-attachments/assets/a1de88c7-62f9-4930-bbd4-9c653677a8ac" alt="Chat Feature" width="200"/>
<img src="https://github.com/user-attachments/assets/cc8d58fa-120b-40b5-86bc-fea8c88d7a58" alt="Teacher Dashboard" width="200"/>
<img src="https://github.com/user-attachments/assets/427f514e-02f1-4f23-acc8-4c06f806d126" alt="User Guide" width="200"/>

---

### Chatting

<img src="https://github.com/user-attachments/assets/b97c7b79-8574-40fb-9d62-7b2c4eda632b" alt="Miscellaneous 1" width="200"/>
<img src="https://github.com/user-attachments/assets/36ee8b54-ca67-40cb-8f63-cd310116225a" alt="Miscellaneous 2" width="200"/>

---

### Chatbot


<img src="https://github.com/user-attachments/assets/afdfc59e-0867-4e4b-a7c6-29c7ac6b03e8" alt="Saved Posts" width="200"/>

---

</div>

## Technical Details

### **Technologies and Libraries**
- **Language:** Kotlin
- **Architecture:** MVVM (Model-View-ViewModel)
- **Firebase:** Firestore, Authentication, Storage
- **Hilt:** For dependency injection
- **RecyclerView:** For listing posts and comments
- **StateFlow:** For managing data flows
- **android-image-cropper:** For image cropping
- **AdMob:** For ad integration
-  **Retrofit:** For a chatbot api

### **Role Management**
- User roles are managed using a `Role` enum class (Teacher and Student).
- Only users with the teacher role can share posts.

### **Structure**
- `App`: Home features
- `ChatModule`: Manages teacher-student messaging.
- `ChatbotModule`: Handles chatbot questions and answers.

### **Setup**
1. Clone the repository:
   ```bash
   git clone https://github.com/murat-dayan/LessOnline.git
   cd lessonline
