package com.morning.taskapimain;

import com.morning.taskapimain.entity.dto.ProjectDTO;
import com.morning.taskapimain.entity.dto.UserWithRoleDTO;
import com.morning.taskapimain.entity.project.*;
import com.morning.taskapimain.repository.*;
import com.morning.taskapimain.service.ProjectJWTService;
import com.morning.taskapimain.service.ProjectService;
import com.morning.taskapimain.service.kafka.KafkaNotificationService;
import com.morning.taskapimain.service.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import com.morning.taskapimain.exception.NotFoundException;

import java.util.List;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProjectServiceTestCreation {

    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectRoleRepository projectRoleRepository;
    @Mock private ProjectResourceRepository projectResourceRepository;
    @Mock private ProjectStatusRepository projectStatusRepository;
    @Mock private ProjectTagRepository projectTagRepository;
    @Mock private ProjectUserRepository projectUserRepository;
    @Mock private UserRepository userRepository;
    @Mock private JwtService jwtService;
    @Mock private ProjectJWTService projectJWTService;
    @Mock
    private KafkaNotificationService kafkaNotificationService;

    @InjectMocks
    private ProjectService projectService;



    @Test
    void testGetFullProjectInfoById_notFound() {
        Long projectId = 999L;

        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());


        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFu4llProjectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFull3ProjectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFul3lProjectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetF3ullProjectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetF3ullProjectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGet3FullProjectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGe3tFullProjectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void tes3tGetFullProjectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void t3estGetFullProjectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_notFo3und1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById_notFo3und2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_notF3ound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById_no3tFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_3notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById2_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoBy2Id_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoB2yId_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfo2ById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectI2nfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProj2ectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullP2rojectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testG2etFullProjectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFu2llProjectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetF1ullProjectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFul1lProjectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testG1etFullProjectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void tes11tGetFullProjectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void tes1tGetFullProjectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void t1estGetFullProjectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void te1stGetFullProjectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoByI1d_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoByI1d_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById1_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_no1tFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById_notF1ound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_notFoun1d1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById_notFoun1d2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_notFound10() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById_notFound20() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_notFound19() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById_notFound29() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_notFound18() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById_notFound28() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_notFound17() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById_notFound27() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_notFound16() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById_notFound26() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_notFound15() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById_notFound25() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_notFound14() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById_notFound24() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_notFound13() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById_notFound23() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_notFound11() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById_notFound21() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_notFound12() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById_notFound22() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testaaGetFullProjectInfoById_notFound() {
        Long projectId = 999L;

        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());


        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void teastGetFullProjectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFu4llProejectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFull3ProjectInfeoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFwul3lProjectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetF3ullPrwwojectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetF3ullPterojectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGet3FullPreojectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGe3twFullProjectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void tes3tGetFullPreojectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void t3estGetFullProjectInfoById_nottFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProejectInfoById_notFo3und1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoByIdt_notFo3und2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInftoById_notF3ound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInrfoById_no3tFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectrInfoById_3notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullrProjectInfoById2_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFeullProjectInfoBy2Id_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGeeetFullProjectInfoB2yId_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGeetFullProjectInfo2ById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void teestGetFullProjectI2nfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void teestGetFullProj2ectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void tewstGetFullP2rojectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void teswtG2etFullProjectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGwwetFu2llProjectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGewtF1ullProjectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFwul1lProjectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testG1etwFullProjectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void tes11tGetFuwllProjectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void tes1tGetFullwProjectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void t1estGetFullProwjectInfoById_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void te1stGetFullProjwectInfoById_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjecwtInfoByI1d_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectIwnfoByI1d_notFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInwfoById1_notFound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfwoById_no1tFound1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfwoById_notF1ound2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfwoById_notFoun1d1() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfowById_notFoun1d2() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoBywId_notFound10() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoBywId_notFound20() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_wnotFound19() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById_nowtFound29() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_notFouwnd18() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById_notFoundw28() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_notFoundq17() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById_notFoqund27() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfoById_noqtFound16() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfoById_nqtFound26() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectInfqoById_notFound15() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjectInfqoById_notFound25() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProjectIqnfoById_notFound14() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullProjeqctInfoById_notFound24() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGetFullProqjectInfoById_notFound13() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testGetFullqqProjectInfoById_notFound23() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGewtFullProjectInfoById_notFound11() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void testqGetFullProjectInfoById_notFound21() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }
    @Test
    void testGeqtFullProjectInfoById_notFound12() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }

    @Test
    void tqestGetFullProjectInfoById_notFound22() {
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getFullProjectInfoById(projectId))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Project not found")
                )
                .verify();
    }


}

