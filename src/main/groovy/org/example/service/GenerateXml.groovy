package org.example.service

import groovy.xml.MarkupBuilder
import groovy.json.JsonSlurper
import org.example.controller.ProjectController


class GenerateXml {
    ProjectController projectController = new ProjectController()

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
        new File('output.xml').text = writer.toString()
    }

    def String generateAssignmentXml() {
        def assignmentPath = "assignment.json"
        def projectPath = "project.json"
        def taskPath = "task.json"
        def jsonSlurper = new JsonSlurper()
        def jsonFileAssignment = getClass().getClassLoader().getResource(assignmentPath)
        def jsonFileProject = getClass().getClassLoader().getResource(projectPath)
        def jsonFileTask = getClass().getClassLoader().getResource(taskPath)
        def jsonTextAssignment = jsonFileAssignment.text
        def jsonTextProject = jsonFileProject.text
        def jsonTextTask = jsonFileTask.text
        def jsonDataAssignment = jsonSlurper.parseText(jsonTextAssignment)
        def jsonDataProject = jsonSlurper.parseText(jsonTextProject)
        def projectData = projectController.getProject(jsonDataProject.projects)
        println(projectData)
        def jsonDataTask = jsonSlurper.parseText(jsonTextTask)
        def writer = new StringWriter()
        def xmlBuilder = new MarkupBuilder(writer)

        xmlBuilder.NikuDataBus('xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance", "xsi:noNamespaceSchemaLocation": "../xsd/nikuxog_project.xsd") {
            Header(action: "write", externalSource: "NIKU", objectType: "project", version: "7.1.0.3023")
            Projects {
                jsonDataProject.projects.each { project ->
                    Project(projectID: project.projectID, name: project.name) {
                        Tasks {
                            project.tasks.each { task ->
                                Task(internalTaskID: task.internalTaskID, taskID: task.taskID, outlineLevel: task.outlineLevel, name: task.name) {
                                    Assignments {
                                        task.assignments.each { assignment ->
                                            TaskLabor(actualWork: assignment.actualWork, baselineWork: assignment.baselineWork,
                                                    remainingWork: assignment.remainingWork, resourceID: assignment.resourceID) {
                                                CustomInformation()
                                            }
                                        }
                                    }

                                    CustomInformation()
                                }
                            }
                        }

                        Dependencies()
                        CustomInformation()
                        OBSAssocs()
                    }
                }
            }
        }
        new File('output.xml').text = writer.toString()
    }
}
