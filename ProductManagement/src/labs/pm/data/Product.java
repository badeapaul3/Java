/*
 * Copyright (c) 2024.
 *
 * This is free software and can be used/distributed.
 */

package labs.pm.data;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;



public class Product {
    private int id;
    private String name;
    private BigDecimal price;

    private Rating rating;



    public Product() {
        this(0,"no name",BigDecimal.ZERO);
    }

    public Product(int id, String name, BigDecimal price, Rating rating) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.rating = rating;
    }

    public Product(int id, String name, BigDecimal price) {
       this(id, name, price, Rating.NOT_RATED);
    }



    /**
     * A constant that defines a
     * {@link java.math.BigDecimal} value of the discount rate
     * <br>
     * Discount rate is 10%
     */
    public static final BigDecimal DISCOUNT_RATE = BigDecimal.valueOf(0.1);

    /*
     * Calculates discount based on a product price and
* {@link DISCOUNT_RATE discount rate}
* @return a {@link java.math.BigDecimal BigDecimal}
* value of the discount
*/
    public BigDecimal getDiscount(){
        return price.multiply(DISCOUNT_RATE).setScale(2, HALF_UP);
    }



    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Rating getRating() {
        return rating;
    }

    public Product applyRating(Rating newRating){
        return new Product(id, name, price, newRating);
    }

}
