package asia.igsaas.rest;

import asia.igsaas.data.BotMessageData;
import asia.igsaas.service.BotMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final BotMessageService service;

    @PostMapping
    ResponseEntity<Object> storeMessage(@RequestBody BotMessageData data) {
        log.info("storing message: {}", data);
        var result = service.save(data);
        return ResponseEntity.ok(result);
    }
}
