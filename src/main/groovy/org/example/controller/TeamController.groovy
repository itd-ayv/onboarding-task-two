package org.example.controller

import de.itdesign.clarity.rest.RestResponse
import org.example.service.TeamService

class TeamController {
    TeamService TeamService = new TeamService()

    def createTeam(String projectInternalId, Map TeamData) {
        try {
            RestResponse response = TeamService.createTeam(projectInternalId, TeamData)
            return  [status: 'success', Team: response?.jsonMap()]
        } catch (Exception e) {
            return  [status : 'error', message: e.message]
        }
    }

    def getTeam(String projectInternalId) {
        try {
            RestResponse response = TeamService.getTeam(projectInternalId)
            return [status: 'success', Team: response?.jsonMap()]
        } catch(Exception e) {
            return [status: 'error', message: e.message]
        }
    }

    def updateTeam(String projectInternalId, String TeamId, Map TeamData) {
        try {
            RestResponse response = TeamService.updateTeam(projectInternalId, TeamId, TeamData)
            return [status: 'success', Team: response?.jsonMap()]
        } catch (Exception e) {
            return [status: 'error', message: e.message]
        }
    }
}
