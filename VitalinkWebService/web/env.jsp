<%@ page import="java.util.Map" %><%--
  Created by IntelliJ IDEA.
  User: bdcuyp0
  Date: 16-11-2016
  Time: 13:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Environment</title>
</head>
<body>
<h1>System Properties</h1>

<table>
    <thead>
    <tr>
        <td>Key</td>
        <td>Value</td>
    </tr>
    </thead>
    <tbody>
    <% for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {%>
    <tr>
        <td><%= entry.getKey().toString() %>
        </td>
        <td><%= entry.getValue().toString() %>
        </td>
    </tr>
    <% } %>
    </tbody>
</table>

<h1>Environment Variables</h1>

<table>
    <thead>
    <tr>
        <td>Key</td>
        <td>Value</td>
    </tr>
    </thead>
    <tbody>
    <% for (Map.Entry<String, String> entry : System.getenv().entrySet()) {%>
    <tr>
        <td><%= entry.getKey() %>
        </td>
        <td><%= entry.getValue() %>
        </td>
    </tr>
    <% } %>
    </tbody>
</table>

</body>
</html>
