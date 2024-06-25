package com.example.springaipresentation.controller;

import com.example.springaipresentation.model.Author;
import lombok.Builder;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.parser.ListOutputParser;
import org.springframework.ai.parser.MapOutputParser;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Builder
public class OutputParserController {
    private final ChatClient chatClient;

    @GetMapping("/songs")
    public List<String> getSongsByCountry(@RequestParam(value = "country") String country) {
        String message = "Please give me an actual list of 5 popular artists from the {country}." +
                " If you don't know the answer say I don't know" +
                "{format}";

        ListOutputParser outputParser = new ListOutputParser(new DefaultConversionService());

        PromptTemplate promptTemplate =
                new PromptTemplate(message, Map.of("country", country, "format", outputParser.getFormat()));
        Prompt prompt = promptTemplate.create();
        ChatResponse response = chatClient.call(prompt);
        return outputParser.parse(response.getResult().getOutput().getContent());
    }


    @GetMapping("/author")
    public Map<String, Object> byAuthor(@RequestParam(value = "author") String author) {
        String promptMessage = "Generate a list of books for the {author}." +
                "Include the authors name as the key and any book name as " +
                "{format}";
        MapOutputParser outputParser = new MapOutputParser();
        String format = outputParser.getFormat();
        PromptTemplate promptTemplate = new PromptTemplate(promptMessage, Map.of("author", author, "format", format));
        Prompt prompt = promptTemplate.create();
        Generation generation = chatClient.call(prompt).getResult();
        return outputParser.parse(generation.getOutput().getContent());
    }


//    @GetMapping("/by-author")
//    public Author getBooksByAuthor(@RequestParam(value = "author") String author) {
//        String promptMessage = "Generate a list of books written by the author {author}." +
//                "If you aren't sure that a book belongs to this author, then don't include it. {format}";
//
//        var outputParser = new BeanOutputParser<>(Author.class);
//        String format = outputParser.getFormat();
//        PromptTemplate promptTemplate = new PromptTemplate(promptMessage, Map.of("author", author, "format", format));
//        Prompt prompt = promptTemplate.create();
//        return outputParser.parse(chatClient.call(prompt).getResult().getOutput().getContent());
//    }
}
