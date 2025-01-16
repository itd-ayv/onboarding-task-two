package org.example

import org.example.controller.TeamController
import org.example.service.GenerateXml
import org.example.service.ReadJsonProjectAndTask

class ProjectDataHandler {
    static void main(String[] args) {
        ReadJsonProjectAndTask readJsonProjectAndTask = new ReadJsonProjectAndTask()
        readJsonProjectAndTask.readAndCreateProjects()
        GenerateXml generateResourceXml = new GenerateXml()
        generateResourceXml.generateResourceXml()
        generateResourceXml.generateAssignmentXml()
        String xmlData = new File("assignment.xml").text
        TeamController teamController = new TeamController()
        teamController.createTeam(xmlData)
    }
}