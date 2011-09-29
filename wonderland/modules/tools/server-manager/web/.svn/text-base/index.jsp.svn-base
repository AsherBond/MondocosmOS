<%-- 
    Document   : index
    Created on : Oct 20, 2008, 3:53:50 PM
    Author     : jkaplan
--%>

<%@page contentType="text/html" pageEncoding="MacRoman"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <link href="/wonderland-web-front/css/base.css" rel="stylesheet" type="text/css" media="screen" />
        <link href="/wonderland-web-front/css/module.css" rel="stylesheet" type="text/css" media="screen" />
        <script src="/wonderland-web-front/javascript/prototype-1.6.0.3.js" type="text/javascript"></script>
        <script type="text/javascript" src="http://www.google.com/jsapi"></script>
        <script type="text/javascript">
            google.load("visualization", "1", {packages:["annotatedtimeline"]});
            google.setOnLoadCallback(startUpdates);

            var pe;
            var data;
            var last = 0;

            function startUpdates() {
                data = new google.visualization.DataTable();
                data.addColumn('date', 'Date');
                data.addColumn('number', 'Ping Time');
                data.addColumn('string', 'title1');
                data.addColumn('string', 'text1');
         
                updateChartData();
                setUpdatePeriod(15);
            }
      
            function updateChartData() {
                new Ajax.Request('resources/pingData?after=' + last, {
                    method:'get',
                    requestHeaders: { Accept:'application/json' },
                    onSuccess: function(response){
                        var data = response.responseText.evalJSON(true);
                        updateLast(data.pingData);
                        updateChart(data.pingData);
                    }
                });
            }
    
            function updateLast(pingData) {
                last = pingData[pingData.length - 1].sampleDate;
            }
    
            function updateChart(pingData) {
                var lastRow = data.addRows(pingData.length);
                var firstRow = lastRow - pingData.length + 1;
        
                for (var i = 0; i < pingData.length; i++) {
                    var idx = firstRow + i;
            
                    // add date
                    var d = new Date();
                    d.setTime(pingData[i].sampleDate);
                    data.setValue(idx, 0, d);
            
                    // add ping time
                    var n = parseInt(pingData[i].pingTime);
                    if (n > 0) {
                        data.setValue(idx, 1, n);
                    }
            
                    // add note if any
                    if (pingData[i].pingNoteText != null) {
                        data.setValue(idx, 3, pingData[i].pingNoteText);
                    }
                    if (pingData[i].pingNoteTitle != null) {
                        data.setValue(idx, 2, pingData[i].pingNoteTitle);
                    }
                }
        
                var chart = new google.visualization.AnnotatedTimeLine(document.getElementById('chart_div'));
                chart.draw(data, {displayAnnotations: true});
            }
    
            function setUpdatePeriod(period) {
                if (pe) { pe.stop(); }
                if (period > 0) {
                    pe = new PeriodicalExecuter(updateChartData, period);
                }
    
                // clear the list
                $('periods').update("refresh:");
        
                var times = [0, 1, 5, 15, 30, 60];
                for (var i = 0; i < times.length; i++) {
                    var timeStr = times[i] + " sec.";
                    if (times[i] == 0) {
                        timeStr = "none";
                    }
            
                    if (times[i] == period) {
                        $('periods').insert(timeStr);
                    } else {
                        $('periods').insert(new Element('a', { 'href': 'javascript:void(0);',
                            'onclick' : 'setUpdatePeriod(' + times[i] +')'}
                    ).update(timeStr));
                    }
            
                    $('periods').insert(' ');
                }
            }
        </script>
    </head>


    <body>
        <h1>Server Performance</h1>

        <div id="periods"></div>

        <h3>Ping Times</h3>
        <div id="chart_div" style="width: 700px; height: 240px;"></div>
    </body>
</html>
