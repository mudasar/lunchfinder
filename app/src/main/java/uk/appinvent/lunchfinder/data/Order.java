package uk.appinvent.lunchfinder.data;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mudasar on 03/07/16.
 */
public class Order {

    private long id;
    private long dishId ;
    private String dishName;
    private String dishImage;
    private int Qty;
    private String uid;
    private String username;
    private String status;
    private String userId;

    public Order(long id, long dishId, String dishName, String dishImage, int qty, String uid, String username, String status, String userId) {
        this.id = id;
        this.dishId = dishId;
        this.dishName = dishName;
        this.dishImage = dishImage;
        Qty = qty;
        this.uid = uid;
        this.username = username;
        this.status = status;
        this.userId = userId;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("dishId", dishId);
        result.put("dishName", dishName);
        result.put("dishImage", dishImage);
        result.put("qty", Qty);
        result.put("username", username);
        result.put("status", status);
        result.put("userId",userId);

        return result;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getDishId() {
        return dishId;
    }

    public void setDishId(long dishId) {
        this.dishId = dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getDishImage() {
        return dishImage;
    }

    public void setDishImage(String dishImage) {
        this.dishImage = dishImage;
    }

    public int getQty() {
        return Qty;
    }

    public void setQty(int qty) {
        Qty = qty;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
