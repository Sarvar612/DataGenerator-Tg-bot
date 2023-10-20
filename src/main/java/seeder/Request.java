package seeder;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class Request {
    private String fileName;
    private Integer count;
    private String type;
    private List<Pairs> pairs;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCount() {
        return count;
    }

    public void setPairs(List<Pairs> pairs) {
        this.pairs = pairs;
    }

    public List<Pairs> getPairs() {
        return pairs;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
