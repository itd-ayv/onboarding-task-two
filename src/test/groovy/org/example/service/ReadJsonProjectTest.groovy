package org.example.service

import org.example.controller.ProjectController
import org.example.controller.TaskController
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.mockito.Mockito.*

class ReadJsonProjectTest {

    // Mock dependencies
    ProjectController projectController
    TaskController taskController
    ReadJsonProjectAndTask readJsonProject

    @BeforeEach
    void setUp() {
        // Mock ProjectController and TaskController
        projectController = mock(ProjectController.class)
        taskController = mock(TaskController.class)

        // Initialize ReadJsonProject with mocked dependencies
        readJsonProject = new ReadJsonProjectAndTask()
        readJsonProject.projectController = projectController
        readJsonProject.taskController = taskController
    }

    @Test
    void testReadAndCreateProjects() {
        // Mock the project data
        def projectData = [
            name          : 'Test Project',
            scheduleStart : '2025-01-01',
            scheduleFinish: '2025-12-31',
            createdDate   : '2025-01-01',
            isActive      : true
        ]

        // Simulate a valid response from the projectController.createProject method
        def mockResponse = [status: 'success', project: [projectId: '12345']]
        when(projectController.createProject(any())).thenReturn(mockResponse)

        // Call the method under test
        readJsonProject.readAndCreateProjects()

        // Verify interactions with the projectController
        verify(projectController, times(1)).createProject(any())

        // Verify that tasks are processed (mocked as println output in your code)
        verify(taskController, never()).createTask(any(), any())  // No tasks to create in this case
    }

    @Test
    void testValidateProject_validProject() {
        // Create a valid project
        def project = [
            name          : 'Valid Project',
            scheduleStart : '2025-01-01',
            scheduleFinish: '2025-12-31',
            createdDate   : '2025-01-01',
            isActive      : true
        ]

        // Call the method under test
        def validationErrors = readJsonProject.validateProject(project)

        // Assert that there are no validation errors for a valid project
        assert validationErrors.isEmpty()
    }

    @Test
    void testValidateProject_invalidProject() {
        // Create an invalid project (e.g., missing or invalid fields)
        def project = [
            name          : '',
            scheduleStart : '2025-01-01',
            scheduleFinish: '2025-12-31',
            createdDate   : '2025-01-01',
            isActive      : true
        ]

        // Call the method under test
        def validationErrors = readJsonProject.validateProject(project)

        // Assert that validation errors are returned
        assert validationErrors.size() == 1
        assert validationErrors.contains("Name cannot be null or empty.")
    }

    @Test
    void testLogValidationErrors() {
        // Capture println output
        def out = new ByteArrayOutputStream()
        System.setOut(new PrintStream(out))

        // Create a project with validation errors
        def project = [
            name          : '',
            scheduleStart : '2025-01-01',
            scheduleFinish: '2025-12-31',
            createdDate   : '2025-01-01',
            isActive      : true
        ]
        def validationErrors = readJsonProject.validateProject(project)

        // Log the validation errors
        readJsonProject.logValidationErrors(project.name, validationErrors)

        // Assert that the validation error message is printed
        assert out.toString().contains("Validation failed for project ''")
        assert out.toString().contains("  - Name cannot be null or empty.")
    }
}
