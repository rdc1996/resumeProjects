<!DOCTYPE html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
    <meta http-equiv="refresh" content="10">
    <title>Web Checkers | ${title}</title>
    <link rel="stylesheet" type="text/css" href="/css/style.css">
</head>

<body>
<div class="page">

    <h1>Web Checkers | ${title}</h1>

    <!-- Provide a navigation bar -->
    <div class="navigation">
        <a href="/">my home</a>
    </div>

    <div class="body">

        <!-- Provide a message to the user, if supplied. -->
        <#include "message.ftl" />

        <form action="./signin" method="POST">
            Input a Name:
            <#if invalidSignin??>
                <p>${invalidSignin}</p>
            </#if>
            <input name="userName" />
            <br/>
            <button type="submit">Sign In</button>
        </form>

        <!-- TODO: future content on the Home:
                to start games,
                spectating active games,
                or replay archived games
        -->
    </div>

</div>
</body>

</html>