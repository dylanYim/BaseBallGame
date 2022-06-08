package kr.co.brique.baseball.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.brique.baseball.api.game.Game;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class MainService {

    private static final ObjectMapper objMapper = new ObjectMapper();

    public ResponseEntity<?> startGame(String name) {
        Map<String, Object> result = new HashMap();
        if (Game.checkAlreadyStarted()) {
            result.put("message", "이미 게임 진행 중입니다.");
            return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
        }
        Game game = Game.startGame(name);
        result.put("name", game.name);
        result.put("start_time", game.startTime);
        result.put("length", game.length);
        result.put("remain_chance", game.remainChance);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public ResponseEntity<?> guessNum(String Num) {

        Map<String,Object> resEntity = new HashMap<>();
        Game game = Game.getInstance();

        if(Num.length()!= game.length){
            if (game.wrongLengthCount == 1) {
                if(game.remainChance == 1){
                    return lose(game, resEntity);
                }
                resEntity.put("message","잘못된 길이를 2회 연속 입력하셨습니다. 기회를 차감합니다.");
                game.subRemainChance();
                game.initWrongNumCount();
                return new ResponseEntity<>(resEntity, HttpStatus.BAD_REQUEST);
            }
            resEntity.put("message","잘못된 길이를 입력하셨습니다. 2회 연속 입력시 기회를 차감합니다.");
            game.addWrongNumCount();
            return new ResponseEntity<>(resEntity, HttpStatus.BAD_REQUEST);
        }

        if (game.remainChance == 0) {
            game.endGame();
            return lose(game, resEntity);
        }

        int[] clientNum = toIntNumArr(Num);
        int[] targetNum = game.randomNum;
        Map<String, Object> guessingResult = getGuessingResult(targetNum, clientNum);
        game.subRemainChance();
        if (guessingResult.get("strike") != null && guessingResult.get("strike").equals(game.length)) {
            game.endGame();
            return win(game, resEntity);
        }
        resEntity.put("number", Num);
        resEntity.put("result", guessingResult);
        resEntity.put("remain_chance", game.remainChance);

        return new ResponseEntity<>(resEntity, HttpStatus.OK);
    }

    private int[] toIntNumArr(String Num) {
        int[] res = new int[Num.length()];
        String[] targetStr = Num.split("");
        for (int i = 0; i < targetStr.length; i++) {
            res[i] = Integer.parseInt(targetStr[i]);
        }

        return res;
    }

    private ResponseEntity<?> lose(Game game, Map<String,Object> map) {
        map.put("finish_time", LocalDateTime.now());
        map.put("used_chance", 5 - game.remainChance);
        map.put("answer", game.randomNum);
        map.put("result", "패배");
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    private ResponseEntity<?> win(Game game, Map<String,Object> map) {
        map.put("finish_time", LocalDateTime.now());
        map.put("used_chance", 5 - game.remainChance);
        map.put("answer", game.randomNum);
        map.put("result", "승리");
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    private Map<String, Object> getGuessingResult(int[] target, int[] client) {
        Map<String, Object> res = new HashMap<>();
        boolean isOut = false;
        int strike = 0;
        int ball = 0;

        for (int i = 0; i < client.length; i++) {
            if (exists(target, client[i])) {
                if (target[i] == client[i]) {
                    strike ++;
                }else {
                    ball ++;
                }
            }
        }
        if (strike == 0 && ball == 0) {
            isOut = true;
        }
        res.put("out", isOut);
        res.put("strike", strike);
        res.put("ball", ball);
        return res;
    }

    private static boolean exists(int n[], int index) {
        for (int i = 0; i < n.length; i++) {
            if(n[i] == index)
                return true;
        }
        return false;
    }

}
