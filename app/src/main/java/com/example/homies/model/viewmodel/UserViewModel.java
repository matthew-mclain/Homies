package com.example.homies.model.viewmodel;

import androidx.annotation.NonNull;

import com.example.homies.MyApplication;
import com.example.homies.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

import timber.log.Timber;

public class UserViewModel {
    private static FirebaseFirestore db;
    private static final String TAG  = UserViewModel.class.getSimpleName();
    User user;
    public void getUserInformation(String userId){
        db = MyApplication.getDbInstance();
        db.collection("users")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                            Map<String, Object> data = doc.getData();
                            User user = new User((String)data.get("email"), (String)data.get("displayName"));
                            setUser(user);
                            Timber.tag(TAG).d(task.getException(), "found user by userId: %s", userId);
                        } else{
                            Timber.tag(TAG).e(task.getException(), "Error fetching user by userId: %s", userId);
                        }
                    }
                });
    }

    public User getUser(){ return user; }

    private void setUser(User user){ this.user = user; }

}
