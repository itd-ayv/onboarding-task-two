package org.example

import groovy.json.JsonSlurper
import org.example.controller.ProjectController


class Main {
    static void main(String[] args) {
        ProjectController projectController = new ProjectController()
        def resourcePath = 'resource.json'
        println(resourcePath)
        def jsonFile = Main.class.getClassLoader().getResource(resourcePath)
        def jsonText = jsonFile.text
        println(jsonText)

        def jsonSlurper = new JsonSlurper()
        def parsedData = jsonSlurper.parseText(jsonText)
        parsedData?.projects?.each { project ->
            def projectData = [
                    name          : project.name,
                    scheduleStart : project.scheduleStart,
                    scheduleFinish: project.scheduleFinish,
                    createdDate   : project.createdDate,
                    isActive      : project.isActive
            ]
            def response = projectController.createProject(projectData)
            println(response)
        }


    }

}

//def response = projectController.getProject("5000000")
//        def projectData = [
//                name          : 'Project Alpha',
//                scheduleStart : '2023-01-02T08:00:00',
//                scheduleFinish: '2024-11-11T17:00:00',
//                createdDate   : '2023-01-01T08:00:00',
//                isActive      : true
//        ]
//        def projectDataa = [
//                name          : 'Project Alpha',
//
//        ] //5006000