<#include "base.ftl">

<#macro page_body>
<form class="tab-pane" id="buyForm" role="form" action="/api/trade/command/buy" method="post">
    <div class="form-group">
        <label for="user">Username:</label>
        <input name="userId" type="text" class="form-control col-md-4" id="userId">
    </div>
    <div class="form-group">
        <label for="stockSymbol">Stock Symbol:</label>
        <input name="stockSymbol" type="text" class="form-control" id="stockSymbol">
    </div>
    <div class="form-group">
        <label for="amount">Amount:</label>
        <input name="amount" type="text" class="form-control col-md-4" id="amount">
    </div>
    <button id="commandSubmit" type="button" class="btn btn-default">Submit</button>
</form>
</#macro>

<@display_page/>