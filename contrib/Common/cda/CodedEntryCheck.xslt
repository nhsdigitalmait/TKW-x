<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:hl7="urn:hl7-org:v3" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<xsl:variable name="codedentries" select="//*[@codeSystem='2.16.840.1.113883.2.1.3.2.4.15' and @displayName]"/>
		<xsl:choose>
			<xsl:when test="$codedentries">
				<table border="0">
					<br/>
					<td style="color:#008000">
						<b>
							<xsl:text>Coded Entry List (for manual validation)</xsl:text>
						</b>
					</td>
					<xsl:for-each select="$codedentries">
						<xsl:variable name="type" select="./@xsi:type"/>
						<xsl:variable name="code" select="./@code"/>
						<xsl:variable name="displayName" select="./@displayName"/>
						<tr>
							<td style="color:#008000">
								<xsl:text>code: '</xsl:text>
								<xsl:value-of select="$code"/>
								<xsl:text>' displayName: '</xsl:text>
								<xsl:value-of select="$displayName"/>
								<xsl:text>' type: '</xsl:text>
								<xsl:value-of select="$type"/>
								<xsl:text>'</xsl:text>
								<br/>
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<tr>
					<td>
						<xsl:text>No coded entries found</xsl:text>
					</td>
				</tr>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
