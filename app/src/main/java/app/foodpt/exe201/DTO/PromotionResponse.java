package app.foodpt.exe201.DTO;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class PromotionResponse implements Parcelable {
    private int id;
    private String code;
    private String description;
    private boolean status;
    private double discountPercentage;
    private double fixedDiscountAmount;
    private long supplierId;

    public PromotionResponse() {
    }

    protected PromotionResponse(Parcel in) {
        id = in.readInt();
        code = in.readString();
        description = in.readString();
        status = in.readByte() != 0;
        discountPercentage = in.readDouble();
        fixedDiscountAmount = in.readDouble();
        supplierId = in.readLong();
    }

    public static final Creator<PromotionResponse> CREATOR = new Creator<PromotionResponse>() {
        @Override
        public PromotionResponse createFromParcel(Parcel in) {
            return new PromotionResponse(in);
        }

        @Override
        public PromotionResponse[] newArray(int size) {
            return new PromotionResponse[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(long supplierId) {
        this.supplierId = supplierId;
    }

    public double getFixedDiscountAmount() {
        return fixedDiscountAmount;
    }

    public void setFixedDiscountAmount(double fixedDiscountAmount) {
        this.fixedDiscountAmount = fixedDiscountAmount;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(code);
        parcel.writeString(description);
        parcel.writeByte((byte) (status ? 1 : 0));
        parcel.writeDouble(discountPercentage);
        parcel.writeDouble(fixedDiscountAmount);
        parcel.writeLong(supplierId);
    }
}