
<#macro page_head>
    <title>Day Trading Web Client</title>
    <link rel='stylesheet' href='/webjars/bootstrap/3.3.5/css/bootstrap.min.css'>
    <link rel='stylesheet' href='/webjars/bootstrap/3.3.5/css/bootstrap-theme.min.css'>
    <script type='text/javascript' src='/webjars/jquery/1.11.1/jquery.min.js'></script>
    <script type='text/javascript' src='/webjars/bootstrap/3.3.5/js/bootstrap.min.js'></script>
</#macro>

<#macro nav_bar>
    <nav class="navbar navbar-default">
        <div class="container">
            <div id="navbar" class="collapse navbar-collapse">
                <ul class="nav navbar-nav">
                </ul>
            </div>
        </div>
    </nav>
</#macro>

<#macro page_body>
    <p>Page Body</p>
</#macro>

<#macro username_form_input>
<div class="form-group">
    <label for="user">Username:</label>
    <input name="userId" type="text" class="form-control col-md-4" id="userId">
</div>
</#macro>

<#macro stocksymbol_form_input>
<div class="form-group">
    <label for="user">Username:</label>
    <input name="userId" type="text" class="form-control col-md-4" id="userId">
</div>
</#macro>

<#macro amount_form_input>
<div class="form-group">
    <label for="user">Username:</label>
    <input name="userId" type="text" class="form-control col-md-4" id="userId">
</div>
</#macro>

<#macro filename_form_input>
<div class="form-group">
    <label for="user">Username:</label>
    <input name="userId" type="text" class="form-control col-md-4" id="userId">
</div>
</#macro>



<#macro display_page>
    <!doctype html>
    <html>
        <head>
            <@page_head/>
        </head>

        <@nav_bar/>

        <body>
            <div class="dropdown" id="commandDropdown">
                <button class="btn btn-default dropdown-toggle" type="button" data-toggle="dropdown">Select Command
                    <span class="caret"></span>
                </button>
                <ul class="dropdown-menu" id="commandDropdownMenu">
                    <li><a href="/api/trade/command/forms/add">ADD</a></li>
                    <li><a href="/api/trade/command/forms/quote">QUOTE</a></li>
                    <li><a href="/api/trade/command/forms/buy">BUY</a></li>
                    <li><a href="/api/trade/command/forms/buy/commit">COMMIT_BUY</a></li>
                    <li><a href="/api/trade/command/forms/buy/cancel">CANCEL_BUY</a></li>
                    <li><a href="/api/trade/command/forms/buy/trigger/amount">SET_BUY_AMOUNT</a></li>
                    <li><a href="/api/trade/command/forms/buy/trigger/stockprice">SET_BUY_TRIGGER</a></li>
                    <li><a href="/api/trade/command/forms/buy/trigger/cancel">CANCEL_SET_BUY</a></li>
                    <li><a href="/api/trade/command/forms/sell">SELL</a></li>
                    <li><a href="/api/trade/command/forms/sell/commit">COMMIT_SELL</a></li>
                    <li><a href="/api/trade/command/forms/sell/cancel">CANCEL_SELL</a></li>
                    <li><a href="/api/trade/command/forms/sell/trigger/amount">SET_SELL_AMOUNT</a></li>
                    <li><a href="/api/trade/command/forms/sell/trigger/stockprice">SET_SELL_TRIGGER</a></li>
                    <li><a href="/api/trade/command/forms/sell/trigger/cancel">CANCEL_SET_SELL</a></li>
                    <li><a href="/api/trade/command/forms/summary">DISPLAY_SUMMARY</a></li>
                    <li><a href="/api/trade/command/forms/dumplog">DUMPLOG</a></li>
                </ul>
            </div>

            <script type="text/javascript">
                $(document).ready(function(){
                    $('#commandForm').find('#commandSubmit').on('click', function(event)
                    {
                        var form = event.target.closest("form");
                        var formAction = form.action;
                        var formMethod = form.method;

                        // serialize form fields into query parameters
                        var queryParams = $('#commandForm').find('#'+form.id).serialize();

                        // build the target endpoint
                        var endpoint = formAction + "?" + queryParams;

                        // execute the ajax request
                        $.ajax({
                            url: endpoint,
                            type: formMethod,
                            data: {},
                            dataType: "json",
                            success: function (response) {
                                console.log(response);
                                var responseHTML = '';

                                $.each(response, function(key, val)
                                {
                                    console.log(key + val);
                                    // if error is found, render it and stop
                                    if(key == "errorMsg" && val != "")
                                    {
                                        $('#commandResponse').html("<h4>"+key+" = "+val+"</>");
                                        return;
                                    }
                                    // only render non-zero values
                                    if(val != 0 && val != "")
                                    {
                                        responseHTML += "<h4>"+key+" = "+val+"</>";
                                    }
                                });
                                $('#commandResponse').html(responseHTML);
                            },
                            error: function (response) {
                                console.log("Ajax failed");
                                console.log(response);
                                console.log(response.badfieldname);

                                var responseHTML = "<h4>"+"Status text: "+response.statusText+"</>";
                                responseHTML += "<h4>"+"Status code :"+response.status+"</>";
                                responseHTML += "<h4>"+"Response text: "+response.responseText+"</>";

                                $('#commandResponse').html(responseHTML);
                            }
                        });
                    });
                });
            </script>

            <div class="row col-md-4">
                <div id="commandForm">
                    <@page_body/>
                </div>

                <div id="commandResponse">

                </div>
            </div>
        </body>
    </html>
</#macro>