package com.buysmart.ai.utils;

import android.net.Uri;
import com.buysmart.ai.models.Order;
import com.buysmart.ai.models.PriceAlert;
import com.buysmart.ai.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

/**
 * World-Class Firebase Manager for BuySmartAI.
 */
public class FirebaseManager {

    private static FirebaseManager instance;
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_ALERTS = "alerts";
    private static final String COLLECTION_ORDERS = "orders";
    private static final String COLLECTION_PRODUCTS = "products";

    private FirebaseManager() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    // ==================== AUTHENTICATION ====================

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public String getCurrentUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }

    public boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    public void signInWithEmail(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    public void signUpWithEmail(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    public void sendPasswordResetEmail(String email, OnCompleteListener<Void> listener) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(listener);
    }

    public void signOut() {
        auth.signOut();
    }

    // ==================== USER OPERATIONS ====================

    public void createUser(User user, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_USERS).document(user.getUserId()).set(user).addOnCompleteListener(listener);
    }

    public void getUser(String userId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection(COLLECTION_USERS).document(userId).get().addOnCompleteListener(listener);
    }

    public void updateUser(String userId, Map<String, Object> updates, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_USERS).document(userId).update(updates).addOnCompleteListener(listener);
    }

    public void uploadProfileImage(String userId, Uri imageUri, OnCompleteListener<Uri> listener) {
        StorageReference ref = storage.getReference().child("profiles/" + userId + ".jpg");
        ref.putFile(imageUri).continueWithTask(task -> ref.getDownloadUrl()).addOnCompleteListener(listener);
    }

    // ==================== SAVED PRODUCTS ====================

    public void saveProduct(String userId, String productId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_USERS)
                .document(userId)
                .update("savedProducts", FieldValue.arrayUnion(productId))
                .addOnCompleteListener(listener);
    }

    public void removeSavedProduct(String userId, String productId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_USERS)
                .document(userId)
                .update("savedProducts", FieldValue.arrayRemove(productId))
                .addOnCompleteListener(listener);
    }

    // ==================== ORDERS & ALERTS ====================

    public void placeOrder(Order order, OnCompleteListener<Void> listener) {
        String id = db.collection(COLLECTION_ORDERS).document().getId();
        order.setOrderId(id);
        db.collection(COLLECTION_ORDERS).document(id).set(order).addOnCompleteListener(listener);
    }

    public void createPriceAlert(PriceAlert alert, OnCompleteListener<Void> listener) {
        String id = db.collection(COLLECTION_ALERTS).document().getId();
        alert.setAlertId(id);
        db.collection(COLLECTION_ALERTS).document(id).set(alert).addOnCompleteListener(listener);
    }

    public void getUserOrders(String userId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection(COLLECTION_ORDERS).whereEqualTo("userId", userId).get().addOnCompleteListener(listener);
    }

    public void getUserAlerts(String userId, OnCompleteListener<QuerySnapshot> listener) {
        db.collection(COLLECTION_ALERTS).whereEqualTo("userId", userId).get().addOnCompleteListener(listener);
    }

    public void updateAlert(String alertId, Map<String, Object> updates, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_ALERTS).document(alertId).update(updates).addOnCompleteListener(listener);
    }

    public void deleteAlert(String alertId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_ALERTS).document(alertId).delete().addOnCompleteListener(listener);
    }

    // ==================== PRODUCT OPERATIONS ====================

    public void getProduct(String productId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection(COLLECTION_PRODUCTS).document(productId).get().addOnCompleteListener(listener);
    }
}
