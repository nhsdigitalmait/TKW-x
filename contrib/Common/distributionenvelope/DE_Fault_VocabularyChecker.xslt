<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2004/10/xpath-functions" xmlns:xdt="http://www.w3.org/2004/10/xpath-datatypes" xmlns:itk="urn:nhs-itk:ns:201005">
	<xsl:output method="text" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<xsl:variable name="vocabfile" select="document('./DE_error_reference.xml')"/>
		<xsl:variable name="codesystem" select="'2.16.840.1.113883.2.1.3.2.4.17.516'"/>
		<xsl:variable name="version" select="'1.0'"/>
		<xsl:variable name="csroot" select="DE_error"/>
		<xsl:for-each select="//itk:payload//*[@codeSystem = $codesystem]">
			<xsl:variable name="vocab" select="$vocabfile//itk:vocabulary[@id = $codesystem][@version = $version]"/>
			<!--
							There MAY be no "concept" elements in the vocabulary... only try to apply the check if there is
							anything to apply it with...
						-->
			<xsl:if test="$vocab/itk:concept">
				<xsl:variable name="code" select="//itk:DistributionEnvelope/itk:payloads/itk:payload/itk:InfrastructureResponse/itk:errors/itk:errorInfo/itk:ErrorCode"/>
				<xsl:variable name="displayName" select="//itk:DistributionEnvelope/itk:payloads/itk:payload/itk:InfrastructureResponse/itk:errors/itk:errorInfo/itk:ErrorText"/>
				<!--							<xsl:value-of select="$code"/><xsl:text> </xsl:text><xsl:value-of select="$displayName"/>-->
				<!--
									First see if the code is known to the vocabulary, then if the displayName matches
								-->
				<xsl:variable name="vocabConcept" select="$vocab/itk:concept[@code = $code]"/>
				<xsl:choose>
					<xsl:when test="$vocabConcept">
						<!--
										The @displayName is optional - so we only check it if it is present
									-->
						<xsl:if test="$displayName">
							<xsl:choose>
								<xsl:when test="$vocabConcept/itk:displayName = $displayName"/>
								<xsl:otherwise>
									<xsl:variable name="location">
										<xsl:call-template name="plotPath">
											<xsl:with-param name="forNode" select="."/>
										</xsl:call-template>
									</xsl:variable>
									<xsl:text>Error: Invalid displayName at </xsl:text>
									<xsl:value-of select="$location"/>
									<xsl:text> displayName </xsl:text>
									<xsl:value-of select="$displayName"/>
									<xsl:text> not valid for code </xsl:text>
									<xsl:value-of select="$code"/>
									<xsl:text> in vocabulary </xsl:text>
									<xsl:value-of select="$csroot"/>
									<xsl:text>/</xsl:text>
									<xsl:value-of select="$version"/>
									<br/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:variable name="location">
							<xsl:call-template name="plotPath">
								<xsl:with-param name="forNode" select="."/>
							</xsl:call-template>
						</xsl:variable>
						<xsl:text>Error: Invalid code at </xsl:text>
						<xsl:value-of select="$location"/>
						<xsl:text> code </xsl:text>
						<xsl:value-of select="$code"/>
						<xsl:text> not found in vocabulary </xsl:text>
						<xsl:value-of select="$csroot"/>
						<xsl:text> OID/version: </xsl:text>
						<xsl:value-of select="$codesystem"/>
						<xsl:text>/</xsl:text>
						<xsl:value-of select="$version"/>
						<br/>
					</xsl:otherwise>
				</xsl:choose>
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
