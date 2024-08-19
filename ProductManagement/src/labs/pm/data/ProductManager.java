package labs.pm.data;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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

    private final ResourceBundle config = ResourceBundle.getBundle("labs.pm.data.config");

    private final MessageFormat reviewFormat = new MessageFormat(config.getString("review.data.format"));
    private final MessageFormat productFormat = new MessageFormat(config.getString("product.data.format"));
    //private ResourceFormatter formatter;

    private final Path reportsFolder = Path.of(config.getString("reports.folder"));
    private final Path dataFolder = Path.of(config.getString("data.folder"));
    private final Path tempFolder = Path.of(config.getString("temp.folder"));
    private final static Map<String,ResourceFormatter> formatters =
            Map.of("en-GB", new ResourceFormatter (Locale. UK) ,
                    "en-US", new ResourceFormatter (Locale.US),
                    "es-US", new ResourceFormatter (new Locale ("es", "US") ),
                    "fr-FR", new ResourceFormatter(Locale.FRANCE),
                    "zh-CN", new ResourceFormatter(Locale.CHINA)) ;

    private static final Logger logger = Logger.getLogger(ProductManager.class.getName());

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();


    private ProductManager(){
        loadAllData();
    }

    private static final ProductManager pm = new ProductManager();

    public static ProductManager getInstance(){
        return pm;
    }


    public static Set<String> getSupportedLocales(){
        return formatters.keySet();
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore){
        Product product = null;
        try{
            writeLock.lock();
            product = new Food(id, name, price, rating, bestBefore);
            products.putIfAbsent(product,new ArrayList<>());
        }catch(Exception e){
            logger.log(Level.INFO,"Error adding product "+ e.getMessage());
            return null;
        }finally {
            writeLock.unlock();
        }
        return product;
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating){
        Product product = null;
        try{
            writeLock.lock();
            product = new Drink(id, name, price, rating);
            products.putIfAbsent(product,new ArrayList<>());
        }catch(Exception e){
            logger.log(Level.INFO,"Error adding product "+ e.getMessage());
            return null;
        }finally {
            writeLock.unlock();
        }
        return product;
    }

    private Product reviewProduct(Product product, Rating rating, String comments){
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

    public Product reviewProduct(int id, Rating rating, String comments){
        try {
            writeLock.lock();
            return reviewProduct(findProduct(id),rating,comments);
        } catch (ProductManagerException e) {
            logger.log(Level.INFO,e.getMessage());
            return null;
        }finally {
            writeLock.unlock();
        }
    }

//    public void printProductReport(Product product, String languageTag){
//        //formatting and printing logic
//        ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
//        StringBuilder txt = new StringBuilder();
//        List<Review> reviews = products.get(product);
//        Collections.sort(reviews);
//        txt.append(formatter.formatProduct(product));
//        txt.append('\n');
//
//        if(reviews.isEmpty()){
//            txt.append(formatter.getText("no.reviews"));
//        }else{
//            txt.append(
//                    reviews.stream()
//                            .map(r->formatter.formatReview(r)+"\n")
//                            .collect(Collectors.joining()));
//        }
//
//        System.out.println(txt);
//    }
//    public void printProductReport(int id, String languageTag){
//        try {
//            printProductReport(findProduct(id),languageTag);
//        } catch (ProductManagerException e) {
//            logger.log(Level.INFO,e.getMessage());
//        }
//    }
    private void sendProductReportFile(Product product, String languageTag, String client) throws IOException {
        //formatting and printing logic
        ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));

        Path productFile = reportsFolder.resolve(MessageFormat.format(
                config.getString("report.file"),product.getId(),client
                ));

        try(PrintWriter out = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(productFile, StandardOpenOption.CREATE),"UTF-8"))

                )
        {   writeLock.lock();
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
        }finally {
            writeLock.unlock();
        }
    }

    public void sendProductReportFile(int id, String languageTag, String client){
        try {
            writeLock.lock();
            sendProductReportFile(findProduct(id),languageTag, client);
        } catch (ProductManagerException e) {
            logger.log(Level.INFO,e.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE,"Error printing product report " +e.getMessage(), e);
        }finally {
            writeLock.unlock();
        }
    }

    public void printProducts(Predicate<Product> filter, Comparator<Product> sorter, String languageTag){

        try {
            readLock.lock();
            ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
            StringBuilder txt = new StringBuilder();

            products.keySet()
                    .stream()
                    .sorted(sorter)
                    .filter(filter)
                    .forEach(p -> txt.append(formatter.formatProduct(p) + "\n"));
            System.out.println(txt);
        }finally {
            readLock.unlock();
        }
    }

