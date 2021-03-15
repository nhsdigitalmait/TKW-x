<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:hl7="urn:hl7-org:v3">
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<!--templateExtensions - this xpath will return all section heading contentId in structuredBody (explicitly ignoring coded entries)-->
		<xsl:variable name="templateExtensions" select="//*[local-name()='structuredBody']//*[@root='2.16.840.1.113883.2.1.3.2.4.18.16']/..[local-name()='component']/child::*[local-name()='contentId']"/>
		<xsl:variable name="clinicalHeadingref" select="document('./DischargeReference.xml')"/>
		<xsl:choose>
			<xsl:when test="$templateExtensions">
				<table border="0">
					<xsl:for-each select="$templateExtensions">
						<xsl:if test="name() = 'npfitlc:contentId'">
							<xsl:variable name="extension_message" select="./@extension"/>
							<xsl:variable name="extension_referencefile" select="$clinicalHeadingref//template[@extension=$extension_message]/@extension"/>
							<xsl:variable name="CHTitle_message" select="//*[local-name()='structuredBody']//*[@root='2.16.840.1.113883.2.1.3.2.4.18.16' and @extension = $extension_message]/../*[local-name()='section']/*[local-name()='title']"/>
							<xsl:variable name="CHTitle_referencefile" select="$clinicalHeadingref//template[@extension=$extension_message]/clinicalheading/@title"/>
							<tr>
								<td style="color:#008000">
									<b>
										<xsl:value-of select="$extension_message"/>
									</b>
									<br/>
								</td>
							</tr>
							<xsl:choose>
								<xsl:when test="not($extension_message=$extension_referencefile) or not($CHTitle_message=$CHTitle_referencefile)">
									<!--Check extension from message against extension from reference file-->
									<xsl:choose>
										<xsl:when test="not(upper-case($extension_message)=upper-case($extension_referencefile))">
											<tr>
												<td style="color:#900000">
													<xsl:text>ERROR: An invalid extension has been used</xsl:text>
													<xsl:value-of select="$extension_message"/>
													<xsl:text> </xsl:text>
													<xsl:value-of select="$extension_referencefile"/>
												</td>
											</tr>
										</xsl:when>
										<xsl:otherwise>
											<!--Check title from message against title from reference file-->
											<xsl:if test="not(upper-case($CHTitle_message)=upper-case($CHTitle_referencefile))">
												<tr>
													<td style="color:#900000">
														<xsl:text>ERROR: Invalid Title - Title found: </xsl:text>
														<xsl:value-of select="$CHTitle_message"/>
														<xsl:text> Correct Title: '</xsl:text>
														<xsl:value-of select="$CHTitle_referencefile"/>
														<xsl:text>'</xsl:text>
													</td>
												</tr>
											</xsl:if>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:otherwise>
									<!-- Check subHeadings from message against those in reference file-->
									<xsl:if test="$CHTitle_message">
															<tr>
																<td style="color:#008000">
																	<xsl:text>PASSED: Valid Title Used '</xsl:text>
																	<xsl:value-of select="$CHTitle_message"/>
																	<xsl:text>'</xsl:text>
																</td>
															</tr>
										<xsl:for-each select="$CHTitle_message">
											<xsl:if test="name() = 'title'">
												<xsl:variable name="title" select="."/>
												<xsl:variable name="CHSubHeadings_message" select="../*[local-name()='text']//*/*[local-name()='th']"/>
												<xsl:for-each select="$CHSubHeadings_message">
													<xsl:variable name="subheading" select="."/>
													<xsl:variable name="subheading_reference" select="$clinicalHeadingref//template/clinicalheading[@title=$title]/subheading//upper-case(@text)"/>
													<xsl:choose>
														<xsl:when test="not($subheading_reference=upper-case($subheading))">
															<tr>
																<td style="color:#900000">
																	<xsl:text>ERROR: Invalid subheading '</xsl:text>
																	<xsl:value-of select="$subheading"/>
																	<xsl:text>'</xsl:text>
																</td>
															</tr>
														</xsl:when>
														<xsl:otherwise>
															<!--Uncomment block below to write out passes if you need to -->
																														<tr>
																<td style="color:#008000">
																	<xsl:text>PASSED: Valid subheading: '</xsl:text>
																		<xsl:value-of select="$subheading"/>
																	<xsl:text>'</xsl:text>
																</td>
															</tr>
														</xsl:otherwise>
													</xsl:choose>
												</xsl:for-each>
											</xsl:if>
										</xsl:for-each>
									</xsl:if>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
					</xsl:for-each>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<table border="0">
					<tr>
						<td style="color:#900000">
							<xsl:text>ERROR: No Clinical Document Templates Found</xsl:text>
						</td>
					</tr>
				</table>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
