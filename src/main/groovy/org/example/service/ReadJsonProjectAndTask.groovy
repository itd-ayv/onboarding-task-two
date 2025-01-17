package org.example.service

import groovy.json.JsonSlurper
import org.example.controller.ProjectController
import org.example.controller.TaskController

class ReadJsonProjectAndTask {
    ProjectController projectController = new ProjectController()
    TaskController taskController = new TaskController()
    def projectPath = 'project.json'
    def taskPath = 'task.json'

    def readAndCreateProjects() {
        def jsonFile = getClass().getClassLoader().getResource(projectPath)
        def jsonText = jsonFile.text
        def jsonSlurper = new JsonSlurper()
        def parsedData = jsonSlurper.parseText(jsonText)
        // Reading tasks from JSON
        def taskFile = getClass().getClassLoader().getResource(taskPath)
        def taskText = taskFile.text
        def taskData = jsonSlurper.parseText(taskText)
        def statusMapping = [
                'NOT STARTED': 0,
                'IN PROGRESS': 1,
                'COMPLETED'  : 2
        ]
        parsedData?.projects?.each { project ->
            def validationErrors = validateProject(project)
            if (validationErrors) {
                logValidationErrors(project.name, validationErrors)
                return
            }
            def projectData = [
                    name          : project.name,
                    scheduleStart : project.scheduleStart,
                    scheduleFinish: project.scheduleFinish,
                    createdDate   : project.createdDate,
                    isActive      : project.isActive
            ]
            def response = projectController.createProject(projectData)
            def internalId = response?.project?._internalId

            if (internalId) {
                println "Project created with internal ID: ${internalId}"
                // Find tasks associated with this project
                def associatedTasks = taskData?.tasks?.findAll { it.project_id == project.id }

                if (associatedTasks?.isEmpty()) {
                    println "No tasks found for project ${project.name} (ID: ${project.id})"
                } else {
                    associatedTasks.each { task ->
                        def mappedStatus = statusMapping[task.status.toUpperCase()] ?: 0
                        def taskDataToPost = [
                                name     : task.name,
                                _parentId: internalId,
                                status   : mappedStatus
                        ]
                        // Posting the task for the created project
                        taskController.createTask(internalId as String, taskDataToPost)
                    }
                }
            } else {
                println "Failed to create project: ${projectData}. Response: ${response?.jsonMap()}"
            }
        }
    }

    def readProject() {
        def jsonFile = getClass().getClassLoader().getResource(projectPath)
        def jsonText = jsonFile.text
        def jsonSlurper = new JsonSlurper()
        def parsedData = jsonSlurper.parseText(jsonText)
        projectController.getProject(parsedData.projects)
    }

    // Method to validate each project
    private List<String> validateProject(def project) {
        def validationErrors = []

        if (!isValidName(project.name)) {
            validationErrors << "Name cannot be null or empty."
        }

        if (!isValidIsActive(project.isActive)) {
            validationErrors << "isActive must be true, false, 0, or 1."
        }

        if (!areValidDates(project.scheduleStart, project.scheduleFinish)) {
            validationErrors << "Schedule finish date cannot be before schedule start date."
        }

        return validationErrors
    }

    private boolean isValidName(String name) {
        return name && !name.trim().isEmpty()
    }

    private boolean isValidIsActive(def isActive) {
        return (isActive instanceof Boolean) || (isActive in [0, 1])
    }

    private boolean areValidDates(String startDate, String finishDate) {
        if (startDate && finishDate) {
            try {
                def scheduleStartDate = Date.parse("yyyy-MM-dd", startDate)
                def scheduleFinishDate = Date.parse("yyyy-MM-dd", finishDate)
                return !scheduleFinishDate.before(scheduleStartDate)
            } catch (Exception e) {
                return false
            }
        }
        return false
    }

    private void logValidationErrors(String projectName, List<String> validationErrors) {
        println "Validation failed for project '${projectName}':"
        validationErrors.each { error ->
            println "  - $error"
        }
    }
}
