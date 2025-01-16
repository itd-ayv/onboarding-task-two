package org.example.service

import groovy.xml.MarkupBuilder
import groovy.json.JsonSlurper
import org.example.controller.ProjectController
import org.example.controller.TaskController

class GenerateXml {
    ProjectController projectController = new ProjectController()
    TaskController taskController = new TaskController()

    def String generateResourceXml() {
        def resourcePath = "resource.json"
        def jsonSlurper = new JsonSlurper()
        def jsonFile = getClass().getClassLoader().getResource(resourcePath)
        def jsonText = jsonFile.text
        def jsonData = jsonSlurper.parseText(jsonText)
        def writer = new StringWriter()
        def xmlBuilder = new MarkupBuilder(writer)
        xmlBuilder.NikuDataBus('xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance", "xsi:noNamespaceSchemaLocation": "../xsd/nikuxog_resource.xsd") {
            Header(version: "6.0.12", action: "write", objectType: "resource", externalSource: "ORACLE-FINANCIAL")
            Resources {
                jsonData.resources.each { resource ->
                if (!resource.resourceId || !resource.name || resource.isActive == null || !resource.emailAddress) {
                    println "Skipping resource with missing required fields: $resource"
                    return
                }

                if (!isValidEmail(resource.emailAddress)) {
                    println "Invalid email address for resource ${resource.resourceId}: ${resource.emailAddress}"
                    return
                }
                    def nameParts = resource.name.split(' ', 2)
                    def firstName = nameParts[0]
                    def lastName = nameParts.size() > 1 ? nameParts[1] : ''
                    Resource(resourceId: resource.resourceId, isActive: resource.isActive,
                            employmentType: "Employee",
                            externalId: "2323AAA") {
                        PersonalInformation(lastName: lastName,
                                firstName: firstName,
                                emailAddress: resource.emailAddress)
                    }
                }
            }
        }
        new File('resource.xml').text = writer.toString()
    }

    def boolean isValidEmail(String email) {
    def emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
     return email.matches(emailPattern)
}

    def String generateAssignmentXml() {
        def assignmentPath = "assignment.json"
        def projectPath = "project.json"
        def taskPath = "task.json"

        def jsonSlurper = new JsonSlurper()

        // Load the JSON files as text
        def jsonFileAssignment = getClass().getClassLoader().getResource(assignmentPath)
        def jsonFileProject = getClass().getClassLoader().getResource(projectPath)
        def jsonFileTask = getClass().getClassLoader().getResource(taskPath)

        def jsonTextAssignment = jsonFileAssignment.text
        def jsonTextProject = jsonFileProject.text
        def jsonTextTask = jsonFileTask.text

        // Parse the JSON text
        def jsonDataAssignment = jsonSlurper.parseText(jsonTextAssignment)
        def jsonDataProject = jsonSlurper.parseText(jsonTextProject)
        def jsonDataTask = jsonSlurper.parseText(jsonTextTask)
        def projectData = projectController.getProject(jsonDataProject.projects)

        def writer = new StringWriter()
        def xmlBuilder = new MarkupBuilder(writer)

        xmlBuilder.NikuDataBus('xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance", "xsi:noNamespaceSchemaLocation": "../xsd/nikuxog_project.xsd") {
            Header(action: "write", externalSource: "NIKU", objectType: "project", version: "7.1.0.3023")

            Projects {
                jsonDataProject.projects.each { project ->
                    // Find matching project data (example: project name match)
                    def matchingProject = projectData.find { it.name == project.name }

                    if (matchingProject) {
                        Project(projectID: matchingProject.code, name: matchingProject.name) {
                            Tasks {
                                jsonDataTask.tasks.findAll { it.project_id == project.id }.each { task ->
                                    def taskAssignments = jsonDataAssignment.assignments.findAll { it.task_id == task.id }

                                    if (taskAssignments) {
                                        // For each task, generate XML with its assignments
                                        def taskData = taskController.getTaskInternalId(task.name)
                                        Task(taskID: taskData?.id, outlineLevel: "1", name: taskData?.name) {
                                            Assignments {
                                                taskAssignments.each { assignment ->
                                                    // For each assignment, add TaskLabor with details
                                                    TaskLabor(
                                                            actualWork: assignment.actuals ?: "0",  // Ensure no null values
                                                            baselineWork: "0",  // Default baselineWork is "0"
                                                            remainingWork: assignment.etc ?: "0",  // Ensure no null values
                                                            resourceID: assignment.resource_id) {

                                                        CustomInformation()
                                                    }
                                                }
                                            }
                                            CustomInformation()
                                        }
                                    } else {
                                        println("No assignments found for task: ${task.name}")
                                    }
                                }
                            }

                            Dependencies()
                            CustomInformation()
                            OBSAssocs()
                        }
                    } else {
                        println("No matching project found for: ${project.name}")
                    }
                }
            }
        }
        new File('assignment.xml').text = writer.toString()
        return writer.toString()
    }

}