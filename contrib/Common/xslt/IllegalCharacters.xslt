<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xdt="http://www.w3.org/2004/10/xpath-datatypes">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
		<xsl:template match="/"><xsl:text disable-output-escaping="yes"/>
		<!-- Check dodgy characters in element content -->
		<xsl:variable name="apos" select='"&apos;"'/>
		<xsl:variable name="amp" select='"&amp;"'/>
		<xsl:variable name="hyphen" select='"â€�?;"'/>
		<xsl:variable name="foo" select="concat('^[\-_A-Z=a-z\t\n\r\] \[0-9 `\$²%@~\^\\/\\ \\.,:\\,?\*\+\(\)\|\{\}\[\] &#35;&#xA3;&#xAC;&#x22;&#x201C;&#x201D;&#xB0;&#10003;&#10007;&#xAC;&#xE9;&gt;&lt;!&amp;',$apos,'',$hyphen, $amp,']*$' )" />
<!--<xsl:variable name="foo" select="concat(^[\-_A-Z=a-z\t\n\r\] \[0-9 ²%@~\^\\/\\ \\.,:;\\,?\*\+\(\)\|\{\}\[\] &#x22;&#x201C;&#x201D;&#xB0;&#10003;&#10007;&#xAC;&#xE9;&gt;&lt;!&amp;',$apos,'',$hyphen, $amp,']*$' )" />-->
		<xsl:variable name="dodgychars" select="//text()[not(matches(.,$foo, 'm'))]" />
		<xsl:for-each select="$dodgychars">
			<br/>
			<xsl:text>ERROR: Found illegal character in </xsl:text>
			<xsl:value-of select="."/>
			<br/>
			<xsl:text> at </xsl:text>			
			<xsl:call-template name="plotPath">
				<xsl:with-param name="forNode" select="."/>
			</xsl:call-template>
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
