
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
                            dataType: "html",
                            success: function (response) {
                                console.log(response);
                                var responseHTML = response;
                                /*
                                $.each(response, function(key, val)
                                {
                                    console.log(key + val);
                                    responseHTML += "<h3>"+key+" = "+val+"</h3>";
                                });
                                */
                            $('#commandResponse').html(responseHTML);
                            },
                            error: function (response) {
                                alert('ajax failed');
                                // ajax error callback
                            }
                        });
                    });
                });
            </script>

            <div id="commandForm">
                <@page_body/>
            </div>

            <div id="commandResponse">
                <h3>Command Response</h3>
            </div>

        </body>
    </html>
</#macro>