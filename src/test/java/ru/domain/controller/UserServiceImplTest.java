package ru.domain.controller;

import domain.mapper.UserMapper;
import domain.repository.UserRepository;
import domain.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


@RequiredArgsConstructor
class UserServiceImplTest {
    private final UserServiceImpl userServiceImpl;
    private final UserMapper userMapper;
    private final UserRepository userRepository;


    @BeforeEach
    void setUp() {
        userServiceImpl = new UserServiceImpl(UserMapper userMapper, UserRepository userRepository);
    }

    @Test
    void testAddUser() {

    }
}
