package org.example.service

import groovy.json.JsonSlurper
import org.example.controller.TaskController
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

import static org.mockito.Mockito.*
import static org.junit.jupiter.api.Assertions.*

@ExtendWith(MockitoExtension.class)
class ReadJsonTaskTest {

    @Mock
    TaskController taskController

    @InjectMocks
    ReadJsonTask readJsonTask

    @BeforeEach
    void setUp() {

    }

    @Test
    void testValidTask() {
        def validTask = [
            name     : "Task A",
            _parentId: 5002000,
            status   : 1
        ]

        def validationErrors = readJsonTask.validateTask(validTask)
        assertTrue(validationErrors.isEmpty(), "Expected no validation errors for valid task.")
    }

    @Test
    void testInvalidName() {
        def invalidTask = [
            name     : "",
            _parentId: 5002000,
            status   : 1
        ]

        def validationErrors = readJsonTask.validateTask(invalidTask)
        assertFalse(validationErrors.isEmpty(), "Expected validation errors for task with invalid name.")
        assertTrue(validationErrors.contains("Name cannot be null or empty."))
    }

    @Test
    void testInvalidParentId() {
        def invalidTask = [
            name     : "Task with invalid Parent",
            _parentId: "invalid",
            status   : 1
        ]

        def validationErrors = readJsonTask.validateTask(invalidTask)
        assertFalse(validationErrors.isEmpty(), "Expected validation errors for task with invalid parentId.")
        assertTrue(validationErrors.contains("_parentId must be a valid integer."))
    }

    @Test
    void testInvalidStatus() {
        def invalidTask = [
            name     : "Task with invalid Status",
            _parentId: 5002000,
            status   : 99
        ]

        def validationErrors = readJsonTask.validateTask(invalidTask)
        assertFalse(validationErrors.isEmpty(), "Expected validation errors for task with invalid status.")
        assertTrue(validationErrors.contains("Status must be 0, 1, or 2."))
    }

    @Test
    void testReadAndCreateTasks() {
        def mockTaskJson = '''
        {
            "tasks": [
                {
                    "name": "Task 1",
                    "_parentId": 5002000,
                    "status": 1
                }
            ]
        }
        '''

        def mockInputStream = new ByteArrayInputStream(mockTaskJson.getBytes())
        def jsonSlurper = new JsonSlurper()
        def parsedData = jsonSlurper.parse(mockInputStream)

      doNothing().when(taskController).createTask(eq(5002000), argThat { map ->
        map.name == "Task 1" && map._parentId == 5002000 && map.status == 1
    })
        readJsonTask.readAndCreateTasks()

       verify(taskController, times(1)).createTask(eq(5002000), argThat { map ->
        map.name == "Task 1" && map._parentId == 5002000 && map.status == 1
    })
    }
}
