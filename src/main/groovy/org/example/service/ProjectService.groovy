package org.example.service

import groovy.json.JsonBuilder
import groovy.sql.Sql
import de.itdesign.clarity.rest.ClarityRestClient
import de.itdesign.clarity.rest.RestResponse
import java.sql.Connection
import org.example.utils.dbUtil
import de.itdesign.clarity.logging.CommonLogger

class ProjectService {
    static Sql sql
    static CommonLogger cmnLog = new CommonLogger(this)
    static Connection connection = dbUtil.connect()

    static RestResponse sendRequest(String httpMethod, String endpoint, Map data = null) {
        Sql sql = new Sql(connection)
        ClarityRestClient rest = new ClarityRestClient("admin", sql.getConnection(), cmnLog, "http://10.0.0.173:7080")
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

    static RestResponse getAllProject(String projectId) {
        return sendRequest('GET', "/projects/${projectId}")
    }

    static List<Map> getProject(List<Map> projectNamesFromJson) {
        def allProjects = []
        def projectNames = projectNamesFromJson.collect { it.name }
        def statement = connection.createStatement()
        def resultSet = statement.executeQuery("SELECT ID, CODE, NAME FROM INV_INVESTMENTS WHERE name IN (${projectNames.collect { "'${it}'" }.join(",")})")
        while (resultSet.next()) {
            def project = [
                    id  : resultSet.getInt("ID"),
                    code: resultSet.getString("CODE"),
                    name: resultSet.getString("NAME")
            ]
            allProjects << project
        }
        return allProjects
    }

    // Method to retrieve project internal_id from the database using project name
    static String getProjectInternalId(String projectName) {
        def statement = connection.createStatement()
        def query = "SELECT ID, CODE, NAME FROM INV_INVESTMENTS WHERE name = ?"
        def preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, projectName)
        def resultSet = preparedStatement.executeQuery()
        if (resultSet.next()) {
            return resultSet.getString("ID")
        } else {
            return null
        }
    }

    // Method to retrieve resource id and code from the database using resource code
    static Map getResourceDetails(String resourceCode) {
        def query = "SELECT ID, UNIQUE_NAME FROM SRM_RESOURCES WHERE UNIQUE_NAME = ?"
        def preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, resourceCode)
        def resultSet = preparedStatement.executeQuery()
        if (resultSet.next()) {
            return [id: resultSet.getString("ID"), code: resultSet.getString("UNIQUE_NAME")]
        } else {
            println("Resource with code ${resourceCode} not found in the database.")
            return null
        }
    }

}
