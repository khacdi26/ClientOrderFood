package com.example.dacnapp.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dacnapp.Adapter.CategoryAdapter;
import com.example.dacnapp.Adapter.NonScrollableGridLayoutManager;
import com.example.dacnapp.Adapter.ProductAdapter;
import com.example.dacnapp.Model.Category;
import com.example.dacnapp.Model.Product;
import com.example.dacnapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    RecyclerView revProduct, revCategory;
    CollectionReference proRef, cateRef;
    ProductAdapter productAdapter;
    ArrayList<Product> list;
    CategoryAdapter categoryAdapter;
    ArrayList<Category> categories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        revProduct = view.findViewById(R.id.revListProduct);
        proRef = FirebaseFirestore.getInstance().collection("Products");
        NonScrollableGridLayoutManager layoutManager = new NonScrollableGridLayoutManager(getActivity(), 2);
        revProduct.setLayoutManager(layoutManager);
        revProduct.setNestedScrollingEnabled(false);
        list = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), list);
        revProduct.setAdapter(productAdapter);
        proRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = document.toObject(Product.class);
                        product.setId(document.getId());

                        list.add(product);
                    }
                    productAdapter.notifyDataSetChanged();
                }
            }
        });
        revCategory = view.findViewById(R.id.revCategories);
        revCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(revCategory);

        cateRef = FirebaseFirestore.getInstance().collection("Categories");
        categories = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categories);
        revCategory.setAdapter(categoryAdapter);
        cateRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Category category = document.toObject(Category.class);
                        categories.add(category);
                    }
                    categoryAdapter.notifyDataSetChanged();
                }
            }
        });

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int currentPosition = 0;

            @Override
            public void run() {
                if (currentPosition < categories.size() - 1) {
                    currentPosition++;
                } else {
                    currentPosition = 0;
                }
                revCategory.smoothScrollToPosition(currentPosition);
            }
        }, 0, 3000); // Chuyển đổi hình ảnh sau mỗi 3 giây (3000 milliseconds)
    }
}
