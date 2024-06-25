package com.example.springaipresentation.controller;


import lombok.Builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Builder
public class PromptController {

    private static final Logger logger = LoggerFactory.getLogger(PromptController.class);
    private final ChatClient chatClient;

    @GetMapping("/getSimpleMessage")
    public String generate(@RequestParam(value = "message") String message) {
        return chatClient.call(message);
    }

    @GetMapping("/singers")
    public String findPopularSingersByCountry(@RequestParam(value = "country") String country) {
        String message = "List 10 of the most popular singers by {country} If you don't know the answer just say I don't know";
        PromptTemplate promptTemplate = new PromptTemplate(message);
        Prompt prompt = promptTemplate.create(Map.of("country", country));
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }

    @GetMapping("/it")
    public String itGuru() {
        var system = new SystemMessage("Your primary function is to answer IT questions." +
                "If someone ask you for other information just tell them to ask someone else");
        var user = new UserMessage("What is the mass of the sun?");
        Prompt prompt = new Prompt(List.of(system, user));
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }

    @GetMapping("/stuffing")
    public String getCompanyRules() {
        long startTime = System.currentTimeMillis();

        var user = new UserMessage("How can I use the printer?");
        var system = new SystemMessage("You work for a strange company and these rules have to be applied: " +
                "You can only use the photocopier if you're wearing mismatched socks.\n" +
                "Meetings can only be held in the conference room if everyone enters walking backwards.\n" +
                "Coffee breaks are allowed only if you recite a line from Shakespeare.\n" +
                "You must sing \"Happy Birthday\" to the printer before using it.\n" +
                "Only employees wearing hats can send emails after 2 PM.");

        Prompt prompt = new Prompt(List.of(system, user));
        String result = chatClient.call(prompt).getResult().getOutput().getContent();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        logger.info("Request to /stuffing took " + duration + " ms");

        return result;
    }
}
