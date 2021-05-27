package com.asyncworking.services;

import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.exceptions.ProjectNotFoundException;
import com.asyncworking.exceptions.UserNotFoundException;
import com.asyncworking.models.*;
import com.asyncworking.repositories.MessageRepository;
import com.asyncworking.repositories.ProjectRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.mapper.MessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
public class MessageServiceTest {
    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Autowired
    private MessageMapper messageMapper;

    private MessageService messageService;

    private MessagePostDto messagePostDto;

    private Project mockProject;

    private OffsetDateTime currentTime;

    private UserEntity mockUserEntity1;

    private UserEntity mockUserEntity2;

    private Message mockMessage1;

    private Message mockMessage2;

    private Message mockMessage3;

    @BeforeEach
    public void setUp() {
        messageService = new MessageService(
                messageMapper,
                projectRepository,
                messageRepository,
                userRepository
        );

        messagePostDto = MessagePostDto.builder()
                .companyId(1L)
                .projectId(1L)
                .messageTitle("first message")
                .posterUserId(1L)
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .docURL("https:www.adc.com")
                .build();

        mockProject = Project.builder()
                .id(1L)
                .name("project1")
                .build();

        currentTime = OffsetDateTime.now(UTC);

        mockUserEntity1 = UserEntity.builder()
                .id(1L)
                .name("testName1")
                .build();

        mockUserEntity2 = UserEntity.builder()
                .id(2L)
                .name("testName2")
                .build();

    }

    public void mockMessage() {

        mockMessage1 = Message.builder()
                .id(1L)
                .project(mockProject)
                .posterUserId(1L)
                .messageTitle("first message")
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .postTime(currentTime)
                .docURL("abc.com")
                .build();
        mockMessage2 = Message.builder()
                .id(2L)
                .project(mockProject)
                .posterUserId(1L)
                .messageTitle("second message")
                .content("second message content")
                .category(Category.ANNOUNCEMENT)
                .postTime(currentTime)
                .docURL("abc.com")
                .build();
        mockMessage3 = Message.builder()
                .id(2L)
                .project(mockProject)
                .posterUserId(2L)
                .messageTitle("second message")
                .content("second message content")
                .category(Category.ANNOUNCEMENT)
                .postTime(currentTime)
                .docURL("abc.com")
                .build();
    }


    @Test
    @Transactional
    public void shouldReturnMessageGetDtoGivenCorrectMessagePostDto() {

        Message mockReturnMessage = Message.builder()
                .id(1L)
                .project(mockProject)
                .companyId(1L)
                .posterUserId(1L)
                .messageTitle("first message")
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .createdTime(currentTime)
                .postTime(currentTime)
                .updatedTime(currentTime)
                .docURL("https:www.adc.com")
                .build();

        MessageGetDto mockMessageGetDto = MessageGetDto.builder()
                .id(1L)
                .messageTitle("first message")
                .posterUserId(1L)
                .posterUser("testName1")
                .content("first message content")
                .category(Category.ANNOUNCEMENT)
                .postTime(currentTime)
                .docURL("https:www.adc.com")
                .build();

        when(messageRepository.save(any())).thenReturn(mockReturnMessage);
        when(userRepository.findUserEntityById(1L)).thenReturn(Optional.of(mockUserEntity1));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));

        assertEquals(mockMessageGetDto, messageService.createMessage(messagePostDto));
    }

    @Test
    public void shouldThrowProjectNotFoundExceptionWhenGivenMessagePostDtoWhichProjectIdIsNotExist() {
        when(projectRepository.findById(3L))
                .thenThrow(new ProjectNotFoundException("Cannot find project by id:2"));
        assertThrows(ProjectNotFoundException.class, () -> messageService.createMessage(messagePostDto));
    }

    @Test
    public void shouldReturnListOfUserEntityWhenGivenCorrectListOfMessage() {
        this.mockMessage();
        when(userRepository.findByIdIn(List.of(1L, 1L, 2L))).thenReturn(Optional.of(List.of(mockUserEntity1, mockUserEntity2)));
        List<UserEntity> userEntityList = messageService.findUserEntityByMessageList(List.of(mockMessage1, mockMessage2, mockMessage3));
        assertNotNull(userEntityList);
        assertEquals(2, userEntityList.size());
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenGivenInvalidListOfMessage() {
        this.mockMessage();
        when(userRepository.findByIdIn(List.of(1L, 1L, 2L))).thenThrow(new UserNotFoundException("cannot find user by id in [1, 1, 2]"));
        assertThrows(UserNotFoundException.class, () -> messageService.findUserEntityByMessageList(
                List.of(mockMessage1, mockMessage2, mockMessage3)));
    }

    @Test
    public void shouldReturnListOfMessageGetDtoListWhenGivenCorrectProjectId () {
        this.mockMessage();
        when(messageRepository.findByProjectId(1L)).thenReturn(List.of(mockMessage1, mockMessage2, mockMessage3));
        when(userRepository.findByIdIn(List.of(1L, 1L, 2L))).thenReturn(Optional.of(List.of(mockUserEntity1, mockUserEntity2)));
        List<MessageGetDto> mockMessageGetDtoList = messageService.findMessageListByProjectId(1L);
        assertNotNull(mockMessageGetDtoList);
        assertEquals(3, mockMessageGetDtoList.size());
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenGivenProjectIdWhichMessagePosterUserIdNotFetchListOfUserEntity() {
        this.mockMessage();
        when(messageRepository.findByProjectId(1L)).thenReturn(List.of(mockMessage1, mockMessage2, mockMessage3));
        when(userRepository.findByIdIn(List.of(1L, 1L, 2L))).thenReturn(Optional.of(List.of(mockUserEntity1)));
        assertThrows(UserNotFoundException.class, () ->messageService.findMessageListByProjectId(1L));
    }

}
