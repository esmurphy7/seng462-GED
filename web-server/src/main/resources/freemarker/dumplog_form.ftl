<#include "base.ftl">

<#macro page_body>
<form class="tab-pane" id="dumpLogForm" role="form" action="/api/trade/command/dumplog" method="get">
    <div class="form-group">
        <label for="user">Username:</label>
        <input name="userId" type="text" class="form-control col-md-4" id="userId">
    </div>
    <div class="form-group">
        <label for="user">Filename:</label>
        <input name="filename" type="text" class="form-control col-md-4" id="filename">
    </div>
    <button id="commandSubmit" type="button" class="btn btn-default">Submit</button>
</form>
</#macro>

<@display_page/>