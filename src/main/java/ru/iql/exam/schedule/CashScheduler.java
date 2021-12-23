package ru.iql.exam.schedule;

import org.springframework.scheduling.annotation.Scheduled;

/**
 * Планировщик задач с счетами пользователей
 */
public class CashScheduler {

    @Scheduled(fixedDelay = 20000)
    public void cashIncrementTask() {
        // по всем пользователям

    }
}
