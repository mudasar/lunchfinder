package uk.appinvent.lunchfinder.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mudasar on 23/06/16.
 */
public class Dish  {

    private long id;

    @SerializedName("Id")
    private long refId;

    @SerializedName("Name")
    private String name;

    @SerializedName("Description")
    private String description;

    @SerializedName("WaitingTime")
    private Integer waitingTime;

    @SerializedName("Price")
    private Double price;

    @SerializedName("ImageUrl")
    private String imageUrl;

    @SerializedName("ShortDescription")
    private String shortDescription;

    @SerializedName("IsDishOftheDay")
    private Boolean isDishOftheDay;

    @SerializedName("DishOfDayNumber")
    private Integer dishOfDayNumber;

    @SerializedName("Category")
    private String category;


    /**
     * Empty constructor
     */
    public Dish() {
    }

    public Dish(long id, String name, String description, Integer waitingTime, Double price, String imageUrl, String shortDescription, Boolean isDishOftheDay, Integer dishOfDayNumber, String category) {
        this.refId = id;
        this.name = name;
        this.description = description;
        this.waitingTime = waitingTime;
        this.price = price;
        this.imageUrl = imageUrl;
        this.shortDescription = shortDescription;
        this.isDishOftheDay = isDishOftheDay;
        this.dishOfDayNumber = dishOfDayNumber;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(Integer waitingTime) {
        this.waitingTime = waitingTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Boolean getDishOftheDay() {
        return isDishOftheDay;
    }

    public void setDishOftheDay(Boolean dishOftheDay) {
        isDishOftheDay = dishOftheDay;
    }

    public Integer getDishOfDayNumber() {
        return dishOfDayNumber;
    }

    public void setDishOfDayNumber(Integer dishOfDayNumber) {
        this.dishOfDayNumber = dishOfDayNumber;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getRefId() {
        return refId;
    }

    public void setRefId(long refId) {
        this.refId = refId;
    }
}
