<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!DOCTYPE html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script src="${pageContext.request.contextPath}/js/chatUnit.js"></script>

</head>
<body>
<h1>Chat</h1>
<div class="chatBox">
    <section class="bord">
        <div class="messages">
        </div>
        <label>
            <textarea class="msg"></textarea>
        </label>
    </section>
    <p>
        <button class="SendButton" id="chatBox">submit</button>
    </p>
</div>
</body>
