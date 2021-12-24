package ru.iql.exam.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.iql.exam.dao.UserRepository;


/**
 * Планировщик задач с счетами пользователей
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CashScheduler {

    private final UserRepository userRepository;

    @Value("${scheduler_task.autoincrement.increment_coef}")
    private final double INCREMENT_COEF = 0.1;

    @Value("${scheduler_task.autoincrement.limit_coef}")
    private final double LIMIT_COEF = 1.07;

    @Scheduled(fixedDelay = 5000)  //todo 20000
    public void cashIncrementTask() {
        logInfo("Запущена задача Автоинкремент счетов");
        userRepository.findAllByProfileAutoIncrementedIsTrueAndProfileStartCashGreaterThan(0).stream()
                .forEach(u -> {
                    int increment = (int) (INCREMENT_COEF * u.getProfile().getCash());
                    if (increment <= (int) (LIMIT_COEF * u.getProfile().getStartCash())) {
                        u.getProfile().setCash(u.getProfile().getCash() + increment);
                        logInfo("Счет пользователя ID " + u.getId() + " увеличен на " + increment + "коп.");
                    } else {
                        u.getProfile().setAutoIncremented(false);
                        logInfo("Для пользователя ID " + u.getId() + " установлен запрет на автоинкремент счета");
                    }
                    userRepository.save(u);
                });
        logInfo("Закончена задача Автоинкремент счетов");
    }

    private void logInfo(String msg) {
        log.info("Планировщик: {}", msg);
    }
}
