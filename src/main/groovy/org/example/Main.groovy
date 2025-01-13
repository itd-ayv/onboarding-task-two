package org.example

import groovy.json.JsonSlurper
import org.example.controller.ProjectController
import org.example.controller.TaskController


class Main {
    static void main(String[] args) {
        ProjectController projectController = new ProjectController()
        TaskController taskController = new TaskController()
        def resourcePath = 'resource.json'
        println(resourcePath)
        def jsonFile = Main.class.getClassLoader().getResource(resourcePath)
        def jsonText = jsonFile.text
        println(jsonText)
        projectController.getProject("5005001")

        def jsonSlurper = new JsonSlurper()
//        def parsedData = jsonSlurper.parseText(jsonText)
//        parsedData?.projects?.each { project ->
//            def projectData = [
//                    name          : project.name,
//                    scheduleStart : project.scheduleStart,
//                    scheduleFinish: project.scheduleFinish,
//                    createdDate   : project.createdDate,
//                    isActive      : project.isActive
//            ]
//            def response = projectController.createProject(projectData)
//            println(response)
//        }

//        def parsedData = jsonSlurper.parseText(jsonText)
//        def taskData = [
//                name: 'sample taskk',
//                _parentId: '5002000',
//                status: 0
//
//        ]
        def response = taskController.getTask('5002000')
        println(response)
    }

}