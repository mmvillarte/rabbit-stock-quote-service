package guru.springframework.rabbit.rabbitstockquoteservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuoteRunner implements CommandLineRunner {
    private QuoteGeneratorService quoteGeneratorService;
    private QuoteMessageSender quoteMessageSender;

    @Override
    public void run(String... args) throws Exception {
        quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(100))
                .take(25)
                .log("Got quote")
                .flatMap(quoteMessageSender::sendQuoteMessage)
                .subscribe(result -> {
                    log.debug("Sent message to rabbit");
                }, throwable -> {
                    log.error("Got error", throwable);
                }, () -> {
                    log.debug("All Done");
                });
    }
}
