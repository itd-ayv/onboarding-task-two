package org.example.service

import groovy.json.JsonBuilder
import groovy.sql.Sql
import de.itdesign.clarity.rest.ClarityRestClient
import de.itdesign.clarity.rest.RestResponse
import java.sql.Connection
import org.example.utils.dbUtil

class TaskService {

    static Connection connection = dbUtil.connect()
    static RestResponse sendRequest(String httpMethod, String endpoint, Map data = null) {
        Sql sql = new Sql(connection)
        ClarityRestClient rest = new ClarityRestClient("admin", sql.getConnection(), "http://10.0.0.173:7080")
        def jsonData = data ? new JsonBuilder(data).toString() : null
        RestResponse response
        try {
            if (httpMethod == 'POST') {
                response = rest.POST(endpoint, jsonData)
            } else if (httpMethod == 'PATCH') {
                response = rest.PATCH(endpoint, jsonData)
            } else if (httpMethod == 'GET') {
                response = rest.GET(endpoint)
            }
            println "Response: ${response?.jsonMap()}"
        } catch (Exception e) {
            println "Caught exception: ${e.message}"
            e.printStackTrace()
        } finally {
            rest?.close()
        }

        return response
    }

    static RestResponse createTask(String projectInternalId, Map taskData) {
        return sendRequest('POST', "/projects/${projectInternalId}/tasks", taskData)
    }

    static RestResponse updateTask(String projectInternalId, String taskId, Map taskData) {
        return sendRequest('PATCH', "/projects/${projectInternalId}/tasks/${taskId}", taskData)
    }

    static RestResponse getTask(String projectInternalId) {
        return sendRequest('GET', "projects/${projectInternalId}/tasks/")
    }
}
