<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fhir="http://hl7.org/fhir" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="html" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
	
	<xsl:template name="plotPath">
		<xsl:param name="forNode"/>
		<xsl:if test="name($forNode)">
			<xsl:call-template name="plotPath">
				<xsl:with-param name="forNode" select="$forNode/.."/>
			</xsl:call-template>
			<xsl:text>/</xsl:text>
		</xsl:if>
		<xsl:value-of select="name($forNode)"/>
	</xsl:template>
	
</xsl:stylesheet>
