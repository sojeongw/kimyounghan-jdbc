package hello.kimyounghanjdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {

    @Test
    void unchecked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unchecked_throws() {
        Service service = new Service();
        Assertions.assertThatThrownBy(service::callThrow)
                .isInstanceOf(MyUncheckedException.class);
    }

    // 언체크 예외는 RuntimeException을 상속받는다.
    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String message) {
            super(message);
        }
    }

    // 언체크 예외는 잡거나 던지지 않아도 된다. 잡지 않으면 자동으로 밖으로 던진다.
    static class Service {
        Repository repository = new Repository();

        public void callCatch() {
            try {
                repository.call();
            } catch (MyUncheckedException e) {
                log.info("예외 처리. message = {}", e.getMessage(), e);
            }
        }

        // 예외를 잡지 않아도 자연스럽게 상위로 넘어간다.
        // 체크 예외와 달리 메서드에 선언하지 않는다.
        public void callThrow() {
            repository.call();
        }
    }

    static class Repository {
        public void call() {
            throw new MyUncheckedException("ex");
        }
    }
}
