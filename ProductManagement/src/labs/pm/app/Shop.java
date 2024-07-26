/*
 * Copyright (c) 2024.
 *
 * This is free software and can be used/distributed.
 */

package labs.pm.app;

import labs.pm.data.Drink;
import labs.pm.data.Food;
import labs.pm.data.Product;
import labs.pm.data.Rating;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * {@code Shop} class represents an app that manages Products
 * @version 4.0
 * @author hatzp
 **/

public class Shop {
    public static void main(String[] args) {System.out.println("test");
        Product p1 = new Drink(101,"Tea", BigDecimal.valueOf(1.99), Rating.THREE_STAR);
        Product p2 = new Drink(102,"Coffee", BigDecimal.valueOf(1.99), Rating.FOUR_STAR);
        Product p3 = new Food(103,"Cake", BigDecimal.valueOf(3.99),Rating.FIVE_STAR, LocalDate.now().plusDays(2));
        Product p4 = new Food(105,"Cookie", BigDecimal.valueOf(3.49),Rating.FIVE_STAR, LocalDate.now().plusDays(0));
        Product p5 = p3.applyRating(Rating.THREE_STAR);

        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);
        System.out.println(p4);
        System.out.println(p5);

        Product p6 = new Drink(104, "Chocolate", BigDecimal.valueOf(2.99), Rating.FIVE_STAR);
        Product p7 = new Food(104, "Chocolate", BigDecimal.valueOf(2.99), Rating.FIVE_STAR, LocalDate.now().plusDays(2));

        System.out.println(p6.equals(p7));

        Product p8 = p4.applyRating(Rating.FOUR_STAR);
        Product p9 = p1.applyRating(Rating.FOUR_STAR);

        System.out.println(p8);
        System.out.println(p9);

        if (p3 instanceof Food)
        {
            System.out.println( "\n" + ((Food)p3).getBestBefore());
        }

        System.out.println(p3.getBestBefore());
        System.out.println(p1.getBestBefore());







    }
}
