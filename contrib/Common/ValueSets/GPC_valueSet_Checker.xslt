<?xml version="1.0" encoding="UTF-8"?><xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:fhir="http://hl7.org/fhir">
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>	<xsl:template match="/">
		<xsl:variable name="profileReference" select="document('GPC_fhir_profile.xml')"/>
		<xsl:for-each select="//fhir:system">
			<xsl:if test="contains(./@value,'ValueSet')">
			<xsl:variable name="vsid" select="substring-after(./@value,'ValueSet/')"/>
			<xsl:if test="$vsid">
				<xsl:text>Processing fhir valueSet - </xsl:text>
				<xsl:value-of select="$vsid"/>
				<xsl:text>&#xa;</xsl:text>				
				<br/>
				<xsl:variable name="code" select="../fhir:code/@value"/>
				<xsl:variable name="display" select="../fhir:display/@value"/>		
				<xsl:for-each select="$profileReference//ProfileConfigs/configItem">
				<xsl:variable name="csroot" select="./@codeSystemName"/>

					<xsl:if test="$csroot = $vsid">				
						<xsl:variable name="csname" select="concat('./Reference/', $csroot)"/>
						<xsl:variable name="csfile" select="concat($csname,'.xml')"/>
						<xsl:variable name="codeSystemFile" select="document($csfile)"/>				
						<!-- First see if the code is known to the valueSet, then if the display matches -->
						<xsl:text>Looking up code/display - </xsl:text>
						<xsl:value-of select="$code"/>	
						<xsl:text> /  </xsl:text>						
						<xsl:value-of select="$display"/>				
						<br/>	
						<xsl:variable name="vsCode" select="$codeSystemFile//fhir:concept[@code = $code]"/>
						<xsl:choose>
							<xsl:when test="$vsCode">						
						<!-- The @display is optional - so we only check it if it is present -->						
								<xsl:if test="$display">
									<xsl:choose>
										<xsl:when test="$codeSystemFile//fhir:display = $display"/>
										<xsl:otherwise>
											<xsl:variable name="location">
												<xsl:call-template name="plotPath">
													<xsl:with-param name="forNode" select="."/>
												</xsl:call-template>
											</xsl:variable>
											<xsl:text>Error: Invalid display at </xsl:text>
											<xsl:value-of select="$location"/>
											<xsl:text> display </xsl:text>
											<xsl:value-of select="$display"/>
											<xsl:text> not valid for code </xsl:text>
											<xsl:value-of select="$code"/>
											<xsl:text> in valueSet </xsl:text>
											<xsl:value-of select="$csroot"/>
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
								<xsl:text> not found in valueSet </xsl:text>
								<xsl:value-of select="$csroot"/>
								<br/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
			</xsl:for-each>
			</xsl:if>
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