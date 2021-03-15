<?xml version="1.0" encoding="UTF-8"?>
<!-- Check format of message ID's. Must be 5 hyphen-separated groups of hexadecimal digits using all lower or all upper case having 8, 4, 4, 4, and 12 places respectively. -->
<!-- Modue checks all Resource id's plus the Bundle id. -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:fhir="http://hl7.org/fhir">
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<table border="0">
			<xsl:for-each select="//fhir:id/@value">
			<!--<xsl:for-each select="//fhir:resource//fhir:id/@value">-->
				<xsl:variable name="resource_id" select='.'/>
				<xsl:choose>
					<xsl:when test="$resource_id  != '' or $resource_id !='' ">
						<xsl:choose>
							<xsl:when test="matches($resource_id,'[a-zA-Z_:]*[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}')">
								<tr>
									<td style="color:#008000">
										<xsl:text>PASS: Reference </xsl:text>
										<xsl:value-of select="."/>
										<xsl:text> in xpath '</xsl:text>
										<xsl:call-template name="plotPath">
											<xsl:with-param name="forNode" select=".."/>
										</xsl:call-template>
										<xsl:text>' the resource ID follows the rule - 5 hyphen-separated groups of hexadecimal digits using lower case having 8, 4, 4, 4, and 12 places respectively. '</xsl:text>
										<br/>
									</td>
								</tr>
							</xsl:when>
							<xsl:when test="matches($resource_id,'[a-zA-Z_:]*[A-F0-9]{8}-[A-F0-9]{4}-[A-F0-9]{4}-[A-F0-9]{4}-[A-F0-9]{12}')">
								<tr>
									<td style="color:#008000">
										<xsl:text>PASS: Reference </xsl:text>
										<xsl:value-of select="."/>
										<xsl:text> in xpath '</xsl:text>
										<xsl:call-template name="plotPath">
											<xsl:with-param name="forNode" select=".."/>
										</xsl:call-template>
										<xsl:text>' the resource ID follows the rule - 5 hyphen-separated groups of hexadecimal digits using upper case having 8, 4, 4, 4, and 12 places respectively. '</xsl:text>
										<br/>
									</td>
								</tr>
							</xsl:when>
							<xsl:otherwise>
								<tr>
									<td style="color:#900000">
										<xsl:text>ERROR: Reference </xsl:text>
										<xsl:value-of select="."/>
										<xsl:text> in xpath '</xsl:text>
										<xsl:call-template name="plotPath">
											<xsl:with-param name="forNode" select=".."/>
										</xsl:call-template>
										<xsl:text>' the resource ID follows does NOT follow the rule - 5 hyphen-separated groups of hexadecimal digits having 8, 4, 4, 4, and 12 places respectively. Characters must be ALL upper or ALL lower case NOT a mix of both.'</xsl:text>
									</td>
								</tr>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR: The @value '</xsl:text>
								<xsl:value-of select="."/>
								<xsl:text>' in xpath </xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select=".."/>
								</xsl:call-template>
								<xsl:text> is not of the form resource/id</xsl:text>
							</td>
						</tr>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</table>
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
