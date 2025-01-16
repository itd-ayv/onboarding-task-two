package org.example.service

import groovy.json.JsonSlurper
import org.example.controller.TeamController
import org.example.controller.TaskController

class ReadJsonTeam {
    TeamController teamController = new TeamController()
    def teamPath = 'team.json'

    def readAndCreateTeams() {
        def jsonFile = getClass().getClassLoader().getResource(teamPath)
        def jsonText = jsonFile.text

        def jsonSlurper = new JsonSlurper()
        def parsedData = jsonSlurper.parseText(jsonText)
        println(parsedData)

        parsedData?.teams?.each { team ->
            //def validationErrors = validateTeam(team)
            def teamData = [
                    resource          : team.resource,
            ]
            println(teamData)
            def response = teamController.createTeam("5002000", teamData)
            ///def internalId = response?.project?._internalId

            }
        }
}
