<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2004/10/xpath-functions" xmlns:xdt="http://www.w3.org/2004/10/xpath-datatypes">
	<xsl:output method="text" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<xsl:for-each select="//@*">
			<xsl:if test=". = ''">
				<xsl:text>ERROR: Blank attribute @</xsl:text>
				<xsl:value-of select="name(.)"/>
				<xsl:text> at </xsl:text>
				<xsl:call-template name="plotPath">
					<xsl:with-param name="forNode" select=".."/>
				</xsl:call-template>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
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
