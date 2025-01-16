package org.example.controller

import de.itdesign.clarity.rest.RestResponse
import org.example.service.TeamService

class TeamController {
    TeamService teamService = new TeamService()

    def createTeam(String xmlData) {
        try {
            RestResponse response = teamService.createTeam(xmlData)
            return  [status: 'success', Team: response?.jsonMap()]
        } catch (Exception e) {
            return  [status : 'error', message: e.message]
        }
    }

}
