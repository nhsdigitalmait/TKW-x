<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:hl7="urn:hl7-org:v3">
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<xsl:variable name="templateExtensions" select="//*[local-name()='structuredBody']//*[@root='2.16.840.1.113883.2.1.3.2.4.18.16']"/>
		<xsl:variable name="clinicalHeadingref" select="document('./DischargeReference.xml')"/>
		<xsl:choose>
			<xsl:when test="$templateExtensions">
				<table border="0">
					<xsl:for-each select="$templateExtensions">
						<xsl:if test="name() = 'npfitlc:contentId'">
							<xsl:variable name="extension_message" select="./@extension"/>
							<xsl:variable name="extension_referencefile" select="$clinicalHeadingref//template[@extension=$extension_message]/@extension"/>
							<xsl:variable name="SNOMED_message" select="//*[local-name()='structuredBody']//*[@root='2.16.840.1.113883.2.1.3.2.4.18.16'][@extension=$extension_message]/../*[local-name()='section']/*[local-name()='code'][@codeSystem='2.16.840.1.113883.2.1.3.2.4.15']/@code"/>
							<xsl:variable name="SNOMED_referencefile" select="$clinicalHeadingref//template[@extension=$extension_message]/@snomed"/>
							<xsl:choose>
								<xsl:when test="not($SNOMED_message=$SNOMED_referencefile)">
										<tr>
											<td style="color:#900000">
												<xsl:text>ERROR: INVALID Extension </xsl:text>
												<xsl:value-of select="$extension_message"/>
											</td>
										</tr>
									</xsl:when>
							</xsl:choose>
						</xsl:if>
					</xsl:for-each>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<tr>
					<td style="color:#900000">
						<xsl:text>ERROR: SNOMED Reference Found</xsl:text>
					</td>
				</tr>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
