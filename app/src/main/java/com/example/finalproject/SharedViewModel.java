package com.example.finalproject;

import android.util.Log;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private DatabaseReference userRef;
    private ValueEventListener userListener;
    private final MutableLiveData<QuoteModel> quote = new MutableLiveData<>();



    public LiveData<User> getUser() {
        return user;
    }
    public LiveData<QuoteModel> getQuote() {
        return quote;
    }


    public void fetchUserData(String userId) {
        if (userId == null) {
            user.setValue(null);
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        if (userListener != null) {
            userRef.removeEventListener(userListener);
        }

        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                user.postValue(currentUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("SharedViewModel", "loadUser:onCancelled", databaseError.toException());
                user.postValue(null);
            }
        };
        userRef.addValueEventListener(userListener);
    }

    public void getRandomQuote() {
        if(quote.getValue() != null){
            return;
        }

        RetrofitInterface apiInterface = RetrofitInstance.getRetrofitInstance().create(RetrofitInterface.class);
        String apiKey = BuildConfig.NINJA_API_KEY;
        String categories = "inspirational,success,wisdom";
        Call<List<QuoteModel>> call = apiInterface.getQuotes(apiKey, categories);

        call.enqueue(new Callback<List<QuoteModel>>() {
            @Override
            public void onResponse(Call<List<QuoteModel>> call, Response<List<QuoteModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    QuoteModel newQuote = response.body().get(0);
                    quote.setValue(newQuote);
                } else {
                    Log.e("ApiError", "Response not successful or body is empty. Code: " + response.code());

                }
            }
            @Override
            public void onFailure(Call<List<QuoteModel>> call, Throwable t) {
                Log.e("ApiError", "API call failed: " + t.getMessage(), t);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up the listener when the ViewModel is destroyed to prevent memory leaks
        if (userRef != null && userListener != null) {
            userRef.removeEventListener(userListener);
        }
    }
}
