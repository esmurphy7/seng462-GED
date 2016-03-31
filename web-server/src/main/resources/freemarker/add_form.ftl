<#include "base.ftl">

<#macro page_body>
<form class="tab-pane" id="quoteForm" role="form" action="/api/trade/command/add" method="post">
    <div class="form-group">
        <label for="user">Username:</label>
        <input name="userId" type="text" class="form-control" id="user">
    </div>
    <div class="form-group">
        <label for="amount">Amount:</label>
        <input name="amount" type="text" class="form-control" id="stockSymbol">
    </div>
    <button id="commandSubmit" type="submit" class="btn btn-default">Submit</button>
</form>
</#macro>

<@display_page/>