package org.example.service

import groovy.json.JsonSlurper
import org.example.controller.ProjectController
import org.example.controller.TaskController

class ReadJsonProject {

    ProjectController projectController = new ProjectController()
    TaskController taskController = new TaskController()
    def projectPath = 'project.json'

    def readAndCreateProjects() {
        def jsonFile = getClass().getClassLoader().getResource(projectPath)
        def jsonText = jsonFile.text

        def jsonSlurper = new JsonSlurper()
        def parsedData = jsonSlurper.parseText(jsonText)

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

            if (project.tasks != null) {
                project?.tasks?.each { task ->
                    def taskData = [
                            name     : task.name,
                            _parentId: internalId,
                            status   : task.status
                    ]
                    taskController.createTask(internalId as String, taskData)
                    println(taskData)
                }
            }
        }
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
