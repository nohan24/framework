<%@ page import ="java.util.List" %>
<%@ page import ="model.Emp" %>
<%
    List<String> lst = (List<String>)request.getAttribute("lst");
    Emp e = (Emp)request.getAttribute("obj");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <h1>Hello World !!!!!</h1>
    <%  
        for(String s : lst){ %>
            <p><%= s %></p>
        <% }
        out.print(e.getNom());
    %>
</body>
</html>