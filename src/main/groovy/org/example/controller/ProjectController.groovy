package org.example.controller

import de.itdesign.clarity.rest.RestResponse
import org.example.service.ProjectService

class ProjectController {

    ProjectService projectService = new ProjectService()

    def createProject(Map projectData) {
        try {
            RestResponse response = projectService.createProject(projectData)
            return [status: 'success', project: response?.jsonMap()]
        } catch (Exception e) {
            return [status: 'error', message: e.message]
        }
    }

    def getProject(String projectId) {
        try {
            RestResponse response = projectService.getProject(projectId)
            return [status: 'success', project: response?.jsonMap()]
        } catch (Exception e) {
            return [status: 'error', message: e.message]
        }
    }

    def updateProject(String projectId, Map projectData) {
        try {
            RestResponse response = projectService.updateProject(projectId, projectData)
            return [status: 'success', project: response?.jsonMap()]
        } catch (Exception e) {
            return [status: 'error', message: e.message]
        }
    }
}
