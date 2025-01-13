package org.example.controller

import de.itdesign.clarity.rest.RestResponse
import org.example.service.TaskService

class TaskController {
    TaskService taskService = new TaskService()

    def createTask(String projectInternalId, Map taskData) {
        try {
            RestResponse response = taskService.createTask(projectInternalId, taskData)
            return  [status: 'success', task: response?.jsonMap()]
        } catch (Exception e) {
            return  [status : 'error', message: e.message]
        }
    }

    def getTask(String projectInternalId) {
        try {
            RestResponse response = taskService.getTask(projectInternalId)
            return [status: 'success', task: response?.jsonMap()]
        } catch(Exception e) {
            return [status: 'error', message: e.message]
        }
    }

    def updateTask(String projectInternalId, String taskId, Map taskData) {
        try {
            RestResponse response = taskService.updateTask(projectInternalId, taskId, taskData)
            return [status: 'success', task: response?.jsonMap()]
        } catch (Exception e) {
            return [status: 'error', message: e.message]
        }
    }
}
