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
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * {@code Shop} class represents an app that manages Products
 * @version 4.0
 * @author hatzp
 **/

public class Shop {
    public static void main(String[] args) {System.out.println("test");
        ProductManager pm = ProductManager.getInstance();

        AtomicInteger clientCount = new AtomicInteger(0);

        Callable<String> client = () -> {
            String clientId = "Client "+ clientCount.incrementAndGet();
            String threadName = Thread.currentThread().getName();
            int productId = ThreadLocalRandom.current().nextInt()+101;

            String languageTag = ProductManager.getSupportedLocales()
                    .stream()
                    .skip(ThreadLocalRandom.current().nextInt(4))
                    .findFirst().get();

            StringBuilder log = new StringBuilder();

            log.append(clientId +" "+threadName+"\n-\tstart of log\t-\n");

            log.append(pm.getDiscounts(languageTag)
                    .entrySet()
                    .stream()
                    .map(entry-> entry.getKey()+"\t"+entry.getValue())
                    .collect(Collectors.joining("\n"))
            );

            Product product = pm.reviewProduct(productId, Rating.FOUR_STAR, "Another review 1");

            log.append((product!=null)
                   ? "\nProduct "+ productId+" reviewed\n"
                   : "\nProduct "+ productId+" not reviewed\n");

            pm.sendProductReportFile(productId,languageTag,clientId);

            log.append(clientId+ " generated report for "+ productId + " product");





            log.append("\n-\tend of log\t-\n");
            return log.toString();
        };

//        pm.createProduct(103,"Cake", BigDecimal.valueOf(3.99),Rating.FIVE_STAR, LocalDate.now().plusDays(2));
//        pm.reviewProduct(103,Rating.THREE_STAR,"Decent");
//        pm.reviewProduct(103,Rating.TWO_STAR,"Decent");
//        pm.sendProductReportFile(101);
//        pm.sendProductReportFile(102);
//        pm.sendProductReportFile(103);
//
//
//        //Product p1 = pm.createProduct(101,"Tea", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
//        //use parsing to create product with id 101 | 104 triggers DateTimeParseException
//        //pm.parseProduct("D,101,Tea,1.99,0,2021-09-21");
//        pm.parseProduct("D,101,Tea,1.99,0,2021-09-30");
//        //pm.parseProduct("F,104,Cake,3.99,0,2021-09-34");
//
//        //pm.printProductReport(101);
//        pm.parseReview("101,4,Nice cup of tea - would recommend");
//        //pm.parseReview("101,x,Nice cup of tea - would recommend");
//        pm.parseReview("101,5,very nice cup of tea, would recommend");
//        pm.parseReview("101,3,decent");
//        pm.parseReview("101,3,not a good tea");
//        pm.parseReview("101,1,bad tea");
//        pm.parseReview("101,0,Not worth");
//        //id 1 product does not exist - exception trigger
//        //pm.printProductReport(1);
//        pm.sendProductReportFile(101);
//
//        Product p2 = pm.createProduct(102,"Coffee", BigDecimal.valueOf(1.95), Rating.ONE_STAR);
//        //pm.printProductReport(p2);
//
//        pm.reviewProduct(102,Rating.ONE_STAR,"Not worth");
//        //pm.printProductReport(p2);
//        pm.sendProductReportFile(102);
//
//        pm.dumpData();
//        System.out.println("\n");
//        pm.restoreData();
//
//        pm.sendProductReportFile(101);
//        pm.printProducts(p->p.getPrice().floatValue()<2,
//                (prod1,prod2) -> (prod2.getRating().ordinal() - prod1.getRating().ordinal()));

//        Product p3 = pm.createProduct(103,"Cake", BigDecimal.valueOf(3.99),Rating.FIVE_STAR, LocalDate.now().plusDays(2));
//        //pm.printProductReport(p3);
//        System.out.println(p3.getRating());
//
//        pm.reviewProduct(103,Rating.FOUR_STAR,"decent");
//        //id 1 product does not exist - exception trigger
//        pm.reviewProduct(1,Rating.FOUR_STAR,"decent");
//
//        System.out.println(p3.getRating());
//        pm.sendProductReportFile(103);
//
//        pm.getDiscounts().forEach(
//                (rating,disc)-> System.out.println(rating+"\t"+disc)
//        );
//
//
//        pm.printProducts(p->p.getPrice().floatValue()<2,
//                (prod1,prod2) -> (prod2.getRating().ordinal() - prod1.getRating().ordinal()));
//
//        System.out.println("\n");
//
//
////        pm.printProducts((prod1,prod2)-> prod2.getPrice().compareTo(prod1.getPrice()));
//
//        System.out.println("\n");
//
//        Comparator<Product> ratingSorter = (prod1,prod2)->prod2.getRating().ordinal() - prod1.getRating().ordinal();
//
//        Comparator<Product> priceSorter = (prod1,prod2)->prod2.getPrice().compareTo(prod1.getPrice());

//        pm.printProducts(ratingSorter.thenComparing(priceSorter));
//        System.out.println("\n");
//        pm.printProducts(ratingSorter.thenComparing(priceSorter).reversed());


    }
}
