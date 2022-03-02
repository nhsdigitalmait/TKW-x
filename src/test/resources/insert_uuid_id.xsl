<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : insert_uuid_id.xsl
    Created on : 2 March 2022, 13:19
    Author     : simonfarrow
    Description:
        Use the tkw uuid handler to put a guid id into the stree one level below the root.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fhir="http://hl7.org/fhir" version="1.0">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>
    <xsl:param name ="port" select="'8000'"/>
    <xsl:variable name="uuid" select="document(concat('http://localhost:',$port,'/getuuid'))/uuid/text()"/>

	<xsl:template match="/*">
		<xsl:copy>
            <fhir:id value="{$uuid}"/>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>

	<!-- match all atts all nodes -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
