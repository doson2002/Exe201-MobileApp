package app.foodpt.exe201.DTO;

public class PhuongXa {
    private String id;
    private String fullName;

    public PhuongXa(String id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }



    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return fullName;  // Hiển thị tên tỉnh trong Spinner
    }
}

