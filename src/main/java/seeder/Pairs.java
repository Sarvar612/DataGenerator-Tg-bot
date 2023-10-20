package seeder;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Pairs {
    private String fieldName="";
    private FieldType fieldType;

    public Pairs(FieldType fieldType){
        this.fieldType=fieldType;
    }
    public Pairs(FieldType fieldType,String fieldName){
        this.fieldType=fieldType;
        this.fieldName=fieldName;
    }
    public Pairs(String fieldName){
        this.fieldName=fieldName;
    }
}
