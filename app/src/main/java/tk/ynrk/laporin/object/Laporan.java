package tk.ynrk.laporin.object;

import android.graphics.Bitmap;

public class Laporan {
    String id;
    String description;
    float longitude;
    float latitude;
    String status;
    String image;
    String createdAt;
    String upadatedAt;
    Bitmap mImage;

    boolean expanded = false;

    public Laporan(){

    }

    public Laporan(String id, String description, float longitude, float latitude, String status, String image, String createdAt, String upadatedAt) {
        this.id = id;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.status = status;
        this.image = image;
        this.createdAt = createdAt;
        this.upadatedAt = upadatedAt;
    }

    public void toggleExpanded() {
        this.expanded = !expanded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpadatedAt() {
        return upadatedAt;
    }

    public void setUpadatedAt(String upadatedAt) {
        this.upadatedAt = upadatedAt;
    }

    public Bitmap getmImage() {
        return mImage;
    }

    public void setmImage(Bitmap mImage) {
        this.mImage = mImage;
    }
}
