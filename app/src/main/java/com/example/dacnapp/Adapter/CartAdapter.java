package com.example.dacnapp.Adapter;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dacnapp.Activity.CartActivity;
import com.example.dacnapp.Activity.DetailActivity;
import com.example.dacnapp.DAO.CartDAO;
import com.example.dacnapp.Helper.PositiveNumberInputFilter;
import com.example.dacnapp.MainActivity;
import com.example.dacnapp.Model.Cart;
import com.example.dacnapp.Model.Product;
import com.example.dacnapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    Context context;
    private boolean isSwiped = false;
    ArrayList<Cart> listCart;
    private OnCartChangeListener onCartChangeListener;
    MainActivity mainActivity = MainActivity.getInstance();
    DetailActivity detailActivity = DetailActivity.getInstance();
    DecimalFormat decimalFormat = new DecimalFormat("#,##0");

    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public CartAdapter(Context context, ArrayList<Cart> listCart) {
        this.context = context;
        this.listCart = listCart;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartAdapter.CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Cart cart = listCart.get(position);
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Products").document(cart.getProductId());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    {
                        Product product = documentSnapshot.toObject(Product.class);
                        assert product != null;
                        holder.txtTitle.setText(product.getTitle());
                        String gia = decimalFormat.format(Integer.parseInt(product.getPrice()));
                        holder.txtPrice.setText(gia);
                        holder.txtPrice.append(" ₫");
                        Glide.with(holder.imgThumb.getContext())
                                .load(product.getThumb())
                                .error(R.mipmap.ic_launcher)
                                .into(holder.imgThumb);
                    }
                }
            }
        });
        holder.edtSoLg.setText(String.valueOf(cart.getSoLg()));
        holder.edtSoLg.setImeOptions(EditorInfo.IME_ACTION_DONE);
        holder.edtSoLg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Xử lý tác vụ khi nhấn phím "Enter" hoặc "Done"
                    int newSoLg = Integer.parseInt(v.getText().toString());

                    if (newSoLg == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Xác nhận xóa");
                        builder.setMessage("Bạn có chắc chắn muốn xóa mục hàng này?");
                        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Xóa mục hàng từ cơ sở dữ liệu
                                new CartDAO(context).deleteCartItem(userId, cart.getProductId());
                                // Tải lại hoạt động
                                listCart.remove(position);
                                notifyItemRemoved(position);
                                if (onCartChangeListener != null) {
                                    onCartChangeListener.onCartChanged();
                                }
                                cart.setSoLg(newSoLg);
                                mainActivity.setBadgeCart();

                            }
                        });
                        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Không làm gì nếu không xóa
                                int so = cart.getSoLg();
                                holder.edtSoLg.setText(String.valueOf(so));
                                new CartDAO(context).updateSoLg(userId, cart.getProductId(), so);
                                cart.setSoLg(so);
                                if (onCartChangeListener != null) {
                                    onCartChangeListener.onCartChanged();
                                }
                            }
                        });
                        builder.show();
                    } else {
                        holder.edtSoLg.setText(String.valueOf(newSoLg));
                        new CartDAO(context).updateSoLg(userId, cart.getProductId(), newSoLg);
                        cart.setSoLg(newSoLg);
                        if (onCartChangeListener != null) {
                            onCartChangeListener.onCartChanged();
                        }
                    }

                    mainActivity.setBadgeCart();
                    holder.edtSoLg.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    // Trả về true để thông báo rằng sự kiện đã được xử lý
                    return true;
                }
                // Trả về false để tiếp tục xử lý sự kiện mặc định
                return false;
            }
        });
        holder.btnIncrement.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                int solg = cart.getSoLg();
                holder.edtSoLg.setText(String.valueOf(solg + 1));
                new CartDAO(context).updateSoLg(userId, cart.getProductId(), solg + 1);
                cart.setSoLg(solg + 1);
                if (onCartChangeListener != null) {
                    onCartChangeListener.onCartChanged();
                }
                mainActivity.setBadgeCart();
            }
        });
        holder.btnDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int solg = cart.getSoLg();
                int newSo = solg - 1;
                if (newSo == 0) {
                    // Hiển thị hộp thoại xác nhận xóa
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Xác nhận xóa");
                    builder.setMessage("Bạn có chắc chắn muốn xóa mục hàng này?");
                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Xóa mục hàng từ cơ sở dữ liệu
                            new CartDAO(context).deleteCartItem(userId, cart.getProductId());
                            // Tải lại hoạt động
                            listCart.remove(position);
                            notifyItemRemoved(position);
                            if (onCartChangeListener != null) {
                                onCartChangeListener.onCartChanged();
                            }
                            cart.setSoLg(newSo);
                            mainActivity.setBadgeCart();

                        }
                    });
                    builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Không làm gì nếu không xóa
                            int so = newSo + 1;
                            holder.edtSoLg.setText(String.valueOf(so));
                            new CartDAO(context).updateSoLg(userId, cart.getProductId(), so);
                            cart.setSoLg(so);
                            if (onCartChangeListener != null) {
                                onCartChangeListener.onCartChanged();
                            }
                        }
                    });
                    builder.show();
                } else {
                    holder.edtSoLg.setText(String.valueOf(newSo));
                    new CartDAO(context).updateSoLg(userId, cart.getProductId(), newSo);
                    cart.setSoLg(newSo);
                    if (onCartChangeListener != null) {
                        onCartChangeListener.onCartChanged();
                    }
                }
                mainActivity.setBadgeCart();

            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xác nhận xóa");
                builder.setMessage("Bạn có chắc chắn muốn xóa mục hàng này?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem(position, userId);
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Không làm gì nếu không xóa
                    }
                });
                builder.show();
            }
        });
    }

    public void setOnCartChangeListener(OnCartChangeListener listener) {
        this.onCartChangeListener = listener;
    }

    public void deleteItem(int position, String userId) {
        new CartDAO(context).deleteCartItem(userId, listCart.get(position).getProductId());
        // Tải lại hoạt động
        listCart.remove(position);
        notifyItemRemoved(position);
        if (onCartChangeListener != null) {
            onCartChangeListener.onCartChanged();
        }
        mainActivity.setBadgeCart();
    }

    public interface OnCartChangeListener {
        void onCartChanged();
    }

    @Override
    public int getItemCount() {
        return listCart.size();
    }

    public interface TotalPriceCallback {
        void onTotalPriceCalculated(int totalPrice);
    }

    public void calculateTotalPrice(TotalPriceCallback callback) {
        int[] totalPrice = {0};
        int[] counter = {0};

        for (Cart cart : listCart) {
            String productId = cart.getProductId();
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("Products").document(productId);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        if (product != null) {
                            int quantity = cart.getSoLg();
                            int price = Integer.parseInt(product.getPrice());
                            totalPrice[0] += quantity * price;
                        }
                    }

                    counter[0]++;
                    if (counter[0] == listCart.size()) {
                        callback.onTotalPriceCalculated(totalPrice[0]);
                    }
                }
            });
        }
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView txtTitle, txtPrice, txtTotalPrice;
        EditText edtSoLg;
        Button btnDecrement, btnIncrement, btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgThumbCart);
            txtTitle = itemView.findViewById(R.id.txtTilteCart);
            txtPrice = itemView.findViewById(R.id.txtPriceCart);
            edtSoLg = itemView.findViewById(R.id.edtSoLgCart);
            btnIncrement = itemView.findViewById(R.id.btnIncrement);
            btnDecrement = itemView.findViewById(R.id.btnDecrement);
            btnDelete = itemView.findViewById(R.id.btnDeleteCart);
            txtTotalPrice = itemView.findViewById(R.id.txtTotalPrice);
        }
    }
}
