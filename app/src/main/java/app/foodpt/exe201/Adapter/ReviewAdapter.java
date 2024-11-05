package app.foodpt.exe201.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import app.foodpt.exe201.DTO.Rating;
import app.foodpt.exe201.R;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Rating> ratingList;

    public ReviewAdapter(List<Rating> ratingList) {
        this.ratingList = ratingList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    // Hàm cập nhật danh sách đánh giá khi có dữ liệu mới
    public void updateRatings(List<Rating> newRatingList) {
        this.ratingList.clear(); // Xóa danh sách cũ
        this.ratingList.addAll(newRatingList); // Thêm danh sách mới
        notifyDataSetChanged(); // Thông báo rằng dữ liệu đã thay đổi
    }

    // Thêm phương thức addRating để thêm đánh giá mới
    public void addRating(Rating rating) {
        ratingList.add(rating);
        notifyItemInserted(ratingList.size() - 1); // Thông báo RecyclerView có item mới
    }
    // Interface for callback
    public interface AddRatingCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Rating rating = ratingList.get(position);
        holder.reviewerName.setText(rating.getFullName());
        holder.reviewText.setText(rating.getResponseMessage());
        holder.ratingBar.setRating(rating.getRatingStar());
        // Thiết lập Glide để bo tròn 4 góc
        Glide.with(holder.avatarImage.getContext())
                .load(rating.getAvatarUserUrl())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.loading) // Ảnh mặc định khi đang tải
                        .error(R.drawable.defaultavatar) // Ảnh mặc định khi URL rỗng hoặc lỗi
                        .centerCrop() // Cắt ảnh cho vừa vặn với ImageView hình vuông
                        .transform(new RoundedCorners(30))) // Bo tròn 4 góc
                .into(holder.avatarImage);

    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView reviewerName, reviewText;
        RatingBar ratingBar;
        ImageView avatarImage;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewerName = itemView.findViewById(R.id.reviewerName);
            reviewText = itemView.findViewById(R.id.reviewText);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            avatarImage = itemView.findViewById(R.id.avatarImage);
        }
    }
}
