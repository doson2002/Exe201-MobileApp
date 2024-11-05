package app.foodpt.exe201.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.foodpt.exe201.DTO.PromotionResponse;
import app.foodpt.exe201.R;

import java.util.List;

public class PromotionCustomerAdapter extends RecyclerView.Adapter<PromotionCustomerAdapter.PromotionViewHolder> {

    private List<PromotionResponse> promotionList;
    private int selectedPosition = -1; // Vị trí được chọn

    private OnPromotionSelectedListener listener;

    // Constructor
    public PromotionCustomerAdapter(List<PromotionResponse> promotionList, OnPromotionSelectedListener listener) {
        this.promotionList = promotionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PromotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher_customer, parent, false);
        return new PromotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionViewHolder holder, int position) {
        PromotionResponse promotion = promotionList.get(position);

        holder.textViewCode.setText(promotion.getCode());
        holder.textViewVoucherDescription.setText(promotion.getDescription());
        // Kiểm tra và thiết lập giá trị giảm giá
        if (promotion.getDiscountPercentage() > 0) {
            holder.textViewValue.setText(String.format("Giảm %.0f%%", promotion.getDiscountPercentage()*100));
            holder.textViewDiscountValue.setText(String.format("%.0f%% OFF", promotion.getDiscountPercentage()*100));

        } else if (promotion.getFixedDiscountAmount() > 0) {
            holder.textViewValue.setText(String.format("Giảm %.0f đ", promotion.getFixedDiscountAmount()));
            holder.textViewDiscountValue.setText(String.format("%s OFF", formatDiscountAmount(promotion.getFixedDiscountAmount())));

        } else {
            holder.textViewValue.setText("Không có giảm giá");
        }

        // Thiết lập hình ảnh cho ImageView
        // Glide.with(holder.itemView.getContext()).load(promotion.getImageUrl()).into(holder.imageViewVoucherIcon);

        // Kiểm tra nếu position hiện tại là vị trí đã chọn
        holder.radioButtonSelectVoucher.setChecked(position == selectedPosition);

        // Đặt sự kiện click cho RadioButton
        holder.radioButtonSelectVoucher.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition(); // Lấy vị trí hiện tại
            if (currentPosition != RecyclerView.NO_POSITION) {
                if (listener != null) {
                    listener.onPromotionSelected(currentPosition);
                }
            }
            if (currentPosition != RecyclerView.NO_POSITION) { // Kiểm tra xem vị trí có hợp lệ không
                if (selectedPosition != currentPosition) {
                    // Cập nhật vị trí đã chọn
                    notifyItemChanged(selectedPosition); // Cập nhật lại vị trí trước đó
                    selectedPosition = currentPosition; // Cập nhật vị trí mới
                    notifyItemChanged(selectedPosition); // Cập nhật lại vị trí mới
                }
            }
        });

        // Nếu status là false, làm mờ và không cho chọn
        if (!promotion.isStatus()) {
            holder.radioButtonSelectVoucher.setEnabled(false);
            holder.itemView.setAlpha(0.5f); // Làm mờ
        } else {
            holder.radioButtonSelectVoucher.setEnabled(true);
            holder.itemView.setAlpha(1.0f); // Đặt lại độ mờ
        }
    }
    // Phương thức định dạng số tiền
    private String formatDiscountAmount(double amount) {
        if (amount >= 1000) {
            return String.format("%.0fk", amount / 1000); // Chia cho 1000 và định dạng lại
        }
        return String.format("%.0f", amount); // Trả về giá trị nguyên nếu dưới 1000
    }

    @Override
    public int getItemCount() {
        return promotionList.size();
    }

    // ViewHolder cho Promotion
    public static class PromotionViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewVoucherIcon;
        TextView textViewCode;
        TextView textViewVoucherDescription;
        TextView textViewValue, textViewDiscountValue;
        RadioButton radioButtonSelectVoucher;

        public PromotionViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewVoucherIcon = itemView.findViewById(R.id.imageViewVoucherIcon);
            textViewCode = itemView.findViewById(R.id.textViewCode);
            textViewVoucherDescription = itemView.findViewById(R.id.textViewVoucherDescription);
            textViewValue = itemView.findViewById(R.id.textViewValue);
            textViewDiscountValue = itemView.findViewById(R.id.textViewDiscountValue);
            radioButtonSelectVoucher = itemView.findViewById(R.id.radioButtonSelectVoucher);
        }
    }
    // Thêm một interface callback
    public interface OnPromotionSelectedListener {
        void onPromotionSelected(int position);
    }
}