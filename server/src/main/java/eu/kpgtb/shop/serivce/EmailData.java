package eu.kpgtb.shop.serivce;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

@Getter
public class EmailData {
    private final List<String> to;
    private final String subject;

    private final String html;

    private final String template;
    private final Map<String,String> placeholders;

    private final List<File> attachments;

    public EmailData(String to, String subject, String html) {
        this(to,subject,html,new ArrayList<>());
    }
    public EmailData(String to, String subject, String html,List<File> attachments) {
        this(Arrays.asList(to),subject,html,attachments);
    }
    public EmailData(List<String> to, String subject, String html) {
        this(to,subject,html,new ArrayList<>());
    }
    public EmailData(List<String> to, String subject, String html, List<File> attachments) {
        this.to = to;
        this.subject = subject;
        this.html = html;
        this.attachments = attachments;

        this.template = null;
        this.placeholders = new HashMap<>();
    }


    public EmailData(String to, String subject, String template, Map<String,String> placeholders) {
        this(to,subject,template,placeholders,new ArrayList<>());
    }
    public EmailData(String to, String subject, String template, Map<String,String> placeholders,List<File> attachments) {
        this(Arrays.asList(to),subject,template,placeholders,attachments);
    }
    public EmailData(List<String> to, String subject, String template, Map<String,String> placeholders) {
        this(to,subject,template,placeholders,new ArrayList<>());
    }
    public EmailData(List<String> to, String subject, String template, Map<String,String> placeholders,List<File> attachments) {
        this.to = to;
        this.subject = subject;
        this.template = template;
        this.placeholders = placeholders;
        this.attachments = attachments;

        this.html = null;
    }

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
