package ru.iql.exam.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.iql.exam.dao.UserPhoneRepository;
import ru.iql.exam.model.UserPhone;
import ru.iql.exam.service.UserPhoneService;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserPhoneServiceImpl implements UserPhoneService {

    private final UserPhoneRepository phoneRepository;

    @Override
    public Optional<UserPhone> findByNumber(String number) {
        return phoneRepository.findByValue(number);
    }

}