//    public void parseReview(String text){
//        try {
//            Object[] values = reviewFormat.parse(text);
//            reviewProduct(Integer.parseInt((String) values[0]),
//                        Rateable.convert(Integer.parseInt((String) values[1])), (String) values[2]
//            );
//
//        } catch (ParseException | NumberFormatException e) {
//            logger.log(Level.WARNING, " Error parsing review "+ text + e.getMessage());
//        }
//    }

    private Review parseReviewFile(String text){
        Review review = null;

        try {
            Object[] values = reviewFormat.parse(text);
            review = new Review(Rateable.convert(Integer.parseInt((String) values[0])),(String) values[1]);

        } catch (ParseException | NumberFormatException e) {
            logger.log(Level.WARNING, " Error parsing review "+ text + e.getMessage());
        }
    return review;

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

    private Product parseProductFile(String text){
        Product product = null;
        try {
            Object [] values = productFormat.parse(text);

            int id = Integer.parseInt((String) values[1]);
            String name = (String) values[2];
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble((String) values[3]));
            Rating rating = Rateable.convert(Integer.parseInt((String) values[4]));

            product = switch ((String) values[0]) {
                case "D" ->
                    //drink creation
                        new Drink(id, name, price, rating);
                case "F" -> {
                    //food creation
                    LocalDate bestBefore = LocalDate.parse((String) values[5]);
                    yield new Food(id, name, price, rating, bestBefore);
                }
                default -> product;
            };

//            switch ((String) values[0]){
//                case "D":
//                    //drink creation
//                    product = new Drink(id, name, price, rating);
//                    break;
//                case "F":
//                    //food creation
//                    LocalDate bestBefore = LocalDate.parse((String) values[5]);
//                    product = new Food(id, name, price, rating,bestBefore);
//
//            }

        } catch (ParseException | NumberFormatException | DateTimeParseException e) {
            logger.log(Level.WARNING, " Error parsing product "+ text + e.getMessage());
        }
        return product;
    }

    public Product findProduct(int id) throws ProductManagerException{
        try{
            readLock.lock();
            return products.keySet()
                    .stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new ProductManagerException("Product with id "+ id + " was not found"));
        }finally {
            readLock.unlock();
        }

    }

    public Map<String,String> getDiscounts(String languageTag){
       try {
            readLock.lock();
           ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));
           return products.keySet()
                   .stream()
                   .collect(
                           Collectors.groupingBy(prod -> prod.getRating().getStars(),
                                   Collectors.collectingAndThen(Collectors.summingDouble(
                                                   prod -> prod.getDiscount().doubleValue()),
                                           disc -> formatter.moneyFormat.format(disc)
                                   ))
                   );
       }finally {
           readLock.unlock();
       }
    }

    private List<Review> loadReviews(Product product){
        List<Review> reviews = null;
        Path file = dataFolder.resolve(
                MessageFormat.format(config.getString("reviews.data.file"),product.getId())
        );

       if(Files.notExists(file)){
           reviews = new ArrayList<>();
       }else{
           try {
               reviews = Files.lines(file, StandardCharsets.UTF_8)
                       .map(text -> parseReviewFile(text))
                       .filter(review -> review != null)
                       .collect(Collectors.toList());
           } catch (IOException e) {
               logger.log(Level.WARNING, "Error loading review " + e.getMessage());
           }
       }
        return reviews;
    }

    private Product loadProduct (Path file){
        Product product = null;

        try {
            product = parseProductFile(Files.lines(
                    dataFolder.resolve(file),StandardCharsets.UTF_8).findFirst().orElseThrow()
            );
        } catch (IOException e) {
            logger.log(Level.WARNING,"Error loading product "+ e.getMessage());
        };

        return product;
    }

    private void loadAllData(){
        try {
            products = Files.list(dataFolder)
                    .filter(file -> file.getFileName().toString().startsWith("product"))
                    .map(file ->loadProduct(file))
                    .filter(product -> product != null)
                    .collect(Collectors.toConcurrentMap(product -> product,product -> loadReviews(product)));
        } catch (IOException e) {
            logger.log(Level.SEVERE,"Error loading data "+e.getMessage(),e);
        }
    }

    public void dumpData(){
        try{
            if (Files.notExists(tempFolder)){
                Files.createDirectory(tempFolder);
            }
            Path tempFile = tempFolder.resolve(MessageFormat.format(config.getString("temp.file"), (LocalDateTime.now().toString()).replace(":","-")));

            try(ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(tempFile,StandardOpenOption.CREATE))){
                out.writeObject(products);
                products = new HashMap<>();
            }

        } catch (IOException ex){
            logger.log(Level.SEVERE, "Error dumping data "+ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    public void restoreData(){
        try{
            Path tempFile = Files.list(tempFolder)
                    .filter(path -> path.getFileName().toString().endsWith("tmp"))
                    .findFirst()
                    .orElseThrow();

            try(ObjectInputStream in = new ObjectInputStream(Files.newInputStream(tempFile,StandardOpenOption.DELETE_ON_CLOSE))){
                products = (ConcurrentHashMap)in.readObject();
            }

        }catch (Exception ex){
            logger.log(Level.SEVERE, "Error restoring data "+ ex.getMessage(), ex);
        }
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
