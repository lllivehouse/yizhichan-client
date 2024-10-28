package tech.yizhichan.sdk.test.hotfix;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @description: TestService
 * @author: lex
 * @date: 2024-08-16
 **/
@Service
public class TestService {

    public ResponseEntity<String> print(String msg) {
        System.out.println("hello world");
        return ResponseEntity.ok(msg);
    }
}
