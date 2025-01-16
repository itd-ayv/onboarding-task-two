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

    def getAllProject(String projectId) {
        try {
            RestResponse response = projectService.getAllProject(projectId)
            return [status: 'success', project: response?.jsonMap()]
        } catch (Exception e) {
            return [status: 'error', message: e.message]
        }
    }

    def getProject(List<Map> projectData) {
        try {
            List<Map> response = projectService.getProject(projectData)
            return response
        } catch (Exception e) {
            return [status: 'error', message: e.message]
        }
    }

    def String getProjectInternalId(String projectName) {
        try {
           String response = projectService.getProjectInternalId(projectName)
            return response
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

    def Map getResourceDetails(String resourceCode) {
        try {
            Map response = projectService.getResourceDetails(resourceCode)
            return [status: 'success', resource: response]
        } catch (Exception e) {
            return [status: 'error', message: e.message]
        }
    }
}
