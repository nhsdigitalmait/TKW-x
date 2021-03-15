<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>
	<xsl:include href="../../xslt/MOD11.xslt"/>
	<xsl:template match="/*">
		
		<!-- Common Locations -->
		
		<xsl:for-each select="//*[local-name()='patient']/*[local-name()='id']/@extension">
			<xsl:call-template name="mod11">
				<xsl:with-param name="nhsnumber" select="."/>
				<xsl:with-param name="loc_id" select="'//patient/id/@extension'"/>
				<xsl:with-param name="position" select="position()"/>
			</xsl:call-template>
		</xsl:for-each>
		
		
		<!-- SCR -->
		<xsl:for-each select="//nHSNumber/value/@extension">
			<xsl:call-template name="mod11">
				<xsl:with-param name="nhsnumber" select="."/>
				<xsl:with-param name="loc_id" select="'nHSNumber/value/@extension'"/>
				<xsl:with-param name="position" select="position()"/>
			</xsl:call-template>
		</xsl:for-each>

		<xsl:for-each select="//*[local-name()='person.NHSNumber']/*[local-name()='value']/@extension">
			<xsl:call-template name="mod11">
				<xsl:with-param name="nhsnumber" select="."/>
				<xsl:with-param name="loc_id" select="'//person.NHSNumber/value/@extension'"/>
				<xsl:with-param name="position" select="position()"/>
			</xsl:call-template>
		</xsl:for-each>
		
		<!--PDS-->
		
<!--			<xsl:for-each select="//*[local-name()='component']/*[local-name()='validIdentifier']/*[local-name()='subject']/*[local-name()='patient']/*[local-name()='id']/@extension">
			<xsl:call-template name="mod11">
				<xsl:with-param name="nhsnumber" select="."/>
				<xsl:with-param name="loc_id" select="'//subject/patient/id/@extension'"/>
				<xsl:with-param name="position" select="position()"/>
			</xsl:call-template>
		</xsl:for-each>
		<xsl:for-each select="//*[local-name()='subject']/*[local-name()='patient']/*[local-name()='id'][1]/@extension">
			<xsl:call-template name="mod11">
				<xsl:with-param name="nhsnumber" select="."/>
				<xsl:with-param name="loc_id" select="'//subject/patient/id[1]/@extension'"/>
				<xsl:with-param name="position" select="position()"/>
			</xsl:call-template>
		</xsl:for-each>	
		-->
		
		
		
		<!--CPIS-->
		<xsl:for-each select="//*[local-name()='record']/*[local-name()='CP-ISRecord']/*[local-name()='subject']/*[local-name()='COCT_TP145100GB01.UnbornChild']/*[local-name()='parentMotherPerson']/*[local-name()='playedMother']/*[local-name()='id'][1]/@extension">
			<xsl:call-template name="mod11">
				<xsl:with-param name="nhsnumber" select="."/>
				<xsl:with-param name="loc_id" select="'COCT_TP145100GB01.UnbornChild'"/>
				<xsl:with-param name="position" select="position()"/>
			</xsl:call-template>
		</xsl:for-each>
		<xsl:for-each select="//*[local-name()='record']/*[local-name()='CP-ISRecord']/*[local-name()='subject']//*[local-name()='COCT_TP145101GB01.Child']/*[local-name()='id'][1]/@extension">
			<xsl:call-template name="mod11">
				<xsl:with-param name="nhsnumber" select="."/>
				<xsl:with-param name="loc_id" select="'COCT_TP145101GB01.Child'"/>
				<xsl:with-param name="position" select="position()"/>
			</xsl:call-template>
		</xsl:for-each>		
		
		
	</xsl:template>
</xsl:stylesheet>
