package org.example.service

import groovy.json.JsonBuilder
import groovy.sql.Sql
import de.itdesign.clarity.rest.ClarityRestClient
import de.itdesign.clarity.rest.RestResponse
import java.sql.Connection
import java.sql.DriverManager
import org.example.utils.dbUtil

class ProjectService {

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

    static RestResponse createProject(Map projectData) {
        return sendRequest('POST', '/projects', projectData)
    }

    static RestResponse updateProject(String projectId, Map projectData) {
        return sendRequest('PATCH', "/projects/${projectId}", projectData)
    }

    static RestResponse getProject(String projectId) {
        return sendRequest('GET', "/projects/${projectId}")
    }
}
