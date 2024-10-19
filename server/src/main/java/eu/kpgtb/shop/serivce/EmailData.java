package eu.kpgtb.shop.serivce;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

@Getter @Builder
public class EmailData {
    @Singular("to") private List<String> to;
    private boolean useBcc;
    private String subject;
    @Singular  private List<File> attachments;

    private String html;

    private String template;
    @Singular private Map<String,String> placeholders;

    @SneakyThrows
    public String getContent() {
        if(this.html != null) return this.html;

        String content = "";
        File file = new File(this.template);
        if(!file.exists()) return "";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while((line = reader.readLine()) != null) {
            content += line;
        }
        reader.close();

        for (Map.Entry<String, String> entry : this.placeholders.entrySet()) {
            content = content.replace(entry.getKey(), entry.getValue());
        }

        return content;
    }
}
