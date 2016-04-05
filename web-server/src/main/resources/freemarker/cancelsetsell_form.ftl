<#include "base.ftl">

<#macro page_body>
<form class="tab-pane" id="cancelSetSellForm" role="form" action="/api/trade/command/sell/trigger/cancel" method="post">
    <div class="form-group">
        <label for="user">Username:</label>
        <input name="userId" type="text" class="form-control col-md-4" id="userId">
    </div>
    <div class="form-group">
        <label for="stockSymbol">Stock Symbol:</label>
        <input name="stockSymbol" type="text" class="form-control" id="stockSymbol">
    </div>
    <button id="commandSubmit" type="button" class="btn btn-default">Submit</button>
</form>
</#macro>

<@display_page/>