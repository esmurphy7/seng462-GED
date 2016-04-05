<#include "base.ftl">

<#macro page_body>
<form class="tab-pane" id="commitBuyForm" role="form" action="/api/trade/command/buy/commit" method="post">
    <div class="form-group">
        <label for="user">Username:</label>
        <input name="userId" type="text" class="form-control col-md-4" id="userId">
    </div>
    <button id="commandSubmit" type="button" class="btn btn-default">Submit</button>
</form>
</#macro>

<@display_page/>