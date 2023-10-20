package seeder;

import com.github.javafaker.*;
import com.github.javafaker.service.RandomService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.Data;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
@Getter
@Data
public class AppForGeneratingDatas
{
    private static TelegramBot telegramBot=new TelegramBot("6087188203:AAHGhwrHxxhk5NF_xgo1tD4022sz6vpPiZw");
    private static  String fileName;
    private static String type;
    private static int count;
    private static List<Pairs> pairs;
    private static Path path;
    private final Path importPath=path;
    private static Request requestA;
    static final Map<FieldType,Supplier<Object>> functions=new HashMap<>();
    static {
        Faker faker=new Faker();
        Name name = faker.name();
        Book book = faker.book();
        Country country = faker.country();
        Internet internet = faker.internet();
        PhoneNumber phoneNumber = faker.phoneNumber();
        RandomService random=faker.random();
        functions.put(FieldType.CreditCardNumber,()->faker.business().creditCardNumber());
        functions.put(FieldType.ID, random::nextLong);
        functions.put(FieldType.UUID, UUID::randomUUID);
        functions.put(FieldType.Blood_Group, name::bloodGroup);
        functions.put(FieldType.Age, ()->random.nextInt(0,100));
        functions.put(FieldType.Book_Title, book::title);
        functions.put(FieldType.Boot_Author,book::author);
        functions.put(FieldType.Post_Body, name::username);
        functions.put(FieldType.Phone, phoneNumber::cellPhone);
        functions.put(FieldType.FirstName,name::firstName);
        functions.put(FieldType.LastName, name::lastName);
        functions.put(FieldType.Words, faker::lorem);
        functions.put(FieldType.Letters, faker::lorem);
        functions.put(FieldType.Email,internet::emailAddress);
        functions.put(FieldType.Paragraphs, book::title);
        functions.put(FieldType.Capital,country::capital );
        functions.put(FieldType.CountryCode, country::countryCode2);
        functions.put(FieldType.Post_Title,book::title);

    }
        public String proccessRequest(Request requestA,Long chatId) throws IOException {
        fileName=requestA.getFileName();
        type=requestA.getType();
        count=requestA.getCount();
        pairs=requestA.getPairs();
        Request request = new Request(fileName+"."+type,count,type,pairs);
        switch (type) {
            case "json" -> generateRandomDataAsJson(request);
            case "csv" -> generateRandomDataAsCSV(request);
            case "sql" -> generateRandomDataAsSQL(request);
            default -> otherCase(request,chatId);
        }
        return path.toString();
    }


    private static void otherCase(Request request,Long chatId) throws IOException{
        SendMessage sendMessage = new SendMessage(chatId, "Try Again /start");
        telegramBot.execute(sendMessage);
    }
    private static void generateRandomDataAsCSV(Request request) throws IOException {
        List<Pairs> pairs = request.getPairs();
        StringBuilder div = new StringBuilder();
        String firstLine = "";

        for (int i = 0; i < pairs.size(); i++)
            firstLine += pairs.get(i).getFieldName()+((i!=pairs.size()-1)?",":"");

        for (int i = 0; i < request.getCount(); i++) {
            StringBuilder horizontal = new StringBuilder();
            for (Pairs pair : pairs) {
                FieldType fieldType = pair.getFieldType();
                Object value = AppForGeneratingDatas.functions.get(fieldType).get();
                String result = value.toString();
                if (result.contains(",")) {
                    result = "\"" + result + "\"";
                }
                horizontal.append(result).append(",");
            }
            div.append(horizontal.substring(0, horizontal.length() - 1)).append("\n");
        }
        String res = firstLine+"\n"+div;
        path= Files.writeString(Path.of(request.getFileName()), res);

    }

    private static void generateRandomDataAsJson(Request request) throws IOException {
        List<Pairs> pairs = request.getPairs();
        StringJoiner stringJoiner = new StringJoiner(", ","[\n","\n]");
        for (int i = 0; i < request.getCount(); i++) {
            StringJoiner stringJoiner1 = new StringJoiner(",\n","\n{\n","\n}");
            for(Pairs pair: pairs){
                FieldType fieldType = pair.getFieldType();
                stringJoiner1.add(fieldType.getJsonPairs(String.valueOf(fieldType), AppForGeneratingDatas.functions.get(fieldType).get()));
            }
            stringJoiner.add(stringJoiner1.toString());
        }
        Files.writeString(Path.of(request.getFileName()),stringJoiner.toString());
        path=Files.writeString(Path.of(request.getFileName()),stringJoiner.toString());
    }
    private static void generateRandomDataAsSQL(Request request) throws IOException{
        List<Pairs> pairs = request.getPairs();
        StringJoiner html = new StringJoiner("");
        String tableName ="INSERT INTO "+request.getFileName().replace(".","_")+" ";


        StringJoiner headHtml = new StringJoiner("");
        for (int i = 0; i < request.getCount(); i++) {
            StringJoiner keys = new StringJoiner(",","(",")");
            StringJoiner values = new StringJoiner(",","(",")");
            for (Pairs p : pairs) {
                FieldType fieldType = p.getFieldType();
                keys.add(p.getFieldName());
                values.add(fieldType.getSQLPairs(AppForGeneratingDatas.functions.get(fieldType).get()));
            }
            html.add(tableName);
            html.add(keys.toString());
            html.add(" VALUES "+values+";\n");
        }
        System.out.println(headHtml);
        Files.writeString(Path.of(request.getFileName()),headHtml.toString());
        path=Files.writeString(Path.of(request.getFileName()),headHtml.toString());
    }
}
