package dev.wsousa.controller;

import dev.wsousa.response.ActorFilms;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/actor")
public class ActorController {


    private final ChatClient chatClient;
    private final ChatModel chatModel;

    @Autowired
    public ActorController(ChatClient.Builder builder, ChatModel chatModel) {
        this.chatClient = builder
                .build();
        this.chatModel = chatModel;
    }

    @GetMapping("/films-string")
    public String getActorFilmsString() {
        return chatClient.prompt()
                .user("Generate a filmography for a Anthony Hopkins for the year 2010.")
                .call()
                .content();
    }

    @GetMapping("/films")
    public ActorFilms getActorFilms() {
        return chatClient.prompt()
                .user("Generate a filmography for a Anthony Hopkins.")
                .call()
                .entity(ActorFilms.class);
    }

    @GetMapping("/films-list")
    public List<ActorFilms> listActorFilms() {
        return chatClient.prompt()
                .user("Generate a filmography for the actors Denzel Washington and Tom Hanks")
                .call()
                .entity(new ParameterizedTypeReference<>() {});
    }

    @GetMapping("/films-by-actor")
    public String getActorFilmsByName(@RequestParam String actor) {
        //return getActorFilmsByNameConverter(actor);
        return getActorFilmsByNameConverterResponse(actor);
       /* return chatClient.prompt()
                .user(u -> u.text("Generate a filmography for the actor {actor}").param("actor",actor))
                .call()
                .entity(ActorFilms.class);
                */
    }
    public String getActorFilmsByNameConverterResponse(String actor) {
    // 1. Faça a chamada e guarde a resposta intermediária
    ChatResponse response = chatClient.prompt()
            .user(u -> u.text("Generate a filmography for the actor {actor}").param("actor",actor))
            .call()
            .chatResponse();

        // 2. Extraia e imprima o conteúdo bruto da resposta
        String rawContent = response.getResult().getOutput().getContent();
        System.out.println("=========================================");
        System.out.println("RESPOSTA BRUTA DA IA:");
        System.out.println(rawContent);
        System.out.println("=========================================");


        return rawContent;
    }

    public ActorFilms getActorFilmsByNameConverter(String actor) {
        BeanOutputConverter<ActorFilms> beanOutputConverter = new BeanOutputConverter<>(ActorFilms.class);

        String format = beanOutputConverter.getFormat();

        String template = """
               Generate a filmography for the actor {actor}").param("actor",actor)
               """;

        PromptTemplate promptTemplate = new PromptTemplate(template, Map.of("actor", actor, "format", format));
        Prompt prompt = new Prompt(promptTemplate.createMessage());
        Generation generation = chatModel.call(prompt).getResult();

        return beanOutputConverter.convert(generation.getOutput().getContent());
    }
}
