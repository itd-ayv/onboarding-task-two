package org.example.service

import groovy.json.JsonSlurper
import org.example.controller.TaskController

class ReadJsonTask {

    TaskController taskController = new TaskController()
    def taskPath = 'task.json'

    def readAndCreateTasks() {
        def jsonFile = getClass().getClassLoader().getResource(taskPath)
        def jsonText = jsonFile.text
        def jsonSlurper = new JsonSlurper()
        def parsedData = jsonSlurper.parseText(jsonText)
        println(parsedData)

        parsedData?.tasks?.each { task ->
            def validationErrors = validateTask(task)
            if (validationErrors) {
                logValidationErrors(task.name, validationErrors)
                return
            }
            def taskData = [
                    name     : task.name,
                    _parentId: task._parentId,
                    status   : task.status,

            ]
            // def response = taskController.createTask(task._parentId, taskData)
            println(taskData)
        }
    }

    // Method to validate each task
    private List<String> validateTask(def task) {
        def validationErrors = []

        if (!isValidName(task.name)) {
            validationErrors << "Name cannot be null or empty."
        }

        if (!isValidParentId(task._parentId)) {
            validationErrors << "_parentId must be a valid integer."
        }

        if (!isValidStatus(task.status)) {
            validationErrors << "Status must be 0, 1, or 2."
        }

        return validationErrors
    }

    private boolean isValidName(String name) {
        return name && !name.trim().isEmpty()
    }

    private boolean isValidParentId(def parentId) {
        return parentId instanceof Integer || parentId instanceof Long
    }

    private boolean isValidStatus(def status) {
        return status in [0, 1, 2]
    }

    private void logValidationErrors(String taskName, List<String> validationErrors) {
        println "Validation failed for task '${taskName}':"
        validationErrors.each { error ->
            println "  - $error"
        }
    }

}