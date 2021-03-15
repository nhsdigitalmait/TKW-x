<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!--<xsl:output method="text" version="1.0" encoding="UTF-8" indent="yes"/>-->
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>
	<xsl:variable name="vDigits" select="'0123456789'"/>


	<xsl:template name="mod11">
		<xsl:param name="nhsnumber"/>
		<xsl:param name="loc_id"/>
		<xsl:param name="position"/>
		<xsl:param name="pN1" select="substring($nhsnumber,1,1)"/>
		<xsl:param name="pN2" select="substring($nhsnumber,2,1)"/>
		<xsl:param name="pN3" select="substring($nhsnumber,3,1)"/>
		<xsl:param name="pN4" select="substring($nhsnumber,4,1)"/>
		<xsl:param name="pN5" select="substring($nhsnumber,5,1)"/>
		<xsl:param name="pN6" select="substring($nhsnumber,6,1)"/>
		<xsl:param name="pN7" select="substring($nhsnumber,7,1)"/>
		<xsl:param name="pN8" select="substring($nhsnumber,8,1)"/>
		<xsl:param name="pN9" select="substring($nhsnumber,9,1)"/>
		<xsl:param name="pN10" select="substring($nhsnumber,10,1)"/>
		<xsl:param name="Product" select="($pN1*10)+($pN2*9)+($pN3*8)+($pN4*7)+($pN5*6)+($pN6*5)+($pN7*4)+($pN8*3)+($pN9*2)"/>
		<xsl:param name="ActualMod11" select="11-($Product mod 11)"/>

		<xsl:choose>
			<xsl:when test="0 = string-length(translate($nhsnumber,$vDigits,''))">
				<xsl:if test="$ActualMod11 = 11">
					<br/>	
					<xsl:if test="$pN10 != 0">
						<xsl:text>ERROR The NHS Number used in occurrence </xsl:text>
						<xsl:value-of select="$position"/>
						<xsl:text> of '</xsl:text>
						<xsl:value-of select="$loc_id"/>
						<xsl:text>' has an incorrect Mod11 value. For number: </xsl:text>
						<xsl:value-of select="$nhsnumber"/>
						<xsl:text> the last digit should be:</xsl:text>
						<xsl:value-of select="0"/>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$ActualMod11 = 10">
					<xsl:if test="$pN10 != 1">
						<xsl:text>ERROR The NHS Number used in occurrence </xsl:text>
						<xsl:value-of select="$position"/>
						<xsl:text> of '</xsl:text>
						<xsl:value-of select="$loc_id"/>
						<xsl:text>' has an incorrect Mod11 value. For number: </xsl:text>
						<xsl:value-of select="$nhsnumber"/>
						<xsl:text> the last digit should be:</xsl:text>
						<xsl:value-of select="1"/>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$ActualMod11 &lt;10">
					<xsl:if test="$pN10 != $ActualMod11">
						<xsl:text>ERROR The NHS Number used in occurrence </xsl:text>
						<xsl:value-of select="$position"/>
						<xsl:text> of '</xsl:text>
						<xsl:value-of select="$loc_id"/>
						<xsl:text>' has an incorrect Mod11 value. For number: </xsl:text>
						<xsl:value-of select="$nhsnumber"/>
						<xsl:text> the last digit should be:</xsl:text>
						<xsl:value-of select="$ActualMod11"/>
					</xsl:if>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<br/>
				<xsl:text>ERROR The NHS Number used in occurrence </xsl:text>
				<xsl:value-of select="$position"/>
				<xsl:text> of '</xsl:text>
				<xsl:value-of select="$loc_id"/>
				<xsl:text>' contains non numeric values. Mod11 check can not be executed.</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
