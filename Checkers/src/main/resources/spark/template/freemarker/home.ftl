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
  <#include "nav-bar.ftl" />

  <div class="body">

    <!-- Provide a message to the user, if supplied. -->
    <#include "message.ftl" />

    <h2>Players Online</h2>

    <#if currentUser??>
        <#if playerNumber??>
            <#list playerList as player>
                <#if player.getName() == currentUser.getName()>
                <#else>
                    <li>Name: <a href="/game?id=${player.getName()}">${player.getName()}</a></li>
                </#if>
            </#list>
        <#else>
            <ul>
                <li><i>There are no other players available at this time</i></li>
            </ul>
        </#if>
    <#else>
        <#if playerNumber??>
            <ul>
                <li>There are currently ${playerNumber} player(s) active</li>
            <ul>
        <#else>
            <ul>
                <li><i>There are no other players available at this time</i></li>
            </ul>
        </#if>
    </#if>

    <!-- TODO: future content on the Home:
            to start games,
            spectating active games,
            or replay archived games
    -->

  </div>

</div>
</body>

</html>
