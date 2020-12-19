<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageTitle" required="true" %>

 <html>
 <head>
    <title>${pageTitle}</title>
     <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

     <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css" integrity="sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2" crossorigin="anonymous">

     <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link href='http://fonts.googleapis.com/css?family=Lobster+Two' rel='stylesheet' type='text/css'>
 </head>
 <body>
    <header>
        <a style="color: #5e253f;" href="${pageContext.servletContext.contextPath}">
            <img style="min-width: 100px" src="${pageContext.request.contextPath}/images/icon.png"/>
            Referencing System
        </a>
    </header>
    <main>
        <jsp:doBody/>
    </main>
 </body>
 </html>