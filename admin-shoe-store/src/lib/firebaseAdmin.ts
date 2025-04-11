import admin from "firebase-admin";

// Kiểm tra nếu Firebase Admin chưa khởi tạo
if (!admin.apps.length) {
  admin.initializeApp({
    credential: admin.credential.cert({
      projectId: process.env.FIREBASE_PROJECT_ID,
      clientEmail: process.env.FIREBASE_CLIENT_EMAIL,
      privateKey: process.env.FIREBASE_PRIVATE_KEY?.replace(/\\n/g, "\n"),
    }),
    databaseURL: `https://shop-giay-the-thao-default-rtdb.firebaseio.com`, // Thêm URL của Firebase Realtime Database
  });
}

const db = admin.database();

export { db };