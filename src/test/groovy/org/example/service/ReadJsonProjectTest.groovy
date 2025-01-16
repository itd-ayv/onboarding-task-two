package org.example.service

import org.example.controller.ProjectController
import org.example.controller.TaskController
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.mockito.Mockito.*

class ReadJsonProjectTest {
    ProjectController projectController
    TaskController taskController
    ReadJsonProjectAndTask readJsonProject

    @BeforeEach
    void setUp() {
        projectController = mock(ProjectController.class)
        taskController = mock(TaskController.class)
        readJsonProject = new ReadJsonProjectAndTask()
        readJsonProject.projectController = projectController
        readJsonProject.taskController = taskController
    }

    @Test
    void testReadAndCreateProjects() {
        def projectData = [
            name          : 'Test Project',
            scheduleStart : '2025-01-01',
            scheduleFinish: '2025-12-31',
            createdDate   : '2025-01-01',
            isActive      : true
        ]

        def mockResponse = [status: 'success', project: [projectId: '12345']]
        when(projectController.createProject(any())).thenReturn(mockResponse)

        readJsonProject.readAndCreateProjects()

        verify(projectController, times(1)).createProject(any())

        verify(taskController, never()).createTask(any(), any())
    }

    @Test
    void testValidateProject_validProject() {
        def project = [
            name          : 'Valid Project',
            scheduleStart : '2025-01-01',
            scheduleFinish: '2025-12-31',
            createdDate   : '2025-01-01',
            isActive      : true
        ]

        def validationErrors = readJsonProject.validateProject(project)

        assert validationErrors.isEmpty()
    }

    @Test
    void testValidateProject_invalidProject() {
        def project = [
            name          : '',
            scheduleStart : '2025-01-01',
            scheduleFinish: '2025-12-31',
            createdDate   : '2025-01-01',
            isActive      : true
        ]

        def validationErrors = readJsonProject.validateProject(project)
        assert validationErrors.size() == 1
        assert validationErrors.contains("Name cannot be null or empty.")
    }

    @Test
    void testLogValidationErrors() {
        def out = new ByteArrayOutputStream()
        System.setOut(new PrintStream(out))

        def project = [
            name          : '',
            scheduleStart : '2025-01-01',
            scheduleFinish: '2025-12-31',
            createdDate   : '2025-01-01',
            isActive      : true
        ]
        def validationErrors = readJsonProject.validateProject(project)

        readJsonProject.logValidationErrors(project.name, validationErrors)

        assert out.toString().contains("Validation failed for project ''")
        assert out.toString().contains("  - Name cannot be null or empty.")
    }
}
