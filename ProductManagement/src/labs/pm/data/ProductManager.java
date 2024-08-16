package labs.pm.data;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author hatzp
 **/
public class ProductManager {





    private Review[] reviews = new Review[5];

    private Map<Product,List<Review>> products = new HashMap<>();

    private ResourceBundle config = ResourceBundle.getBundle("labs.pm.data.config");

    private MessageFormat reviewFormat = new MessageFormat(config.getString("review.data.format"));
    private MessageFormat productFormat = new MessageFormat(config.getString("product.data.format"));
    private ResourceFormatter formatter;

    private final Path reportsFolder = Path.of(config.getString("reports.folder"));
    private final Path dataFolder = Path.of(config.getString("data.folder"));
    private final Path tempFolder = Path.of(config.getString("temp.folder"));
    private static Map<String,ResourceFormatter> formatters =
            Map.of("en-GB", new ResourceFormatter (Locale. UK) ,
                    "en-US", new ResourceFormatter (Locale.US),
                    "es-US", new ResourceFormatter (new Locale ("es", "US") ),
                    "fr-FR", new ResourceFormatter(Locale.FRANCE),
                    "zh-CN", new ResourceFormatter(Locale.CHINA)) ;

    private static final Logger logger = Logger.getLogger(ProductManager.class.getName());

    public ProductManager(Locale locale) {
        this(locale.toLanguageTag());
    }

    public ProductManager(String languageTag){
        changeLocale(languageTag);
    }

    public void changeLocale(String languageTag){
        formatter = formatters.getOrDefault(languageTag,formatters.get("en-GB"));
    }

    public static Set<String> getSupportedLocales(){
        return formatters.keySet();
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore){
        Product product = new Food(id, name, price, rating, bestBefore);
        products.putIfAbsent(product,new ArrayList<>());
        return product;
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating){
        Product product = new Drink(id, name, price, rating);
        products.putIfAbsent(product,new ArrayList<>());
        return product;
    }

    public Product reviewProduct(Product product, Rating rating, String comments){
        List<Review> reviews = products.get(product);
        products.remove(product,reviews);
        reviews.add(new Review(rating, comments));

        product = product.applyRating(Rateable.convert((int) Math.round(
                reviews.stream()
                        .mapToInt(r -> r.getRating().ordinal())
                        .average()
                        .orElse(0)
        )));

        products.put(product,reviews);
        return product;
    }

    public Product reviewProduct (int id, Rating rating, String comments){
        try {
            return reviewProduct(findProduct(id),rating,comments);
        } catch (ProductManagerException e) {
            logger.log(Level.INFO,e.getMessage());
            return null;
        }
    }

    public void printProductReport(Product product){
        //formatting and printing logic
        StringBuilder txt = new StringBuilder();
        List<Review> reviews = products.get(product);
        Collections.sort(reviews);
        txt.append(formatter.formatProduct(product));
        txt.append('\n');

        if(reviews.isEmpty()){
            txt.append(formatter.getText("no.reviews"));
        }else{
            txt.append(
                    reviews.stream()
                            .map(r->formatter.formatReview(r)+"\n")
                            .collect(Collectors.joining()));
        }

        System.out.println(txt);
    }
    public void printProductReport(int id){
        try {
            printProductReport(findProduct(id));
        } catch (ProductManagerException e) {
            logger.log(Level.INFO,e.getMessage());
        }
    }
    public void sendProductReportFile(Product product) throws IOException {
        //formatting and printing logic

        Path productFile = reportsFolder.resolve(MessageFormat.format(config.getString("report.file"),product.getId()));

        try(PrintWriter out = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(productFile, StandardOpenOption.CREATE),"UTF-8"))

                )
        {
            List<Review> reviews = products.get(product);
            Collections.sort(reviews);
            out.append(formatter.formatProduct(product) + System.lineSeparator());

            if(reviews.isEmpty()){
                out.append(formatter.getText("no.reviews") + System.lineSeparator());
            }else{
                out.append(
                        reviews.stream()
                                .map(r->formatter.formatReview(r)+ System.lineSeparator())
                                .collect(Collectors.joining()));
            }
            System.out.println(out);
        }
    }

    public void sendProductReportFile(int id){
        try {
            sendProductReportFile(findProduct(id));
        } catch (ProductManagerException e) {
            logger.log(Level.INFO,e.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE,"Error printing product report " +e.getMessage(), e);
        }
    }

    public void printProducts(Predicate<Product> filter, Comparator<Product> sorter){

        StringBuilder txt = new StringBuilder();

        products.keySet()
                .stream()
                .sorted(sorter)
                .filter(filter)
                .forEach(p -> txt.append(formatter.formatProduct(p)+"\n"));
        System.out.println(txt);
    }

    public void parseReview(String text){
        try {
            Object[] values = reviewFormat.parse(text);
            reviewProduct(Integer.parseInt((String) values[0]),
                        Rateable.convert(Integer.parseInt((String) values[1])), (String) values[2]
            );

        } catch (ParseException | NumberFormatException e) {
            logger.log(Level.WARNING, " Error parsing review "+ text + e.getMessage());
        }
    }

    public void parseProduct(String text){
        try {
            Object [] values = productFormat.parse(text);

            int id = Integer.parseInt((String) values[1]);
            String name = (String) values[2];
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble((String) values[3]));
            Rating rating = Rateable.convert(Integer.parseInt((String) values[4]));

            switch ((String) values[0]){
                case "D":
                    //drink creation
                    createProduct(id, name, price, rating);
                    break;
                case "F":
                    //food creation
                    LocalDate bestBefore = LocalDate.parse((String) values[5]);
                    createProduct(id, name, price, rating,bestBefore);

            }

        } catch (ParseException | NumberFormatException | DateTimeParseException e) {
            logger.log(Level.WARNING, " Error parsing product "+ text + e.getMessage());
        }
    }

    public Product findProduct(int id) throws ProductManagerException{
        return products.keySet()
                .stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ProductManagerException("Product with id "+ id + " was not found"));
    }

    public Map<String,String> getDiscounts(){
        return  products.keySet()
                .stream()
                .collect(
                        Collectors.groupingBy(prod -> prod.getRating().getStars(),
                                Collectors.collectingAndThen(Collectors.summingDouble(
                                        prod-> prod.getDiscount().doubleValue()),
                                        disc-> formatter.moneyFormat.format(disc)
                                ))
                );
    }





    private static class ResourceFormatter{
        private Locale locale;
        private ResourceBundle resources;
        private DateTimeFormatter dateFormat;
        private NumberFormat moneyFormat;

        private ResourceFormatter(Locale locale){
            this.locale = locale;
            resources = ResourceBundle.getBundle("labs.pm.data.resources",locale);
            dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
            moneyFormat = NumberFormat.getCurrencyInstance(locale);
        }

        private String formatProduct(Product product){
            return MessageFormat.format(
                    resources.getString("product"),
                    product.getName(),
                    moneyFormat.format(product.getPrice()),
                    product.getRating().getStars(),
                    dateFormat.format(product.getBestBefore())
            );
        }

        private String formatReview(Review review){
            return MessageFormat.format(
                    resources.getString("review"),
                    review.getRating().getStars(),
                    review.getComments()
            );
        }

        private String getText(String key){
            return resources.getString(key);
        }


    }



}
