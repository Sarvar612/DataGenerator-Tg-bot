package seeder;

public enum FieldType {
    ID(" "),
    UUID("\""),
    Book_Title("\""),
    Boot_Author("\""),
    Post_Title("\""),
    Post_Body("\""),
    FirstName("\""),
    LastName("\""),
    Age(" "),
    Email("\""),
    Blood_Group("\""),
    Words("\""),
    Paragraphs("\""),
    Phone("\""),
    Letters("\""),
    Random_Int("\""),
    Capital("\""),
    CountryCode("\""),
    CreditCardNumber("\"");
    private final String i;




    FieldType(String i) {
        this.i = i;
    }

    public String getJsonPairs(String fieldName,Object value) {
        return "\t\"%s\" : %s%s%s".formatted(fieldName,i,value,i);
    }
    public String getSQLPairs(Object value){
        return "%s%s%s".formatted(i,value,i);
    }


}
