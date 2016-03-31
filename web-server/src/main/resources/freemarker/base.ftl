
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
                    <li>One</li>
                    <li>Two</li>
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

            <!--<script type="text/javascript">
                $(document).ready(function(){
                    // Dropdown selection handler: Get html form that corresponds to the command selected
                    $("#commandDropdownMenu").find("li").click(function () {
                        var selectedText = $(this).text();

                        // update the dropdown's text to display the selected command
                        $('#commandDropdown').find('.dropdown-toggle').html(selectedText + '<span class="caret"></span>')

                        // build ajax request to get html form
                        var formUrl = null;
                        switch (selectedText)
                        {
                            case "ADD":
                                break;
                            case "QUOTE":
                                formUrl = "/api/trade/command/forms/quote";
                                break;
                            default:
                                formUrl = null;
                                break;
                        }

                        // send ajax request and display retrieved form
                        if(formUrl != null)
                        {
                            $.get(
                                    formUrl,
                                    function(form){
                                        $('#commandForm').html(form);
                                    }
                            );
                        }
                        else
                        {
                            $('#commandForm').html('');
                        }
                    });
                });
            </script>

            <script type="text/javascript">
                $(document).ready(function(){
                    $('#commandForm').on('submit', '.tab-pane', function(event){
                        var formId = event.target.id;
                        alert(formId);
                        // serialize the form into query parameters
                        var str = $('#commandForm').find('#'+formId).serialize();
                        alert(str);
                    });
                });
            </script>
            -->
            <div id="commandForm">

            </div>

            <@page_body/>
        </body>
    </html>
</#macro>