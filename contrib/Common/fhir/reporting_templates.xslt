<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xpath-default-namespace="http://hl7.org/fhir">
	<xsl:output method="html" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
	<xsl:include href="FHIR_Generic_Get_Xpath_v1.0.xslt"/>
		<!-- reusable reporting templates -->
	<xsl:template name="reportPass">
		<xsl:param name="resultString"/>
		<tr>
			<td style="color:#008000">
				<xsl:value-of select="$resultString"/>
				<xsl:text>   PASS</xsl:text>
			</td>
		</tr>
	</xsl:template>
	<xsl:template name="reportError">
		<xsl:param name="resultString"/>
		<tr>
			<td style="color:#900000">
				<xsl:value-of select="$resultString"/>
				<xsl:text>    ERROR</xsl:text>
			</td>
		</tr>
	</xsl:template>
	<xsl:template name="reportPassLocation">
		<xsl:param name="resultString"/>
		<xsl:param name="location"/>
		<tr>
			<td style="color:#008000">
				<xsl:value-of select="$resultString"/>
				<xsl:text> in xpath '</xsl:text>
				<xsl:call-template name="plotPath">
					<xsl:with-param name="forNode" select="$location"/>
				</xsl:call-template>
				<xsl:text>'   PASS</xsl:text>
			</td>
		</tr>
	</xsl:template>
	<xsl:template name="reportErrorLocation">
		<xsl:param name="resultString"/>
		<xsl:param name="location"/>
		<tr>
			<td style="color:#900000">
				<xsl:value-of select="$resultString"/>
				<xsl:text> in xpath '</xsl:text>
				<xsl:call-template name="plotPath">
					<xsl:with-param name="forNode" select="$location"/>
				</xsl:call-template>
				<xsl:text>'    ERROR</xsl:text>
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>
