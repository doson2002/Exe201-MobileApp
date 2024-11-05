package app.foodpt.exe201.DTO;

public class UpdateTimeRequestDTO {
    private String openTime; // Thời gian mở cửa dưới dạng chuỗi (HH:mm)
    private String closeTime; // Thời gian đóng cửa dưới dạng chuỗi (HH:mm)

    public UpdateTimeRequestDTO( String openTime, String closeTime) {
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    // Getters và Setters

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }
}