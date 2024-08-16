/*
 * Copyright (c) 2024.
 *
 * This is free software and can be used/distributed.
 */

package labs.pm.app;

import labs.pm.data.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Locale;

/**
 * {@code Shop} class represents an app that manages Products
 * @version 4.0
 * @author hatzp
 **/

public class Shop {
    public static void main(String[] args) {System.out.println("test");
        ProductManager pm = new ProductManager("en-GB");

        //Product p1 = pm.createProduct(101,"Tea", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
        //use parsing to create product with id 101 | 104 triggers DateTimeParseException
        //pm.parseProduct("D,101,Tea,1.99,0,2021-09-21");
        pm.parseProduct("D,101,Tea,1.99,0,2021-09-30");
        pm.parseProduct("F,104,Cake,3.99,0,2021-09-34");

        //pm.printProductReport(101);
        pm.parseReview("101,4,Nice cup of tea - would recommend");
        //pm.parseReview("101,x,Nice cup of tea - would recommend");
        pm.parseReview("101,5,very nice cup of tea, would recommend");
        pm.parseReview("101,3,decent");
        pm.parseReview("101,3,not a good tea");
        pm.parseReview("101,1,bad tea");
        pm.parseReview("101,0,Not worth");
        //id 1 product does not exist - exception trigger
        //pm.printProductReport(1);
        pm.sendProductReportFile(101);

        Product p2 = pm.createProduct(102,"Coffee", BigDecimal.valueOf(1.95), Rating.ONE_STAR);
        //pm.printProductReport(p2);

        pm.reviewProduct(102,Rating.ONE_STAR,"Not worth");
        //pm.printProductReport(p2);

        Product p3 = pm.createProduct(103,"Cake", BigDecimal.valueOf(3.99),Rating.FIVE_STAR, LocalDate.now().plusDays(2));
        //pm.printProductReport(p3);
        System.out.println(p3.getRating());

        pm.reviewProduct(103,Rating.FOUR_STAR,"decent");
        //id 1 product does not exist - exception trigger
        pm.reviewProduct(1,Rating.FOUR_STAR,"decent");

        System.out.println(p3.getRating());
        pm.sendProductReportFile(103);

        pm.getDiscounts().forEach(
                (rating,disc)-> System.out.println(rating+"\t"+disc)
        );


        pm.printProducts(p->p.getPrice().floatValue()<2,
                (prod1,prod2) -> (prod2.getRating().ordinal() - prod1.getRating().ordinal()));

        System.out.println("\n");


//        pm.printProducts((prod1,prod2)-> prod2.getPrice().compareTo(prod1.getPrice()));

        System.out.println("\n");

        Comparator<Product> ratingSorter = (prod1,prod2)->prod2.getRating().ordinal() - prod1.getRating().ordinal();

        Comparator<Product> priceSorter = (prod1,prod2)->prod2.getPrice().compareTo(prod1.getPrice());

//        pm.printProducts(ratingSorter.thenComparing(priceSorter));
//        System.out.println("\n");
//        pm.printProducts(ratingSorter.thenComparing(priceSorter).reversed());


    }
}
