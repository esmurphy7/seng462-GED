<#include "base.ftl">

<#macro page_body>
    <h1>Request Quote</h1>
    <form role="form">
        <div class="form-group">
            <label for="user">Username:</label>
            <input type="text" class="form-control" id="user">
        </div>
        <div class="form-group">
            <label for="stockSymbol">Stock Symbol:</label>
            <input type="text" class="form-control" id="stockSymbol">
        </div>
        <button type="submit" class="btn btn-default">Submit</button>
    </form>
</#macro>

<@display_page/>