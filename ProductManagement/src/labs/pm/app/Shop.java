/*
 * Copyright (c) 2024.
 *
 * This is free software and can be used/distributed.
 */

package labs.pm.app;

import labs.pm.data.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

/**
 * {@code Shop} class represents an app that manages Products
 * @version 4.0
 * @author hatzp
 **/

public class Shop {
    public static void main(String[] args) {System.out.println("test");
        ProductManager pm = new ProductManager(Locale.UK);
        Product p1 = pm.createProduct(101,"Tea", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
        pm.printProductReport(p1);
        p1 = pm.reviewProduct(p1,Rating.FOUR_STAR,"Nice cup of tea, would recommend");
        p1 = pm.reviewProduct(p1,Rating.FIVE_STAR,"very nice cup of tea, would recommend");
        p1 = pm.reviewProduct(p1,Rating.THREE_STAR,"decent");
        p1 = pm.reviewProduct(p1,Rating.TWO_STAR,"not a good tea");
        p1 = pm.reviewProduct(p1,Rating.ONE_STAR,"bad tea");
        p1 = pm.reviewProduct(p1,Rating.NOT_RATED,"Not worth");
        pm.printProductReport(p1);

        Product p2 = pm.createProduct(102,"Coffee", BigDecimal.valueOf(1.95), Rating.NOT_RATED);
        pm.printProductReport(p2);
        p2 = pm.reviewProduct(p2,Rating.FOUR_STAR,"Nice cup of tea, would recommend");
        p2 = pm.reviewProduct(p2,Rating.FIVE_STAR,"very nice cup of tea, would recommend");
        p2 = pm.reviewProduct(p2,Rating.THREE_STAR,"decent");
        p2 = pm.reviewProduct(p2,Rating.TWO_STAR,"not a good tea");
        p2 = pm.reviewProduct(p2,Rating.ONE_STAR,"bad tea");
        p2 = pm.reviewProduct(p2,Rating.NOT_RATED,"Not worth");
        pm.printProductReport(p2);

        Product p3 = pm.createProduct(103,"Cake", BigDecimal.valueOf(3.99),Rating.FIVE_STAR, LocalDate.now().plusDays(2));
        pm.printProductReport(p3);
        p3 = pm.reviewProduct(p3,Rating.FOUR_STAR,"Nice cup of tea, would recommend");
        p3 = pm.reviewProduct(p3,Rating.FIVE_STAR,"very nice cup of tea, would recommend");
        p3 = pm.reviewProduct(p3,Rating.THREE_STAR,"decent");
        p3 = pm.reviewProduct(p3,Rating.TWO_STAR,"not a good tea");
        p3 = pm.reviewProduct(p3,Rating.ONE_STAR,"bad tea");
        p3 = pm.reviewProduct(p3,Rating.NOT_RATED,"Not worth");
        pm.printProductReport(p3);


    }
}
