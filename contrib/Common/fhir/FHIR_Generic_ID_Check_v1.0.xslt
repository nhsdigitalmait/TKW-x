<?xml version="1.0" encoding="UTF-8"?>
<!-- 10 Mar 2017 	ChBr 	Extend validation to include "contained resources". -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:fhir="http://hl7.org/fhir">
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<table border="0">
			<xsl:for-each select="//fhir:reference/@value">
				<xsl:if test="(substring(.,1,1) !='#')">
				
					<!-- if NOT a contained reference drop into this validation -->
					<xsl:variable name="resource_name" select="substring-before(.,'/')"/>
					<xsl:variable name="resource_id" select="substring-after(.,'/')"/>
					<xsl:choose>
						<xsl:when test="$resource_name != '' or $resource_id !='' ">
							<xsl:choose>
								<xsl:when test="//fhir:resource/*[local-name()=$resource_name]//fhir:id[@value=$resource_id]/@value">
									<tr>
										<td style="color:#008000">
											<xsl:text>PASS: Reference </xsl:text>
											<xsl:value-of select="."/>
											<xsl:text> in xpath '</xsl:text>
											<xsl:call-template name="plotPath">
												<xsl:with-param name="forNode" select=".."/>
											</xsl:call-template>
											<xsl:text>' the '</xsl:text>
											<xsl:value-of select="$resource_name"/>
											<xsl:text>' resource with id '</xsl:text>
											<xsl:value-of select="$resource_id"/>
											<xsl:text>' exists in the Bundle</xsl:text>
											<br/>
										</td>
									</tr>
								</xsl:when>
								<xsl:otherwise>
									<tr>
										<td style="color:#900000">
											<xsl:text>ERROR: For xpath </xsl:text>
											<xsl:call-template name="plotPath">
												<xsl:with-param name="forNode" select=".."/>
											</xsl:call-template>
											<xsl:text>' the '</xsl:text>
											<xsl:value-of select="$resource_name"/>
											<xsl:text>' resource with id '</xsl:text>
											<xsl:value-of select="$resource_id"/>
											<xsl:text>' does NOT exist in the Bundle</xsl:text>
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
				</xsl:if>
				<xsl:if test="(substring(.,1,1) ='#')">
				
					<!-- if this IS a contained reference drop into this validation -->
					<xsl:variable name="contained_resource" select="substring(.,1,1)"/>
					<xsl:variable name="contained_resource_id" select="substring(.,2)"/>
					<xsl:choose>
						<xsl:when test="$contained_resource_id !='' ">
							<xsl:choose>
								<xsl:when test="( (//fhir:id[@value=$contained_resource_id]/@value) and (lower-case(name(//fhir:id[@value=$contained_resource_id]/@value/../../..))='contained') )">
									<xsl:variable name="contained_resource_name" select="name(//fhir:id[@value=$contained_resource_id]/@value/../..)"/>
									<tr>
										<td style="color:#008000">
											<xsl:text>PASS: Reference </xsl:text>
											<xsl:value-of select="."/>
											<xsl:text> in xpath '</xsl:text>
											<xsl:call-template name="plotPath">
												<xsl:with-param name="forNode" select=".."/>
											</xsl:call-template>
											<xsl:text>' the contained resource ('</xsl:text>
											<xsl:value-of select="$contained_resource"/>
											<xsl:value-of select="$contained_resource_name"/>
											<xsl:text>') with id '</xsl:text>
											<xsl:value-of select="$contained_resource_id"/>
											<xsl:text>' exists in the Bundle</xsl:text>
											<br/>
										</td>
									</tr>
								</xsl:when>
								<xsl:otherwise>
									<tr>
										<td style="color:#900000">
											<xsl:text>ERROR: For xpath </xsl:text>
											<xsl:call-template name="plotPath">
												<xsl:with-param name="forNode" select=".."/>
											</xsl:call-template>
											<xsl:text>' the contained resource ('</xsl:text>
											<xsl:value-of select="$contained_resource"/>
											<xsl:text>') with id '</xsl:text>
											<xsl:value-of select="$contained_resource_id"/>
											<xsl:text>' does NOT exist in the Bundle</xsl:text>
										</td>
									</tr>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR: The @value '</xsl:text>
									<xsl:text>' in xpath </xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select=".."/>
									</xsl:call-template>
									<xsl:text> has the contained resource prefix # and no subsequent id</xsl:text>
								</td>
							</tr>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
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
