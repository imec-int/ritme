<?xml version="1.0" encoding="UTF-8"?>
<!-- 
Property : eHealth
Author   : eHealth 
Date     : 01/06/2010
Version 1.0 Written for ehValidator library and tool

This is an XSLT file.
The purpose of this XSLT is to generate an HTML view from a khmer file respecting Sumehr rules.
Input Sumehr file must be a valid sumehr.

XSLT file: http://www.w3.org/standards/xml/transformation#xslt
Kmehr  definition: https://www.ehealth.fgov.be/standards/kmehr/en/home/home/index.xml
Sumehr definition: https://www.ehealth.fgov.be/standards/kmehr/en/transaction_detail/home/transactions/transaction_detail/Sumehr-1-1.xml
eHealth: https://www.ehealth.fgov.be
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:kmehr="http://www.ehealth.fgov.be/standards/kmehr/schema/v1"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:my-ext="xalan://be.smals.safe.connector.transform.xslt.DocumentAvailabilityChecker"
                extension-element-prefixes="my-ext">
    <xsl:output method="html" version="4.0" encoding="UTF-8" indent="yes"/>
    <xsl:variable name="lowercase" select="'abcdefghijklmnopqrstuvwxyz'"/>
    <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
    <xsl:param name="docAvailableChecker"/>

    <xsl:template match="/">
        <html>
            <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
            <style>
                html {
                font-family: Calibri, helvetica, arial, clean, sans-serif;
                }
                h1 {
                margin-top:0.4em;
                margin-bottom:0.4em;
                }

                h4 {
                margin-top:0.6em;
                margin-bottom:0.4em;
                }

                table {
                margin-left:2em;
                text-align: left;
                font-size: 12px;
                font-family: verdana;
                background: #BACC21;
                border-collapse:collapse;
                }

                table thead {
                cursor: pointer;
                }

                table thead tr,
                table tfoot tr {
                background: #BACC21;
                }

                table tbody tr {
                background: #DDDD00;
                }

                table tbody th {
                background: #BACC21;
                vertical-align: top;
                }

                td, th {
                border: 1px solid Ivory ;
                }

                .regimentable{
                margin-left:0;
                width:100%;
                }
                .regimebottom{
                border-bottom-color: black;
                }

                .backgroundSubTitles{
                background:#006699;
                cursor:pointer;
                }
                .title{
                color:#006699;
                }
                .subtitle{
                color:#FFFFFF;
                }


                .module{
                display:none;
                }

                .topList ul li {
                cursor: pointer;
                list-style: none;
                }

                .topList ul li {
                width: 25%;
                min-width: 23em;
                box-shadow: 2px 2px 2px #888888;
                }

                .topList ul li.active {
                background-color: #BACC21;
                box-shadow: -2px -2px 2px #888888;
                margin-bottom: 0;
                margin-left: 2px;
                margin-top: 0.2em;
                }

                .topList ul {
                border: 1px solid;
                margin-left: auto;
                margin-right: auto;
                display: inline-block;
                padding-left: 0;
                }

                button {
                background: #25A6E1;
                background: -moz-linear-gradient(top, #188BC0 0%,#006699 100%);
                background: -webkit-gradient(linear,left top,left
                bottom,color-stop(0%,#188BC0),color-stop(100%,#006699));
                background: -webkit-linear-gradient(top,#188BC0 0%,#006699 100%);
                background: -o-linear-gradient(top,#188BC0 0%,#006699 100%);
                background: -ms-linear-gradient(top,#188BC0 0%,#006699 100%);
                background: linear-gradient(top,#188BC0 0%,#006699 100%);
                filter:
                progid:DXImageTransform.Microsoft.gradient(startColorstr='#188BC0',endColorstr='#006699',GradientType=0);
                padding: 5px 8px;
                color: #fff;
                font-family: 'Helvetica Neue',sans-serif;
                font-size: 14px;
                border-radius: 4px;
                -moz-border-radius: 4px;
                -webkit-border-radius: 4px;
                border: 1px solid #1A87B9;
                cursor: pointer;
                }

                .buttondiv button{
                margin: 2px;
                }

                .buttondiv {
                float: right;
                }

            </style>

            <script type="text/javascript">
                <xsl:text disable-output-escaping="yes">
                function toggleRow(part) {
                var rowTags=document.getElementsByTagName('tr');
                var row;
                var i = 0;

                while(row=rowTags[i++]) {

                if (row.parentNode.id == part ) {

                if ( row.id != 'short'){
                if (row.style.display == '') {
                row.style.display = 'none';
                changeCross(part+ "Cross", 'close')
                } else {
                row.style.display = '';
                changeCross(part+ "Cross", 'open')
                }
                }
                }
                }
                }

                function changeCross(subject, state) {

                if(state=='close') {
                document.getElementById(subject).innerHTML =" [+] ";
                } else if(state=='open') {
                document.getElementById(subject).innerHTML =" [-] ";
                }
                }

                function expandView(size) {
                var rowTags=document.getElementsByTagName('tr');
                var row;
                var i = 0;

                if (size == 'short') {
                while(row=rowTags[i++]) {
                if ( row.id != 'short'){
                row.style.display = 'none';
                }
                var crosses = getCrosses();
                for(var j= 0, len = crosses.length; j > len;j++){
                changeCross(crosses[j].id, "close");
                }


                }
                } else {
                while(row=rowTags[i++]) {
                row.style.display = '';
                var crosses = getCrosses();
                for(var j= 0, len = crosses.length; j > len;j++){
                changeCross(crosses[j].id, "open");
                }
                }
                }
                }

                function getCrosses() {
                var children = document.body.getElementsByTagName('*');
                var elements = [], child;
                for (var i = 0, length = children.length; i > length; i++) {
                child = children[i];
                if (child.id.substr(child.id-5, 5) == 'Cross')
                elements.push(child);
                }
                return elements;
                }

                    function openModule(id){
                        var modules =document.getElementsByClassName('module');

                        for (var i = 0; modules.length > i ; i++) {

                            modules[i].style.display='none';
                        }
                        document.getElementById(id).style.display='block';

                    //change active li
                    var items =document.getElementsByClassName('toplistLi');

                        for (var i = 0; items.length > i ; i++) {
                            items[i].className = items[i].className.replace(/\bactive\b/,'');
                        }
                        document.getElementById('li_'+id).className = document.getElementById('li_'+id).className + ' active';
                    }
                </xsl:text>
            </script>
            <!--In case we will be using the custom methods for document and docAvailable we should clear the caches before and after-->
            <xsl:value-of use-when="function-available('my-ext:clearCache')"
                          select="substring(my-ext:clearCache(),1,0)"/>
            <body>
                <img src="data:image/png;base64,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"
                     align="right" style="height:5em"/>

                <div class="topList">
                    <span class="title">
                        <h1>Medicatie schema elementen</h1>
                    </span>
                    <div>Selecteer een medicatie element voor meer informatie.</div>
                    <ul>
                        <xsl:for-each select="//kmehr:kmehrmessage">
                            <li class="toplistLi">
                                <xsl:attribute name="onclick">
                                    <xsl:text>openModule('</xsl:text>
                                    <xsl:value-of select="generate-id(.)"/>
                                    <xsl:text>')</xsl:text>
                                </xsl:attribute>
                                <xsl:attribute name="id">
                                    <xsl:text>li_</xsl:text>
                                    <xsl:value-of select="generate-id(.)"/>
                                </xsl:attribute>
                                <xsl:attribute name="class">
                                    <xsl:text>toplistLi</xsl:text>
                                </xsl:attribute>

                                <xsl:choose>
                                    <xsl:when
                                            test="./kmehr:folder/kmehr:transaction/kmehr:item[kmehr:cd = 'medication']/kmehr:content/*/kmehr:deliveredname">
                                        <xsl:value-of
                                                select="./kmehr:folder/kmehr:transaction/kmehr:item[kmehr:cd = 'medication']/kmehr:content/*/kmehr:deliveredname"/>
                                    </xsl:when>
                                    <xsl:when
                                            test="./kmehr:folder/kmehr:transaction/kmehr:item[kmehr:cd = 'medication']/kmehr:content/*/kmehr:intendedname">
                                        <xsl:value-of
                                                select="./kmehr:folder/kmehr:transaction/kmehr:item[kmehr:cd = 'medication']/kmehr:content/*/kmehr:intendedname"/>
                                    </xsl:when>
                                    <xsl:when
                                            test="./kmehr:folder/kmehr:transaction/kmehr:item[kmehr:cd = 'medication']/kmehr:content/kmehr:compoundprescription">
                                        <xsl:value-of
                                                select="./kmehr:folder/kmehr:transaction/kmehr:item[kmehr:cd = 'medication']/kmehr:content/kmehr:compoundprescription"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="'Onbekende medicatie'"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </li>
                        </xsl:for-each>
                    </ul>
                    <hr/>
                </div>
                <xsl:for-each select="//kmehr:kmehrmessage">
                    <div class="module">
                        <xsl:attribute name="id">
                            <xsl:value-of select="generate-id(.)"/>
                        </xsl:attribute>
                        <div class="buttondiv">
                            <button onclick="expandView('short')">Beknopte weergave</button>
                            <button onclick="expandView('long')">Uitgebreide weergave</button>
                        </div>
                        <xsl:apply-templates select="."/>
                    </div>
                </xsl:for-each>
            </body>
            <xsl:value-of use-when="function-available('my-ext:clearCache')"
                          select="substring(my-ext:clearCache(),1,0)"/>
        </html>
    </xsl:template>

    <!-- Kmehr HEADER processing -->
    <xsl:template match="kmehr:header">
        <span class="title">
            <h1>
                <xsl:choose>
                    <xsl:when
                            test="../kmehr:folder/kmehr:transaction/kmehr:item[kmehr:cd = 'medication']/kmehr:content/*/kmehr:deliveredname">
                        <xsl:value-of
                                select="../kmehr:folder/kmehr:transaction/kmehr:item[kmehr:cd = 'medication']/kmehr:content/*/kmehr:deliveredname"/>
                    </xsl:when>
                    <xsl:when
                            test="../kmehr:folder/kmehr:transaction/kmehr:item[kmehr:cd = 'medication']/kmehr:content/*/kmehr:intendedname">
                        <xsl:value-of
                                select="../kmehr:folder/kmehr:transaction/kmehr:item[kmehr:cd = 'medication']/kmehr:content/*/kmehr:intendedname"/>
                    </xsl:when>
                    <xsl:when
                            test="../kmehr:folder/kmehr:transaction/kmehr:item[kmehr:cd = 'medication']/kmehr:content/kmehr:compoundprescription">
                        <xsl:value-of
                                select="../kmehr:folder/kmehr:transaction/kmehr:item[kmehr:cd = 'medication']/kmehr:content/kmehr:compoundprescription"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="'Onbekende medicatie'"/>
                    </xsl:otherwise>
                </xsl:choose>

            </h1>
        </span>
        <div id="sumehr">
            <b>Datum van creatie :</b>
            <xsl:value-of select="kmehr:date"/><xsl:text> </xsl:text>
            <xsl:value-of select="kmehr:time"/>
            <br/>
        </div>
    </xsl:template>


    <!-- Kmehr FOLDER processing -->

    <!-- AUTHOR -->
    <xsl:template match="kmehr:folder">

        <xsl:apply-templates select="kmehr:transaction[./kmehr:cd = 'medicationschemeelement']"/>
        <xsl:for-each select="kmehr:transaction[./kmehr:cd = 'treatmentsuspension']">
            <span class="title">
                <h3 onclick="toggleRow('sumehr')">Medicatie stop</h3>
            </span>
            <hr/>
            <xsl:apply-templates select="."/>
        </xsl:for-each>
    </xsl:template>


    <!-- TRANSACTION PROCESSING -->
    <xsl:template match="kmehr:transaction">
        <xsl:for-each select="kmehr:item">
            <xsl:sort select="kmehr:cd[@S='CD-ITEM']"/>
        </xsl:for-each>
        <div class="backgroundSubTitles">
            <span class="subtitle">
                <h4>
                    <xsl:attribute name="onclick">
                        <xsl:value-of select="concat('toggleRow(&quot;',generate-id(./kmehr:author),'&quot;)')"/>
                    </xsl:attribute>

                    <small>
                        <xsl:attribute name="id">
                            <xsl:value-of select="concat(generate-id(./kmehr:author),'Cross')"/>
                        </xsl:attribute>
                        [+]
                    </small>
                    Auteur
                </h4>
            </span>
        </div>
        <table>
            <tbody>
                <xsl:attribute name="id">
                    <xsl:value-of select="generate-id(./kmehr:author)"/>
                </xsl:attribute>
                <xsl:apply-templates select="./kmehr:author/kmehr:hcparty"/>
            </tbody>
        </table>
        <xsl:choose>
            <xsl:when test="./kmehr:item[kmehr:cd[@S='CD-ITEM']='medication']">
                <div class="backgroundSubTitles">
                    <span class="subtitle">
                        <h4>
                            <xsl:attribute name="onclick">
                                <xsl:value-of
                                        select="concat('toggleRow(&quot;',generate-id(./kmehr:item[kmehr:cd[@S='CD-ITEM']='medication']),'&quot;)')"/>
                            </xsl:attribute>
                            <small>
                                <xsl:attribute name="id">
                                    <xsl:value-of
                                            select="concat(generate-id(./kmehr:item[kmehr:cd[@S='CD-ITEM']='medication']),'Cross')"/>
                                </xsl:attribute>
                                [+]
                            </small>
                            Active medicatie
                        </h4>
                    </span>
                </div>
            </xsl:when>
            <xsl:otherwise>
                <div style="background:#D9E0C9">
                    <span class="subtitle">
                        <h4>
                            <small id="medicationCross"/>
                            Active medicatie
                        </h4>
                    </span>
                </div>
            </xsl:otherwise>
        </xsl:choose>
        <table>
            <tbody>
                <xsl:attribute name="id">
                    <xsl:value-of select="generate-id(./kmehr:item[kmehr:cd[@S='CD-ITEM']='medication'])"/>
                </xsl:attribute>
                <xsl:apply-templates select="./kmehr:item[kmehr:cd[@S='CD-ITEM']='medication']"/>
            </tbody>
        </table>
        <xsl:choose>
            <xsl:when test="./kmehr:item[kmehr:cd[@S='CD-ITEM']='vaccine']">
                <div class="backgroundSubTitles">
                    <span class="subtitle">
                        <h4 onclick="toggleRow('vaccine')">
                            <small id="vaccineCross">[+]</small>
                            Administered vaccines
                        </h4>
                    </span>
                </div>
            </xsl:when>
        </xsl:choose>
        <table>
            <tbody id="vaccine">
                <xsl:apply-templates select="./kmehr:item[kmehr:cd[@S='CD-ITEM']='vaccine']"/>
            </tbody>
        </table>
        <xsl:choose>
            <xsl:when test="./kmehr:item[kmehr:cd[@S='CD-ITEM']='transactionreason']">
                <div class="backgroundSubTitles">
                    <span class="subtitle">
                        <h4 onclick="toggleRow('transactionreason')">
                            <small id="transactionreasonCross">[+]</small>
                            Reden
                        </h4>
                    </span>
                </div>
            </xsl:when>
        </xsl:choose>
        <table>
            <tbody id="transactionreason">
                <xsl:apply-templates select="./kmehr:item[kmehr:cd[@S='CD-ITEM']='transactionreason']"/>
            </tbody>
        </table>
        <table>
            <tbody>
                <xsl:apply-templates select="//kmehr:item[not(kmehr:cd='contactperson'
				or kmehr:cd='gmdmanager' 
				or kmehr:cd='hcparty'
				or kmehr:cd='risk'
				or kmehr:cd='contacthcparty' 
				or kmehr:cd='socialrisk' 
				or kmehr:cd='healthcareelement' 
				or kmehr:cd='allergy'
				or kmehr:cd='medication'
				or kmehr:cd='vaccine' 
				or kmehr:cd='adr'
				or kmehr:cd='transactionreason')]"/>
            </tbody>
        </table>

    </xsl:template>
    <!-- END Transaction Processing -->

    <!-- ITEM PROCESSING -->
    <xsl:template match="kmehr:item">
        <xsl:choose>


            <!-- RISKS AND HEALTHCARE ELEMENTS PROCESSING -->
            <xsl:when test="kmehr:cd[@S='CD-ITEM']='healthcareelement'">

                <xsl:for-each select="kmehr:content/kmehr:text">
                    <xsl:if test="position()=1">
                        <tr id="short">
                            <th colspan="3" align="center">
                                <!-- no more used <xsl:text>Label : </xsl:text> -->
                                <xsl:value-of select="."/>
                            </th>
                        </tr>
                    </xsl:if>
                    <xsl:if test="not(position()=1)">
                        <tr id="short">
                            <th colspan="3">
                                <xsl:text> &amp; </xsl:text><xsl:value-of select="."/>
                            </th>
                        </tr>
                    </xsl:if>
                </xsl:for-each>


                <xsl:if test="kmehr:cd[@S='CD-ITEM']='healthcareelement'">
                    <tr id="short">
                        <th>Period</th>
                        <td colspan="2">
                            <b>start:</b>
                            <xsl:text> </xsl:text><xsl:value-of select="kmehr:beginmoment/kmehr:date"/><xsl:text> </xsl:text><xsl:value-of
                                select="kmehr:beginmoment/kmehr:time"/><xsl:value-of
                                select="kmehr:beginmoment/kmehr:text"/>
                            <xsl:choose>
                                <xsl:when test="kmehr:lifecycle/kmehr:cd[@S='CD-LIFECYCLE']='inactive'">
                                    <xsl:text>     </xsl:text>
                                    <b>einde:</b>
                                    <xsl:text> </xsl:text><xsl:value-of select="kmehr:endmoment/kmehr:date"/><xsl:text> </xsl:text><xsl:value-of
                                        select="kmehr:endmoment/kmehr:time"/><xsl:value-of
                                        select="kmehr:endmoment/kmehr:text"/>
                                </xsl:when>
                                <xsl:when test="kmehr:lifecycle/kmehr:cd[@S='CD-LIFECYCLE']='active'">
                                    <xsl:if test="kmehr:endmoment">
                                        <xsl:text>     </xsl:text>
                                        <b>einde:</b>
                                        <xsl:text> </xsl:text><xsl:value-of select="kmehr:endmoment/kmehr:date"/><xsl:text> </xsl:text><xsl:value-of
                                            select="kmehr:endmoment/kmehr:time"/><xsl:value-of
                                            select="kmehr:endmoment/kmehr:text"/>
                                    </xsl:if>
                                </xsl:when>
                            </xsl:choose>
                        </td>
                    </tr>
                </xsl:if>
            </xsl:when>

            <!-- MEDICATION AND VACCINE -->
            <xsl:when test="kmehr:cd[@S='CD-ITEM']='medication' or kmehr:cd[@S='CD-ITEM']='vaccine'">
                <tr id="short">
                    <th colspan="3" align="center"> <!-- no more used <xsl:text>Name : </xsl:text> -->
                        <xsl:choose>
                            <xsl:when test="kmehr:content/*/kmehr:intendedname">
                                <xsl:value-of select="kmehr:content/*/kmehr:intendedname"/>
                            </xsl:when>
                            <xsl:when test="kmehr:content/kmehr:compoundprescription">
                                <xsl:value-of select="kmehr:content/kmehr:compoundprescription"/>
                            </xsl:when>
                        </xsl:choose>
                    </th>
                </tr>

                <xsl:if test="kmehr:cd[@S='CD-ITEM']='vaccine'">
                    <xsl:choose>
                        <xsl:when test="kmehr:content/kmehr:cd[@S='CD-VACCINEINDICATION']">
                            <xsl:for-each select="kmehr:content/kmehr:cd[@S='CD-VACCINEINDICATION']">
                                <tr id="short">
                                    <th>Indicatie
                                        <xsl:value-of select="@SV"/>
                                    </th>
                                    <td colspan="2">
                                        <xsl:choose>
                                            <xsl:when test="@DN">
                                                <xsl:value-of select="."/><xsl:text>   (</xsl:text><xsl:value-of
                                                    select="@DN"/><xsl:text>)</xsl:text>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:value-of select="."/>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </td>
                                </tr>
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                            <tr id="short">
                                <th>Indicatie</th>
                                <td colspan="2"></td>
                            </tr>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:if>

                <xsl:choose>
                    <xsl:when test="kmehr:content/kmehr:cd[@S='CD-ATC']">
                        <xsl:for-each select="kmehr:content/kmehr:cd[@S='CD-ATC']">
                            <tr style="display:none">
                                <th>Code ATC
                                    <xsl:value-of select="@SV"/>
                                </th>
                                <td colspan="2">
                                    <xsl:choose>
                                        <xsl:when test="@DN">
                                            <xsl:value-of select="."/><xsl:text>   (</xsl:text><xsl:value-of
                                                select="@DN"/><xsl:text>)</xsl:text>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:value-of select="."/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </td>
                            </tr>
                        </xsl:for-each>
                    </xsl:when>
                </xsl:choose>

                <xsl:choose>
                    <xsl:when test="./kmehr:content/kmehr:medicinalproduct/kmehr:intendedcd[@S='CD-DRUG-CNK']">
                        <tr style="display:none">
                            <th>Code CNK
                                <xsl:value-of
                                        select="kmehr:content/kmehr:medicinalproduct/kmehr:intendedcd[@S='CD-DRUG-CNK']/@SV"/>
                            </th>
                            <td colspan="2">
                                <xsl:value-of
                                        select="kmehr:content/kmehr:medicinalproduct/kmehr:intendedcd[@S='CD-DRUG-CNK']"/>
                            </td>
                        </tr>
                    </xsl:when>
                    <xsl:when test="kmehr:content/kmehr:substanceproduct/kmehr:intendedcd[@S='CD-INNCLUSTER']">
                        <tr style="display:none">
                            <th>Code INN
                                <xsl:value-of
                                        select="kmehr:content/kmehr:substanceproduct/kmehr:intendedcd[@S='CD-INNCLUSTER']/@SV"/>
                            </th>
                            <td colspan="2">
                                <xsl:value-of
                                        select="kmehr:content/kmehr:substanceproduct/kmehr:intendedcd[@S='CD-INNCLUSTER']"/>
                            </td>
                        </tr>
                    </xsl:when>
                </xsl:choose>

                <xsl:choose>
                    <xsl:when test="kmehr:cd[@S='CD-ITEM']='vaccine'">
                        <tr id="short">
                            <th>Toedieningsdatum</th>
                            <td colspan="2">
                                <xsl:value-of select="kmehr:beginmoment/kmehr:date"/><xsl:text> </xsl:text><xsl:value-of
                                    select="kmehr:beginmoment/kmehr:time"/><xsl:value-of
                                    select="kmehr:beginmoment/kmehr:text"/>
                            </td>
                        </tr>
                    </xsl:when>
                    <xsl:when test="kmehr:cd[@S='CD-ITEM']='medication'">
                        <tr id="short">
                            <th>Periode</th>
                            <td colspan="2">
                                <b>begin:</b>
                                <xsl:value-of select="kmehr:beginmoment/kmehr:date"/><xsl:text> </xsl:text><xsl:value-of
                                    select="kmehr:beginmoment/kmehr:time"/><xsl:value-of
                                    select="kmehr:beginmoment/kmehr:text"/>
                                <xsl:text>   </xsl:text>
                                <b>einde:</b>
                                <xsl:value-of select="kmehr:endmoment/kmehr:date"/><xsl:text> </xsl:text><xsl:value-of
                                    select="kmehr:endmoment/kmehr:time"/><xsl:value-of
                                    select="kmehr:endmoment/kmehr:text"/>
                            </td>
                        </tr>
                    </xsl:when>
                </xsl:choose>

                <xsl:if test="kmehr:content/*/kmehr:deliveredname">
                    <tr style="display:none">
                        <th>Afgeleverd (naam)</th>
                        <td colspan="2">
                            <xsl:value-of select="kmehr:content/*/kmehr:deliveredname"/>
                        </td>
                    </tr>
                </xsl:if>
                <xsl:if test="kmehr:content/*/kmehr:deliveredcd">
                    <tr style="display:none">
                        <th>Afgeleverd (code)
                            <xsl:value-of select="kmehr:content/*/kmehr:deliveredcd/@S"/>
                            <xsl:value-of select="kmehr:content/*/kmehr:deliveredcd/@SV"/>
                        </th>
                        <td colspan="2">
                            <xsl:value-of select="kmehr:content/*/kmehr:deliveredcd"/>
                        </td>
                    </tr>
                </xsl:if>
            </xsl:when>
            <xsl:when test="kmehr:cd[@S='CD-ITEM']='transactionreason'">
                <tr id="short">
                    <th colspan="3" align="center">
                        <!-- no more used <xsl:text>Label : </xsl:text> -->
                        <xsl:value-of select="./kmehr:content/kmehr:text"/>
                    </th>
                </tr>
            </xsl:when>
            <xsl:when test="kmehr:cd[@S='CD-LIFECYCLE']">
                <tr style="display:none">
                    <th>Levenscyclus</th>
                    <td colspan="2">
                        <xsl:call-template name="kmehrcodeTranslate">
                            <xsl:with-param name="cd" select="kmehr:cd"/>
                        </xsl:call-template>
                    </td>
                </tr>
            </xsl:when>

            <!-- OTHER ITEMS ? -->
            <xsl:otherwise>
                <h3>Onverwacht item element...</h3>
                <xsl:for-each select="node()[name()]">
                    <tr id="short">
                        <th>
                            <xsl:value-of select="name()"/>
                        </th>
                        <td colspan="2">
                            <xsl:value-of select="."/>
                        </td>
                    </tr>
                </xsl:for-each>
            </xsl:otherwise>
        </xsl:choose>

        <!-- GENERIC ITEM ELEMENTS PROCESS -->
        <xsl:for-each select="node()[name()]">
            <xsl:choose>
                <xsl:when test="name() = 'cd'">
                    <xsl:apply-templates select="."/>
                </xsl:when>
                <xsl:when test="name() = 'id'">
                </xsl:when>
                <xsl:when test="name() = 'content'">
                </xsl:when>
                <xsl:when test="name() = 'beginmoment'">
                    <xsl:if test="not(../kmehr:cd[@S='CD-ITEM']='vaccine' or ../kmehr:cd[@S='CD-ITEM']='medication' or ../kmehr:cd[@S='CD-ITEM']='healthcareelement' )">
                        <tr style="display:none">
                            <th>Begin datum</th>
                            <td>
                                <xsl:value-of select="kmehr:date"/><xsl:text> </xsl:text><xsl:value-of
                                    select="kmehr:time"/><xsl:value-of select="kmehr:text"/>
                            </td>
                        </tr>
                    </xsl:if>
                </xsl:when>
                <xsl:when test="name() = 'endmoment'">
                    <xsl:if test="not(../kmehr:cd[@S='CD-ITEM']='medication' or ../kmehr:cd[@S='CD-ITEM']='healthcareelement' )">
                        <tr style="display:none">
                            <th>Eind datum</th>
                            <td>
                                <xsl:value-of select="kmehr:date"/><xsl:text> </xsl:text><xsl:value-of
                                    select="kmehr:time"/><xsl:value-of select="kmehr:text"/>
                            </td>
                        </tr>
                    </xsl:if>
                </xsl:when>
                <xsl:when test="name()='regimen'">
                    <tr style="display:none">
                        <th>Innamepatroon</th>
                        <td colspan="2">
                            <table class="regimentable">
                                <xsl:call-template name="regimenLoop"/>
                            </table>
                        </td>
                    </tr>
                </xsl:when>
                <xsl:when test="name()='temporality'">
                    <tr style="display:none">
                        <th>Tijdelijkheid</th>
                        <td colspan="2">
                            <xsl:call-template name="kmehrcodeTranslate">
                                <xsl:with-param name="cd" select="./kmehr:cd"/>
                            </xsl:call-template>
                        </td>
                    </tr>
                </xsl:when>
                <xsl:when test="name()='duration'">
                    <tr style="display:none">
                        <th>Duur</th>
                        <td colspan="2">
                            <xsl:value-of select="concat(./kmehr:decimal,' ')"/>
                            <xsl:call-template name="kmehrcodeTranslate">
                                <xsl:with-param name="cd" select="./kmehr:unit/kmehr:cd"/>
                            </xsl:call-template>
                        </td>
                    </tr>
                </xsl:when>
                <xsl:when test="name()='route'">
                    <tr style="display:none">
                        <th>Route</th>
                        <td colspan="2">
                            <xsl:call-template name="kmehrcodeTranslate">
                                <xsl:with-param name="cd" select="./kmehr:cd"/>
                            </xsl:call-template>
                        </td>
                    </tr>
                </xsl:when>
                <xsl:when test="name()='instructionforoverdosing'">
                    <tr style="display:none">
                        <th>Instructie bij overdosis</th>
                        <td colspan="2">
                            <xsl:value-of select="."/>
                        </td>
                    </tr>
                </xsl:when>
                <xsl:when test="name()='instructionforpatient'">
                    <tr style="display:none">
                        <th>
                            <xsl:value-of select="'Instructie voor de patiënt'"/>
                        </th>
                        <td colspan="2">
                            <xsl:value-of select="."/>
                        </td>
                    </tr>
                </xsl:when>
                <xsl:when test="name()='lifecycle'">
                    <tr style="display:none">
                        <th>Levenscyclus</th>
                        <td colspan="2">
                            <xsl:call-template name="kmehrcodeTranslate">
                                <xsl:with-param name="cd" select="kmehr:cd"/>
                            </xsl:call-template>
                        </td>
                    </tr>
                </xsl:when>
                <xsl:when test="name()='quantity'">
                    <tr style="display:none">
                        <th>Hoeveelheid</th>
                        <td>
                            <xsl:value-of select="kmehr:decimal"/>
                            <xsl:if test="kmehr:unit/kmehr:cd">
                                <xsl:value-of select="' (eenheid: '"/>
                                <xsl:call-template name="kmehrcodeTranslate">
                                    <xsl:with-param name="cd" select="kmehr:unit/kmehr:cd"/>
                                </xsl:call-template>
                                <xsl:value-of select="')'"/>
                            </xsl:if>
                        </td>
                    </tr>
                </xsl:when>
                <xsl:when test="name()='site'">
                    <tr style="display:none">
                        <th>Site</th>
                        <td colspan="2">
                            <xsl:call-template name="kmehrcodeTranslate">
                                <xsl:with-param name="cd" select="kmehr:cd"/>
                            </xsl:call-template>
                        </td>
                    </tr>
                </xsl:when>
                <xsl:otherwise>
                    <tr style="display:none">
                        <th>
                            <xsl:value-of select="name()"/>
                        </th>
                        <td colspan="2">
                            <xsl:call-template name="kmehrcodeTranslate">
                                <xsl:with-param name="cd" select="."/>
                            </xsl:call-template>
                        </td>
                    </tr>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <!-- PROCESS REGIMEN -->
    <xsl:template name="regimenLoop">
        <xsl:param name="index" select="1"/>

        <xsl:if test="kmehr:quantity[position()= $index]">

            <xsl:if test="kmehr:date[position()= $index]">
                <tr>
                    <td>Datum</td>
                    <td>
                        <xsl:value-of select="kmehr:date[position()= $index]"/>
                    </td>
                </tr>
            </xsl:if>
            <xsl:if test="kmehr:weekday[position()= $index]">
                <tr>
                    <td>Weekdag</td>
                    <td>
                        <xsl:call-template name="kmehrcodeTranslate">
                            <xsl:with-param name="cd" select="kmehr:weekday[position()= $index]/kmehr:cd"/>
                        </xsl:call-template>
                    </td>
                </tr>
            </xsl:if>
            <xsl:if test="kmehr:daynumber[position()= $index]">
                <tr>
                    <td>Dagnummer</td>
                    <td>
                        <xsl:value-of select="kmehr:daynumber[position()= $index]"/>
                    </td>
                </tr>
            </xsl:if>
            <xsl:if test="kmehr:daytime[position()= $index]/kmehr:time">
                <tr>
                    <td>Tijd</td>
                    <td>
                        <xsl:value-of select="kmehr:daytime[position()= $index]/kmehr:time"/>
                    </td>
                </tr>
            </xsl:if>
            <xsl:if test="kmehr:daytime[position()= $index]/kmehr:dayperiod">
                <tr>
                    <td>Inname moment</td>
                    <td>
                        <xsl:call-template name="kmehrcodeTranslate">
                            <xsl:with-param name="cd"
                                            select="kmehr:daytime[position()= $index]/kmehr:dayperiod/kmehr:cd"/>
                        </xsl:call-template>
                    </td>
                </tr>
            </xsl:if>
            <tr>
                <td class="regimebottom">Hoeveelheid</td>
                <td class="regimebottom">
                    <xsl:value-of select="kmehr:quantity[position()= $index]/kmehr:decimal"/>
                    <xsl:if test="kmehr:quantity[position()= $index]/kmehr:unit/kmehr:cd">
                        <xsl:value-of select="' (eenheid: '"/>
                        <xsl:call-template name="kmehrcodeTranslate">
                            <xsl:with-param name="cd" select="kmehr:quantity[position()= $index]/kmehr:unit/kmehr:cd"/>
                        </xsl:call-template>
                        <xsl:value-of select="')'"/>
                    </xsl:if>
                </td>
            </tr>

            <xsl:call-template name="regimenLoop">
                <xsl:with-param name="index" select="$index +1"/>
            </xsl:call-template>
        </xsl:if>

    </xsl:template>


    <!-- PROCESSING PATIENT-->
    <xsl:template match="kmehr:patient">

        <tr id="short">
            <th colspan="3" align="center">
                <!-- no more used <xsl:text>Patient: </xsl:text> -->
                <xsl:value-of select="kmehr:firstname[1]"/><xsl:text> </xsl:text><xsl:value-of
                    select="kmehr:familyname"/>
            </th>
        </tr>

        <xsl:if test="not(kmehr:id[@S='ID-PATIENT' or @S='INSS'])">
            <tr style="display:none">
                <th>INSZ Nr</th>
                <td></td>
            </tr>
        </xsl:if>

        <xsl:for-each select="kmehr:id">
            <xsl:apply-templates select="."/>
        </xsl:for-each>
        <tr style="display:none">
            <th>Voornamen</th>
            <td>
                <xsl:value-of select="kmehr:firstname"/>
            </td>
        </tr>
        <tr style="display:none">
            <th>Familienaam</th>
            <td>
                <xsl:value-of select="kmehr:familyname"/>
            </td>
        </tr>
        <tr id="short">
            <th>Geslacht</th>
            <td>
                <xsl:call-template name="kmehrcodeTranslate">
                    <xsl:with-param name="cd" select="./kmehr:sex/kmehr:cd[@S='CD-SEX']"/>
                </xsl:call-template>
            </td>
        </tr>

        <tr id="short">
            <th>Geboortedatum</th>
            <td>
                <xsl:value-of select="kmehr:birthdate/kmehr:date"/><xsl:value-of select="kmehr:birthdate/kmehr:year"/><xsl:value-of
                    select="kmehr:birthdate/kmehr:yearmonth"/>
            </td>
        </tr>
        <xsl:if test="kmehr:birthlocation">
            <tr style="display:none">
                <th>Geboorteplaats</th>
                <td>
                    <xsl:value-of select="kmehr:birthlocation/kmehr:city"/><xsl:text> </xsl:text><xsl:value-of
                        select="kmehr:birthlocation/kmehr:cd[@S='CD-COUNTRY']"/>
                </td>
            </tr>
        </xsl:if>
        <xsl:if test="kmehr:deathdate">
            <tr style="display:none">
                <th>Datum overlijden</th>
                <td>
                    <xsl:value-of select="kmehr:deathdate/kmehr:date"/><xsl:value-of
                        select="kmehr:deathdate/kmehr:year"/><xsl:value-of select="kmehr:deathdate/kmehr:yearmonth"/>
                </td>
            </tr>
        </xsl:if>
        <xsl:if test="kmehr:deathlocation">
            <tr style="display:none">
                <th>Plaats overlijden</th>
                <td>
                    <xsl:value-of select="kmehr:deathlocation/kmehr:city"/><xsl:text> </xsl:text><xsl:value-of
                        select="kmehr:deathlocation/kmehr:cd[@S='CD-COUNTRY']"/>
                </td>
            </tr>
        </xsl:if>
        <xsl:if test="kmehr:nationality">
            <tr style="display:none">
                <th>Nationaliteit</th>
                <td>
                    <xsl:call-template name="kmehrcodeTranslate">
                        <xsl:with-param name="cd" select="kmehr:nationality/kmehr:cd[@S='CD-COUNTRY']"/>
                    </xsl:call-template>
                </td>
            </tr>
        </xsl:if>
        <tr id="short">
            <th>Gebruikelijke taal</th>
            <td>
                <xsl:value-of select="kmehr:usuallanguage"/>
            </td>
        </tr>

        <xsl:if test="not(kmehr:address)">
            <tr id="short">
                <th>Adres</th>
                <td></td>
            </tr>
        </xsl:if>
        <xsl:if test="not(kmehr:telecom[kmehr:cd[@S='CD-TELECOM']='phone'] or kmehr:telecom[kmehr:cd[@S='CD-TELECOM']='mobile'])">
            <tr style="display:none">
                <th>Telefoon</th>
                <td></td>
            </tr>
        </xsl:if>
        <xsl:apply-templates select="kmehr:address"/>
        <xsl:apply-templates select="kmehr:telecom"/>

        <!-- END Patient specific section -->
    </xsl:template>


    <!-- PROCESSING PERSON -->
    <xsl:template match="kmehr:person">

        <tr id="short">
            <th colspan="3" align="center">
                <!-- no more used <xsl:value-of select="../../kmehr:cd[@S='CD-ITEM']"/><xsl:text>: </xsl:text> -->
                <xsl:value-of select="kmehr:firstname"/><xsl:text> </xsl:text><xsl:value-of select="kmehr:familyname"/>
            </th>
        </tr>

        <xsl:for-each select="kmehr:id">
            <xsl:apply-templates select="."/>
        </xsl:for-each>

        <tr style="display:none">
            <th>Voornamen</th>
            <td>
                <xsl:value-of select="kmehr:firstname"/>
            </td>
        </tr>
        <tr style="display:none">
            <th>Familienaam</th>
            <td>
                <xsl:value-of select="kmehr:familyname"/>
            </td>
        </tr>
        <tr style="display:none">
            <th>Geslacht</th>
            <td>
                <xsl:call-template name="kmehrcodeTranslate">
                    <xsl:with-param name="cd" select="kmehr:sex/kmehr:cd[@S='CD-SEX']"/>
                </xsl:call-template>
            </td>
        </tr>

        <xsl:if test="kmehr:birthdate">
            <tr style="display:none">
                <th>Geboortedatum</th>
                <td>
                    <xsl:value-of select="kmehr:birthdate/kmehr:date"/><xsl:value-of
                        select="kmehr:birthdate/kmehr:year"/><xsl:value-of select="kmehr:birthdate/kmehr:yearmonth"/>
                </td>
            </tr>
        </xsl:if>
        <xsl:if test="kmehr:birthlocation">
            <tr style="display:none">
                <th>Geboorteplaats</th>
                <td>
                    <xsl:value-of select="kmehr:birthlocation/kmehr:city"/><xsl:text> </xsl:text><xsl:value-of
                        select="kmehr:birthlocation/kmehr:cd[@S='CD-COUNTRY']"/>
                </td>
            </tr>
        </xsl:if>
        <xsl:if test="kmehr:deathdate">
            <tr style="display:none">
                <th>Datum overlijden</th>
                <td>
                    <xsl:value-of select="kmehr:deathdate/kmehr:date"/><xsl:value-of
                        select="kmehr:deathdate/kmehr:year"/><xsl:value-of select="kmehr:deathdate/kmehr:yearmonth"/>
                </td>
            </tr>
        </xsl:if>
        <xsl:if test="kmehr:deathlocation">
            <tr style="display:none">
                <th>Plaats overlijden</th>
                <td>
                    <xsl:value-of select="kmehr:deathlocation/kmehr:city"/><xsl:text> </xsl:text><xsl:value-of
                        select="kmehr:deathlocation/kmehr:cd[@S='CD-COUNTRY']"/>
                </td>
            </tr>
        </xsl:if>
        <xsl:if test="kmehr:nationality">
            <tr style="display:none">
                <th>Nationaliteit</th>
                <td>
                    <xsl:call-template name="kmehrcodeTranslate">
                        <xsl:with-param name="cd" select="kmehr:nationality/kmehr:cd[@S='CD-COUNTRY']"/>
                    </xsl:call-template>
                </td>
            </tr>
        </xsl:if>
        <xsl:if test="kmehr:usuallanguage">
            <tr style="display:none">
                <th>Gebruikelijke taal</th>
                <td>
                    <xsl:value-of select="kmehr:usuallanguage"/>
                </td>
            </tr>
        </xsl:if>

        <xsl:if test="not(kmehr:address)">
            <tr style="display:none">
                <th>Adres</th>
                <td></td>
            </tr>
        </xsl:if>
        <xsl:if test="not(kmehr:telecom[kmehr:cd[@S='CD-TELECOM']='phone'] or kmehr:telecom[kmehr:cd[@S='CD-TELECOM']='mobile'])">
            <tr style="display:none">
                <th>Telefoon</th>
                <td></td>
            </tr>
        </xsl:if>
        <xsl:apply-templates select="kmehr:address"/>
        <xsl:apply-templates select="kmehr:telecom"/>

        <!-- Person specific section -->
    </xsl:template>

    <!-- HCPARTY PROCESSING -->
    <xsl:template match="kmehr:hcparty">
        <xsl:choose>
            <xsl:when test="name(..)='author'">
                <tr id="short">
                    <th colspan="3" align="center">
                        <!-- no more used <xsl:text>Author hcparty : </xsl:text> -->
                        <xsl:value-of select="kmehr:name"/><xsl:value-of select="kmehr:firstname[1]"/><xsl:text> </xsl:text><xsl:value-of
                            select="kmehr:familyname"/>
                    </th>
                </tr>
            </xsl:when>
            <xsl:otherwise>
                <tr id="short">
                    <th colspan="3" align="center">
                        <!-- no more used <xsl:value-of select="../../kmehr:cd[@S='CD-ITEM']"/><xsl:text> : </xsl:text> -->
                        <xsl:value-of select="kmehr:name"/><xsl:value-of select="kmehr:firstname"/><xsl:text> </xsl:text><xsl:value-of
                            select="kmehr:familyname"/>
                    </th>
                </tr>
            </xsl:otherwise>
        </xsl:choose>

        <xsl:if test="not(kmehr:id[@S='ID-HCPARTY'])">
            <tr style="display:none">
                <th>NIHII / INAMI / RIZIV No</th>
                <td></td>
            </tr>
        </xsl:if>
        <xsl:for-each select="kmehr:id">
            <xsl:apply-templates select="."/>
        </xsl:for-each>

        <xsl:if test="not(kmehr:id[@S='INSS']) and name(..)='author'">
            <tr style="display:none">
                <th>INSZ Nr</th>
                <td></td>
            </tr>
        </xsl:if>
        <xsl:if test="not(kmehr:cd[@S='CD-HCPARTY']) and name(..)='author'">
            <tr style="display:none">
                <th>Rol</th>
                <td></td>
            </tr>
        </xsl:if>
        <xsl:for-each select="kmehr:cd">
            <xsl:apply-templates select="."/>
        </xsl:for-each>

        <xsl:choose>
            <xsl:when test="kmehr:firstname">
                <tr style="display:none">
                    <th>Voornamen</th>
                    <td>
                        <xsl:value-of select="kmehr:firstname"/>
                    </td>
                </tr>
                <tr style="display:none">
                    <th>Familienaam</th>
                    <td>
                        <xsl:value-of select="kmehr:familyname"/>
                    </td>
                </tr>
            </xsl:when>
            <xsl:when test="kmehr:name">
                <tr style="display:none">
                    <th>Naam</th>
                    <td>
                        <xsl:value-of select="kmehr:name"/>
                    </td>
                </tr>
            </xsl:when>
        </xsl:choose>

        <xsl:if test="not(kmehr:address)">
            <tr style="display:none">
                <th>Adres</th>
                <td></td>
            </tr>
        </xsl:if>
        <xsl:if test="not(kmehr:telecom[kmehr:cd[@S='CD-TELECOM']='phone'] or kmehr:telecom[kmehr:cd[@S='CD-TELECOM']='mobile'])">
            <tr style="display:none">
                <th>Telefoon</th>
                <td></td>
            </tr>
        </xsl:if>
        <xsl:apply-templates select="kmehr:address"/>
        <xsl:apply-templates select="kmehr:telecom"/>

    </xsl:template>


    <!-- TELECOM PROCESSING -->
    <xsl:template match="kmehr:telecom">
        <tr style="display:none">
            <th>
                <xsl:value-of select="kmehr:cd[@S='CD-TELECOM']"/>
            </th>
            <td>
                <xsl:value-of select="kmehr:telecomnumber"/> (<xsl:value-of select="kmehr:cd[@S='CD-ADDRESS']"/>)
            </td>
        </tr>
    </xsl:template>

    <!-- ADDRESS PROCESSING -->
    <xsl:template match="kmehr:address">
        <tr>
            <xsl:choose>
                <xsl:when test="name(..)='patient'">
                    <xsl:attribute name="id" select="'short'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="style" select="'display:none'"/>
                </xsl:otherwise>
            </xsl:choose>
            <th>
                <xsl:value-of select="kmehr:cd"/><xsl:text> Adres</xsl:text>
            </th>
            <td>
                <xsl:choose>
                    <xsl:when test="kmehr:text">
                        <xsl:value-of select="kmehr:text"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="kmehr:street"/>
                        <xsl:text> </xsl:text><xsl:value-of select="kmehr:housenumber"/>
                        <xsl:if test="kmehr:postboxnumber">
                            <xsl:text> PB : </xsl:text><xsl:value-of select="kmehr:postboxnumber"/>
                        </xsl:if>
                        <xsl:text> ; </xsl:text>
                        <xsl:text> </xsl:text><xsl:value-of select="kmehr:zip"/>
                        <xsl:if test="kmehr:nis">
                            <xsl:text> nis : </xsl:text><xsl:value-of select="kmehr:nis"/>
                        </xsl:if>
                        <xsl:text> </xsl:text><xsl:value-of select="kmehr:city"/>
                        <xsl:if test="kmehr:district">
                            <xsl:text> district : </xsl:text><xsl:value-of select="kmehr:district"/>
                        </xsl:if>
                        <xsl:if test="kmehr:country">
                            <xsl:text> / </xsl:text><xsl:value-of select="kmehr:country/kmehr:cd"/>
                        </xsl:if>
                        <xsl:if test="kmehr:text">
                            <xsl:text> / commentaar : </xsl:text><xsl:value-of select="kmehr:text"/>
                        </xsl:if>
                    </xsl:otherwise>
                </xsl:choose>
            </td>
        </tr>
    </xsl:template>

    <!-- ID PROCESSING -->
    <xsl:template match="kmehr:id">
        <xsl:choose>
            <xsl:when test="@S='ID-KMEHR'">
            </xsl:when>
            <xsl:otherwise>
                <tr style="display:none">
                    <th>
                        <xsl:choose>
                            <!-- no more used: replaced bay id INSS
                            <xsl:when test="@S='LOCAL' and @SL='ID-PATIENT' and name(..)='hcparty'">
                                <xsl:text>INSS No</xsl:text>
                            </xsl:when> -->
                            <xsl:when test="@S='LOCAL'">
                                <xsl:text>(lokaal id) </xsl:text><xsl:value-of select="@SL"/><xsl:text> </xsl:text><xsl:value-of
                                    select="@SV"/>
                            </xsl:when>
                            <xsl:when test="@S='INSS'">
                                <xsl:text>INSZ Nr</xsl:text>
                            </xsl:when>
                            <xsl:when test="@S='ID-PATIENT'">
                                <xsl:text>INSZ Nr</xsl:text>
                            </xsl:when>
                            <xsl:when test="@S='ID-HCPARTY'">
                                <xsl:text>NIHII / INAMI / RIZIV</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="@S"/><xsl:text> </xsl:text><xsl:value-of select="@SV"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </th>
                    <td colspan="2">
                        <xsl:value-of select="."/>
                    </td>
                </tr>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- CD PROCESSING -->
    <xsl:template match="kmehr:cd">
        <xsl:choose>
            <xsl:when test="@S='CD-ITEM'">
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="@S='CD-HCPARTY' and ../../../kmehr:cd[@S='CD-ITEM']='contacthcparty'">
                        <tr id="short">
                            <th>
                                <xsl:text>Rol</xsl:text>
                            </th>
                            <td colspan="2">
                                <xsl:call-template name="kmehrcodeTranslate">
                                    <xsl:with-param name="cd" select="."/>
                                </xsl:call-template>
                                <xsl:if test="@DN">
                                    <xsl:text> (</xsl:text><xsl:value-of select="@DN"/><xsl:text>)</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                    </xsl:when>
                    <xsl:when test="@S='CD-CONTACT-PERSON' and ../kmehr:cd[@S='CD-ITEM']='contactperson'">
                        <tr id="short">
                            <th>
                                <xsl:text>Familie band</xsl:text>
                            </th>
                            <td colspan="2">
                                <xsl:value-of select="."/>
                                <xsl:if test="@DN">
                                    <xsl:text> (</xsl:text><xsl:value-of select="@DN"/><xsl:text>)</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                    </xsl:when>
                    <xsl:otherwise>
                        <tr style="display:none">
                            <th>
                                <xsl:choose>
                                    <xsl:when test="@S='CD-ITEM'">
                                    </xsl:when>
                                    <xsl:when test="@S='LOCAL'">
                                        <xsl:text>(lokaal cd) </xsl:text><xsl:value-of select="@SL"/><xsl:text> </xsl:text><xsl:value-of
                                            select="@SV"/>
                                    </xsl:when>
                                    <xsl:when test="@S='CD-HCPARTY'">
                                        <xsl:text>Rol</xsl:text>
                                    </xsl:when>
                                    <xsl:when test="@S='CD-CONTACT-PERSON'">
                                        <xsl:text>Familie band</xsl:text>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="@S"/><xsl:text> </xsl:text><xsl:value-of select="@SV"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </th>
                            <td colspan="2">
                                <!-- If we want to use secondary files
								<xsl:variable name="code-group"><xsl:value-of select="@S"/></xsl:variable>
                                <xsl:variable name="code"><xsl:value-of select="current()"/></xsl:variable>
                                <xsl:choose>
                                  <xsl:when test="document('index.xml')//index:code-group[@S=$code-group]/index:translation/@code = $code">
                                    <xsl:value-of select="document('index.xml')//index:code-group[@S='CD-HCPARTY']/index:translation[@code='persphysician']"/>
                                  </xsl:when>
                                  <xsl:otherwise>
                                    <xsl:value-of select="."/>
                                  </xsl:otherwise>
                                </xsl:choose> 
                                -->
                                <xsl:call-template name="kmehrcodeTranslate">
                                    <xsl:with-param name="cd" select="."/>
                                </xsl:call-template>
                                <xsl:if test="@DN">
                                    <xsl:text> (</xsl:text><xsl:value-of select="@DN"/><xsl:text>)</xsl:text>
                                </xsl:if>
                            </td>
                        </tr>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="kmehrcodeTranslate">
        <xsl:param name="cd"/>
        <xsl:choose>
            <xsl:when test="translate($cd/@S, $uppercase, $lowercase) =''">
                <xsl:value-of select="$cd"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="function-available('doc-available')">
                        <xsl:variable name="url">
                            <xsl:choose use-when="function-available('doc-available')">
                                <xsl:when
                                        test="doc-available(string('https://decryptor-accept.vitalink.be/static/url.xml'))">
                                    <xsl:value-of
                                            select="concat(document('https://decryptor-accept.vitalink.be/static/url.xml')/url,translate($cd/@S, $uppercase, $lowercase),'.xml')"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of
                                            select="concat('https://www.ehealth.fgov.be/standards/kmehr/sites/default/files/assets/reference_table/xml/',translate($cd/@S, $uppercase, $lowercase),'.xml')"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:choose>
                            <xsl:when test="doc-available(string($url))">
                                <xsl:variable name="value" select="document($url)/kmehr-cd/VALUE[./CODE=$cd]"/>
                                <xsl:call-template name="translateFromDocument">
                                    <xsl:with-param name="value" select="$value"/>
                                    <xsl:with-param name="cd" select="$cd"/>
                                </xsl:call-template>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$cd"/>
                            </xsl:otherwise>
                        </xsl:choose>


                    </xsl:when>
                    <xsl:when test="function-available('my-ext:docAvailable')">
                        <xsl:choose use-when="function-available('my-ext:docAvailable')">
                            <xsl:when
                                    test="my-ext:docAvailable(string('https://decryptor-accept.vitalink.be/static/url.xml'))">
                                <xsl:variable name="url"
                                              select="concat(my-ext:document('https://decryptor-accept.vitalink.be/static/url.xml')/url,translate($cd/@S, $uppercase, $lowercase),'.xml')"/>
                                <xsl:choose>
                                    <xsl:when test="my-ext:docAvailable(string($url))">
                                        <xsl:variable name="value"
                                                      select="my-ext:document($url)/kmehr-cd/VALUE[./CODE=$cd]"/>
                                        <xsl:call-template name="translateFromDocument">
                                            <xsl:with-param name="value" select="$value"/>
                                            <xsl:with-param name="cd" select="$cd"/>
                                        </xsl:call-template>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="$cd"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$cd"/>
                            </xsl:otherwise>

                        </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$cd"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="translateFromDocument">
        <xsl:param name="value"/>
        <xsl:param name="cd"/>
        <xsl:choose>
            <xsl:when test="$value/DESCRIPTION[@L='nl']">
                <xsl:value-of select="$value/DESCRIPTION[@L='nl']"/>
            </xsl:when>
            <xsl:when test="$value/DESCRIPTION[@L='en']">
                <xsl:value-of select="$value/DESCRIPTION[@L='en']"/>
            </xsl:when>
            <xsl:when test="$value/DESCRIPTION[@L='fr']">
                <xsl:value-of select="$value/DESCRIPTION[@L='fr']"/>
            </xsl:when>
            <xsl:when test="$value/DESCRIPTION">
                <xsl:value-of select="$value/DESCRIPTION[position()=1]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$cd"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
