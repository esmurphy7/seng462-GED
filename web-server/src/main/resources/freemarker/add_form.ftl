<#include "base.ftl">

<#macro page_body>
    <form class="tab-pane" id="addForm" role="form" action="/api/trade/command/add" method="post">
        <div class="form-group">
            <label for="user">Username:</label>
            <input name="userId" type="text" class="form-control" id="userId">
        </div>
        <div class="form-group">
            <label for="amount">Amount:</label>
            <input name="amount" type="text" class="form-control" id="amount">
        </div>
        <button id="commandSubmit" type="button" class="btn btn-default">Submit</button>
    </form>
</#macro>

<@display_page/>