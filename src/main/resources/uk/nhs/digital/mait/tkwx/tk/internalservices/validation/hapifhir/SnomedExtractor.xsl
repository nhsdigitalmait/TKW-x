<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : SnomedExtractor.xsl
    Created on : 10 June 2019, 15:56
    Author     : riro
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fhir="http://hl7.org/fhir" version="1.0">
    <xsl:output method="text"/>

    <xsl:template match="/">
        <xsl:for-each select="//fhir:coding">
            <xsl:if test="fhir:system/@value='http://snomed.info/sct'">
                <xsl:value-of select="fhir:code/@value" />
                <xsl:text>,</xsl:text>
            </xsl:if>                    
            <xsl:value-of select="@value"/>
        </xsl:for-each>
                

    </xsl:template>

</xsl:stylesheet>
