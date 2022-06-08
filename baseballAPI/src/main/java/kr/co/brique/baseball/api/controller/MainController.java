package kr.co.brique.baseball.api.controller;

import kr.co.brique.baseball.api.model.request.GuessRequest;
import kr.co.brique.baseball.api.model.request.PlayRequest;
import kr.co.brique.baseball.api.service.MainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    private final MainService mainService;

    public MainController(MainService mainService) {
        this.mainService = mainService;
    }

    @PostMapping("/play")
    public ResponseEntity<?> playGame(@RequestBody PlayRequest play) {

        return mainService.startGame(play.getName());
    }

    @PutMapping("/guess")
    public ResponseEntity<?> guessNumber(@RequestBody GuessRequest guess) {

        return mainService.guessNum(guess.getNumber());
    }

}
