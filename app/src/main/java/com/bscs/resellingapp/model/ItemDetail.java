package com.bscs.resellingapp.model;

public class ItemDetail {



   private String userId;
   private String itemId;
   private String productName;
   private String productPrice;
   private String productBrand;
   private String productCondition;
   private String productCategory;
   private String productDescription;
   private String imageUrl1;
   private String imageUrl2;
   private String imageUrl3;
   private int heartImage;

   public ItemDetail() {
      // Default constructor required for Firebase serialization
   }

   public ItemDetail(String userId, String itemId, String productName, String productPrice, String productBrand, String productCondition, String productCategory, String productDescription, String imageUrl1, String imageUrl2, String imageUrl3, int heartImage) {
      this.userId = userId;
      this.itemId = itemId;
      this.productName = productName;
      this.productPrice = productPrice;
      this.productBrand = productBrand;
      this.productCondition = productCondition;
      this.productCategory = productCategory;
      this.productDescription = productDescription;
      this.imageUrl1 = imageUrl1;
      this.imageUrl2 = imageUrl2;
      this.imageUrl3 = imageUrl3;
      this.heartImage = heartImage;
   }

   public ItemDetail(String itemId,String itemImage, String itemName, String itemPrice, int fillheart) {
      this.itemId=itemId;
      this.imageUrl1 = itemImage;
      this.productName = itemName;
      this.productPrice = itemPrice;
      this.heartImage = fillheart;
   }


   public int getHeartImage() {
      return heartImage;
   }
   public void setHeartImage(int heartImage) {
      this.heartImage = heartImage;
   }
   public String getUserId() {
      return userId;
   }
   public void setUserId(String userId) {
      this.userId = userId;
   }
   public String getItemId() {
      return itemId;
   }
   public void setItemId(String itemId) {
      this.itemId = itemId;
   }
   public String getProductName() {
      return productName;
   }
   public void setProductName(String productName) {
      this.productName = productName;
   }
   public String getProductPrice() {
      return productPrice;
   }
   public void setProductPrice(String productPrice) {
      this.productPrice = productPrice;
   }
   public String getProductBrand() {
      return productBrand;
   }
   public void setProductBrand(String productBrand) {
      this.productBrand = productBrand;
   }
   public String getProductCondition() {
      return productCondition;
   }
   public void setProductCondition(String productCondition) {
      this.productCondition = productCondition;
   }
   public String getProductCategory() {
      return productCategory;
   }
   public void setProductCategory(String productCategory) {
      this.productCategory = productCategory;
   }
   public String getProductDescription() {
      return productDescription;
   }
   public void setProductDescription(String productDescription) {
      this.productDescription = productDescription;
   }
   public String getImageUrl1() {
      return imageUrl1;
   }
   public void setImageUrl1(String imageUrl1) {
      this.imageUrl1 = imageUrl1;
   }
   public String getImageUrl2() {
      return imageUrl2;
   }
   public void setImageUrl2(String imageUrl2) {
      this.imageUrl2 = imageUrl2;
   }
   public String getImageUrl3() {
      return imageUrl3;
   }
   public void setImageUrl3(String imageUrl3) {
      this.imageUrl3 = imageUrl3;
   }
}
