package app.foodpt.exe201.DTO;

public class SupplierType {
    private int id;
    private String typeName;
    private String imgUrl;

    // Constructor không tham số
    public SupplierType() {
    }

    // Constructor có tham số
    public SupplierType(int id, String typeName, String imgUrl) {
        this.id = id;
        this.typeName = typeName;
        this.imgUrl = imgUrl;
    }

    // Getter và Setter cho id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter và Setter cho typeName
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    // Getter và Setter cho imgUrl
    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return typeName;  // Trả về tên của loại nhà cung cấp để hiển thị trong Spinner
    }
}
